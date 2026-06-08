using System.Text.Json;

namespace SimuladorTelefoniaDesigner;

internal static class JsonElementExtensions
{
    public static string GetPropertyOrDefault(this JsonElement element, string name, string defaultValue)
    {
        return element.TryGetProperty(name, out var value) ? value.GetString() ?? defaultValue : defaultValue;
    }
}
