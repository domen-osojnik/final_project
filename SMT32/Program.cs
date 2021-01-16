using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Topshelf;

namespace USBListenerService
{
    class Program
    {
        public static void Main()
        {
            var rc = HostFactory.Run(x => 
            {
                x.Service<USBListenerService>(s => 
                {
                    s.ConstructUsing(name => new USBListenerService());
                    s.WhenStarted(tc => tc.Start());
                    s.WhenStopped(tc => tc.Stop());
                });
                x.RunAsLocalSystem();

                x.SetDescription("STM 32 USB Listener Service");
                x.SetDisplayName("STM 32 USB Listener Service");
                x.SetServiceName("STM32USBListenerService");
            });

            var exitCode = (int)Convert.ChangeType(rc, rc.GetTypeCode()); 
            Environment.ExitCode = exitCode;
        }
    }
}
