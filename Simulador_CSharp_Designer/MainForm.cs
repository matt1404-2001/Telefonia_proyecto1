using System.ComponentModel;
using System.Diagnostics;
using System.Text.Json;
using System.Windows.Forms;

namespace SimuladorTelefoniaDesigner;

public partial class MainForm : Form
{
    private readonly IdentificadorClient client = new();
    private readonly PhoneRuntime phone1 = new();
    private readonly PhoneRuntime phone2 = new();

    public MainForm()
    {
        InitializeComponent();

        if (LicenseManager.UsageMode != LicenseUsageMode.Designtime)
        {
            uiTimer.Start();
        }
    }

    private void Phone1_Digit_Click(object sender, EventArgs e)
    {
        AppendDigit(1, sender);
    }

    private void Phone2_Digit_Click(object sender, EventArgs e)
    {
        AppendDigit(2, sender);
    }

    private void Phone1_Backspace_Click(object sender, EventArgs e)
    {
        Backspace(1);
    }

    private void Phone2_Backspace_Click(object sender, EventArgs e)
    {
        Backspace(2);
    }

    private void Phone1_Clear_Click(object sender, EventArgs e)
    {
        ClearDial(1);
    }

    private void Phone2_Clear_Click(object sender, EventArgs e)
    {
        ClearDial(2);
    }

    private async void Phone1_Call_Click(object sender, EventArgs e)
    {
        await RequestCallOrBalanceAsync(1);
    }

    private async void Phone2_Call_Click(object sender, EventArgs e)
    {
        await RequestCallOrBalanceAsync(2);
    }

    private async void Phone1_Hang_Click(object sender, EventArgs e)
    {
        await FinishActiveCallAsync(1, "Llamada finalizada.");
    }

    private async void Phone2_Hang_Click(object sender, EventArgs e)
    {
        await FinishActiveCallAsync(2, "Llamada finalizada.");
    }

    private async void Phone1_Balance_Click(object sender, EventArgs e)
    {
        await QueryBalanceAsync(1);
    }

    private async void Phone2_Balance_Click(object sender, EventArgs e)
    {
        await QueryBalanceAsync(2);
    }

    private async void btnTestConnection_Click(object sender, EventArgs e)
    {
        await QueryBalanceAsync(1);
    }

    private void uiTimer_Tick(object sender, EventArgs e)
    {
        lblClock1.Text = DateTime.Now.ToString("HH:mm:ss");
        lblClock2.Text = DateTime.Now.ToString("HH:mm:ss");
        UpdateRuntimeView(1);
        UpdateRuntimeView(2);
    }

    private void AppendDigit(int phoneNumber, object sender)
    {
        if (sender is Button button)
        {
            var runtime = GetRuntime(phoneNumber);
            runtime.DialedNumber += button.Text;
            GetControls(phoneNumber).Display.Text = runtime.DialedNumber;
        }
    }

    private void Backspace(int phoneNumber)
    {
        var runtime = GetRuntime(phoneNumber);
        if (runtime.DialedNumber.Length == 0)
        {
            return;
        }

        runtime.DialedNumber = runtime.DialedNumber[..^1];
        GetControls(phoneNumber).Display.Text = runtime.DialedNumber;
    }

    private void ClearDial(int phoneNumber)
    {
        var runtime = GetRuntime(phoneNumber);
        runtime.DialedNumber = "";
        GetControls(phoneNumber).Display.Text = "";
    }

    private async Task RequestCallOrBalanceAsync(int phoneNumber)
    {
        var runtime = GetRuntime(phoneNumber);
        var controls = GetControls(phoneNumber);

        if (runtime.DialedNumber == "#9090*")
        {
            await QueryBalanceAsync(phoneNumber);
            return;
        }

        if (string.IsNullOrWhiteSpace(runtime.DialedNumber))
        {
            controls.Status.Text = "Marca un numero destino.";
            return;
        }

        var request = BuildBasePayload("solicitud", controls);
        request["telefonoDestino"] = runtime.DialedNumber;

        await SendAndHandleAsync(request, response =>
        {
            if (IsOk(response))
            {
                runtime.AuthorizedDestination = runtime.DialedNumber;
                runtime.AuthorizedTime = response.GetPropertyOrDefault("tiempo", "000000");
                runtime.AuthorizedTariff = response.GetPropertyOrDefault("tarifa", "0000000000");

                controls.Destination.Text = runtime.AuthorizedDestination;
                controls.Time.Text = FormatTime(runtime.AuthorizedTime);
                controls.Tariff.Text = FormatTariff(runtime.AuthorizedTariff);
                controls.Status.Text = $"Autorizada por {FormatTime(runtime.AuthorizedTime)}. Iniciando llamada.";
                _ = StartAuthorizedCallAsync(phoneNumber);
            }
            else
            {
                controls.Status.Text = FriendlyStatus(response);
            }
        }, controls.Status);
    }

    private async Task StartAuthorizedCallAsync(int phoneNumber)
    {
        var runtime = GetRuntime(phoneNumber);
        var controls = GetControls(phoneNumber);

        if (string.IsNullOrWhiteSpace(runtime.AuthorizedDestination))
        {
            controls.Status.Text = "Autoriza una llamada primero.";
            return;
        }

        var request = BuildBasePayload("llamada", controls);
        request["telefonoDestino"] = runtime.AuthorizedDestination;
        request["tiempoMaximo"] = runtime.AuthorizedTime;
        request["tarifa"] = runtime.AuthorizedTariff;

        await SendAndHandleAsync(request, response =>
        {
            if (IsOk(response))
            {
                runtime.ActiveCall = true;
                runtime.Finishing = false;
                runtime.Watch.Restart();
                controls.Status.Text = "Llamada activa.";
                controls.CallButton.Enabled = false;
                controls.HangButton.Enabled = true;
            }
            else
            {
                controls.Status.Text = FriendlyStatus(response);
            }
        }, controls.Status);
    }

    private async Task FinishActiveCallAsync(int phoneNumber, string message)
    {
        var runtime = GetRuntime(phoneNumber);
        var controls = GetControls(phoneNumber);

        if (!runtime.ActiveCall && string.IsNullOrWhiteSpace(runtime.AuthorizedDestination))
        {
            controls.Status.Text = "No hay llamada activa.";
            return;
        }

        if (runtime.Finishing)
        {
            return;
        }

        runtime.Finishing = true;

        var request = BuildBasePayload("finalizacion", controls);
        request["telefonoDestino"] = runtime.AuthorizedDestination;

        await SendAndHandleAsync(request, response =>
        {
            if (IsOk(response))
            {
                var elapsed = runtime.Watch.Elapsed;
                runtime.ActiveCall = false;
                runtime.Finishing = false;
                runtime.Watch.Stop();
                runtime.AuthorizedDestination = "";
                runtime.AuthorizedTime = "000000";
                runtime.AuthorizedTariff = "0000000000";
                controls.CallButton.Enabled = true;
                controls.HangButton.Enabled = true;
                controls.Status.Text = $"{message} Duracion: {elapsed:mm\\:ss}.";
            }
            else
            {
                runtime.Finishing = false;
                controls.Status.Text = FriendlyStatus(response);
            }
        }, controls.Status);
    }

    private async Task QueryBalanceAsync(int phoneNumber)
    {
        var controls = GetControls(phoneNumber);
        var request = BuildBasePayload("saldo", controls);

        await SendAndHandleAsync(request, response =>
        {
            controls.Status.Text = response.TryGetProperty("saldo", out var saldo)
                ? $"Saldo disponible: {FormatBalance(saldo.GetString() ?? "0")}"
                : FriendlyStatus(response);
        }, controls.Status);
    }

    private async Task SendAndHandleAsync(Dictionary<string, object> request, Action<JsonElement> onSuccess, Label statusLabel)
    {
        SyncConnectionSettings();
        var json = JsonSerializer.Serialize(request, new JsonSerializerOptions { WriteIndented = true });
        txtRaw.Text = json;

        try
        {
            var responseJson = await client.SendAsync(request);
            var response = JsonSerializer.Deserialize<JsonElement>(responseJson);
            txtResponse.Text = JsonSerializer.Serialize(response, new JsonSerializerOptions { WriteIndented = true });
            onSuccess(response);
        }
        catch (Exception ex)
        {
            txtResponse.Text = ex.Message;
            statusLabel.Text = "No se pudo conectar con el Identificador Python.";
        }
    }

    private Dictionary<string, object> BuildBasePayload(string tipo, PhoneControls controls)
    {
        var payload = new Dictionary<string, object>
        {
            ["telefono"] = EncodeSensitive(controls.Phone.Text.Trim()),
            ["identificadorTelefono"] = EncodeSensitive(controls.DeviceId.Text.Trim()),
            ["identificadorTarjeta"] = EncodeSensitive(controls.CardId.Text.Trim()),
            ["ubicacion"] = controls.Location.Text.Trim(),
            ["tipoTransaccion"] = tipo
        };

        var coordinates = controls.Location.Text.Split(',', StringSplitOptions.TrimEntries);
        if (coordinates.Length == 2)
        {
            payload["latitud"] = coordinates[0];
            payload["longitud"] = coordinates[1];
        }

        return payload;
    }

    private void UpdateRuntimeView(int phoneNumber)
    {
        var runtime = GetRuntime(phoneNumber);
        var controls = GetControls(phoneNumber);
        controls.Timer.Text = runtime.ActiveCall ? runtime.Watch.Elapsed.ToString(@"mm\:ss") : "00:00";

        if (runtime.ActiveCall && !runtime.Finishing && runtime.Watch.Elapsed >= ParseTime(runtime.AuthorizedTime))
        {
            _ = FinishActiveCallAsync(phoneNumber, "Tiempo maximo agotado.");
        }
    }

    private PhoneRuntime GetRuntime(int phoneNumber)
    {
        return phoneNumber == 1 ? phone1 : phone2;
    }

    private PhoneControls GetControls(int phoneNumber)
    {
        return phoneNumber == 1
            ? new PhoneControls(txtPhone1, txtDeviceId1, txtCardId1, txtLocation1, txtDisplay1, lblStatus1, lblTimer1, txtDestination1, txtTime1, txtTariff1, btnCall1, btnHang1)
            : new PhoneControls(txtPhone2, txtDeviceId2, txtCardId2, txtLocation2, txtDisplay2, lblStatus2, lblTimer2, txtDestination2, txtTime2, txtTariff2, btnCall2, btnHang2);
    }

    private string EncodeSensitive(string value)
    {
        return chkEncrypt.Checked ? AesCodec.Encrypt(value) : value;
    }

    private void SyncConnectionSettings()
    {
        client.Host = txtHost.Text.Trim();
        client.Port = (int)numPort.Value;
    }

    private static bool IsOk(JsonElement response)
    {
        return response.TryGetProperty("status", out var status) && status.GetString() == "OK";
    }

    private static string FriendlyStatus(JsonElement response)
    {
        if (response.TryGetProperty("status", out var status))
        {
            var value = status.GetString();
            if (value == "INSUF")
            {
                return "Saldo insuficiente.";
            }

            if (value == "ERROR" && response.TryGetProperty("motivo", out var reason))
            {
                return ReasonText(reason.GetInt32());
            }

            return value ?? "Respuesta recibida.";
        }

        return "Respuesta recibida.";
    }

    private static string ReasonText(int reason)
    {
        return reason switch
        {
            1 => "Telefono destino invalido.",
            2 => "Datos de tarjeta no coinciden.",
            3 => "Llamada no permitida por ubicacion fuera del pais.",
            4 => "Accion invalida.",
            5 => "Codigo de pais invalido o error no controlado.",
            _ => $"Error, motivo {reason}."
        };
    }

    private static TimeSpan ParseTime(string value)
    {
        var text = value.PadLeft(6, '0')[^6..];
        return new TimeSpan(int.Parse(text[..2]), int.Parse(text.Substring(2, 2)), int.Parse(text.Substring(4, 2)));
    }

    private static string FormatTime(string value)
    {
        var text = value.PadLeft(6, '0')[^6..];
        return $"{text[..2]}:{text.Substring(2, 2)}:{text.Substring(4, 2)}";
    }

    private static string FormatTariff(string value)
    {
        var padded = value.PadLeft(3, '0');
        var integerPart = padded[..^2].TrimStart('0');
        if (integerPart.Length == 0)
        {
            integerPart = "0";
        }

        return $"{integerPart}.{padded[^2..]}";
    }

    private static string FormatBalance(string value)
    {
        if (value == "-1")
        {
            return "Postpago sin limite";
        }

        return FormatTariff(value);
    }

    private sealed class PhoneRuntime
    {
        public Stopwatch Watch { get; } = new();
        public string DialedNumber { get; set; } = "";
        public string AuthorizedDestination { get; set; } = "";
        public string AuthorizedTime { get; set; } = "000000";
        public string AuthorizedTariff { get; set; } = "0000000000";
        public bool ActiveCall { get; set; }
        public bool Finishing { get; set; }
    }

    private sealed record PhoneControls(
        TextBox Phone,
        TextBox DeviceId,
        TextBox CardId,
        TextBox Location,
        TextBox Display,
        Label Status,
        Label Timer,
        TextBox Destination,
        TextBox Time,
        TextBox Tariff,
        Button CallButton,
        Button HangButton);
}
