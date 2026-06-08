using System.Windows.Forms;

namespace SimuladorTelefoniaDesigner;

internal static class Program
{
    [STAThread]
    private static void Main()
    {
        ApplicationConfiguration.Initialize();
        Application.Run(new MainForm());
    }
}
