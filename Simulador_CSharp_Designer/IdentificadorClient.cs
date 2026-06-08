using System.Net.Sockets;
using System.Text;
using System.Text.Json;

namespace SimuladorTelefoniaDesigner;

internal sealed class IdentificadorClient
{
    public string Host { get; set; } = "localhost";
    public int Port { get; set; } = 8000;

    public async Task<string> SendAsync(Dictionary<string, object> payload)
    {
        using var tcp = new TcpClient();
        await tcp.ConnectAsync(Host, Port);

        var stream = tcp.GetStream();
        var request = Encoding.UTF8.GetBytes(JsonSerializer.Serialize(payload));
        await stream.WriteAsync(request);
        await stream.FlushAsync();

        var buffer = new byte[8192];
        var read = await stream.ReadAsync(buffer);
        if (read == 0)
        {
            throw new IOException("El servidor cerro la conexion sin responder.");
        }

        return Encoding.UTF8.GetString(buffer, 0, read);
    }
}
