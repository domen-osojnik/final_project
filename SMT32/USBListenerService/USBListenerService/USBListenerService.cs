using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using System.Configuration;
using System.IO.Ports;
using System.Threading;
using Microsoft.Win32;
using System.Management;

namespace USBListenerService
{


    class USBListenerService
    {
        // const
        private const int BUFFSIZE = 100;

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
        public bool InitClient ()
        {
            client = new HttpClient();
            return true;
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
        public void Listen ()
        {
            // Init
            port.PortName = getDeviceCOMWhenAvailable();
            port.Open();

            while (true)
            {
                // Block while no new messages
                try
                {
                    while (port.BytesToRead == 0) ;

                    // Read recieved message
                    port.Read(buf, 0, MessageLength);
                    string charsStr = new string(buf);
                    Console.WriteLine(charsStr);
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
        public void Start ()
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
        public void Stop ()
        {

        }


    }
}
