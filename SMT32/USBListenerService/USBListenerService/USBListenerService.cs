using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Http;
using System.Configuration;
using System.IO.Ports;


namespace USBListenerService
{
    class USBListenerService
    {

        private HttpClient client;
        private string url;
        private string path;

        public void InitClient()
        {
            client = new HttpClient();
           
        }

   
        public void Start ()
        {

            char[] buf = new char[100];
            SerialPort port = new SerialPort();
            port.PortName = "COM3";
            port.Open();


            string charsStr;

            // byte buffer[100];

            while (true)
            {
                while (port.BytesToRead == 0);
                port.Read(buf, 0, 27);
                charsStr = new string(buf);
                Console.WriteLine(charsStr);

            }



            /*
            string readData = "";
            do {
                readData = port.ReadLine();
            } while (readData == "");
            
            Console.WriteLine(readData);
            */


            /*
            using (var sp = new System.IO.Ports.SerialPort("COM3", 115200, System.IO.Ports.Parity.None, 8, System.IO.Ports.StopBits.One))
            {
                sp.Open();

   

                var readData = sp.ReadLine();
                Console.WriteLine(readData);
            }
            */

            // Kako najti usb porte: 
            /*
            string[] ports = SerialPort.GetPortNames();
            Console.WriteLine("The following serial ports were found:");
            foreach (string port in ports)
            {
                Console.WriteLine(port);
            }
            */



            /*
            try
            {
                url = ConfigurationManager.AppSettings["url"];
                path = ConfigurationManager.AppSettings["path"];
                InitClient();
            } catch (Exception ex)
            {

            }
            */

        }

        public void Stop ()
        {

        }
    }
}
