using System;
using System.Net.Http;
using System.Configuration;
using System.IO.Ports;
using System.Threading;
using Newtonsoft.Json;
using System.Text.RegularExpressions;
using System.Text;

namespace USBListenerService
{
    class USBListenerService
    {
        private static readonly Regex rx = new Regex(@"N|W|S|E|NW|NE|SE|SW");
        private static readonly Random random = new Random();
        private static readonly string uuid = "1234567890abcdefghijklmnopqrstuvwxyz";
        private static readonly int BUFFSIZE = 100;

        // App.config
        private readonly string ProductString = ConfigurationManager.AppSettings["ProductString"];
        private readonly string Path = ConfigurationManager.AppSettings["Path"];
        private readonly string Url = ConfigurationManager.AppSettings["Url"];
        private readonly int MessageLength = Int32.Parse(ConfigurationManager.AppSettings["MessageLength"]);

        // Globs
        private Thread listenerThread;
        private HttpClient client;
        private SerialPort port = new SerialPort();
        private char[] buf = new char[BUFFSIZE];
        private string json;
        private StringContent data;


        // Class packet
        internal class Packet
        {
            public string imu_id { get; set; }
            public string date { get; set; }
            public string direction { get; set; }

            public void SwitchId()
            {
                this.imu_id = "";
                for (int i = 0; i < uuid.Length; i++)
                {
                    this.imu_id += uuid[random.Next(0, uuid.Length - 1)];
                }
            }

            public void SetDirection(char one, char two)
            {
                this.direction = "";
                this.direction = (two == '0') ? $"{one} + {two}" : $"{one}";
            }

            public bool isValid()
            {
                return rx.IsMatch(this.direction);
            }
        }


        private Packet packet = new Packet();


        // Print message on success or failure
        public void PrintMessage(string message, bool success)
        {
            if (success)
            {
                Console.ForegroundColor = ConsoleColor.Green;
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Red;
            }
            Console.WriteLine(message);
            Console.ForegroundColor = ConsoleColor.White;
        }

        // Find and return device com from all connected devices list
        public string getDeviceCOM()
        {
            foreach (var device in Win32DeviceMgmt.GetAllCOMPorts())
            {
                if (device.bus_description == ProductString)
                {
                    return device.name;
                }
            }
            return "";
        }

        // Initialize client 
        public bool InitClient()
        {
            try
            {
                client = new HttpClient();
                PrintMessage("Client initialized", true);
                return true;
            }
            catch (Exception ex)
            {
                PrintMessage("Failed to initialize client", false);
                return false;
            }

        }

        // Block main thread until an STM device connects
        public string getDeviceCOMWhenAvailable()
        {
            string device = "";
            while (true)
            {
                device = getDeviceCOM();
                if (device != "")
                {
                    packet.SwitchId();
                    PrintMessage($"Device Connected On {device}", true);
                    return device;
                }
                else
                {
                    // Wait 5 sec
                    Thread.Sleep(5000);
                }
            }
        }

        // Listen for Messages of given COM
        public void Listen()
        {
            // Init
            port.PortName = getDeviceCOMWhenAvailable();

            try
            {
                port.Open();
            }
            catch (Exception ex)
            {
                PrintMessage("Device not functioning. Restart device. Restart service.", false);
                Stop();
            }

            while (true)
            {

                try
                {
                    // Block while no new messages
                    while (port.IsOpen && port.BytesToRead == 0) ;

                    // Read && Send message
                    port.Read(buf, 0, MessageLength);
                    if (buf[0] == 'A' && buf[1] == 'B')
                    {
                        packet.date = DateTime.Now.ToString("yyyy-mm-ddTHH:mm:ss");
                        packet.SetDirection(buf[2], buf[3]);

                        if (packet.isValid())
                        {
                            json = JsonConvert.SerializeObject(packet);
                            Console.WriteLine($"SENT: {json}");
                            data = new StringContent(json, Encoding.UTF8, "application/json");
                            //client.PostAsync(Url, data);
                        }
                    }

                }
                catch (System.IO.IOException ex)
                {
                    PrintMessage("Device Disconnected", false);
                    port.Close();
                    port.PortName = getDeviceCOMWhenAvailable();
                    port.Open();
                    continue;
                }
            }

        }

        // Starting point 
        public void Start()
        {
            if (InitClient())
            {
                // Start Thread
                listenerThread = new Thread(Listen);
                listenerThread.IsBackground = true;
                listenerThread.Start();
            }
            else
            {
                Stop();
            }
        }

        // Exit point
        public void Stop()
        {
            client.Dispose();
            listenerThread.Abort();
        }


    }
}
