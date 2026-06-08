# Identificador Python integrado con Proveedor Java

## Puertos

- Identificador Python: `localhost:8000`
- Proveedor Java: `localhost:6000`

## Orden para probar

1. Ejecutar SQL Server y el proveedor Java.
2. Verificar que Java muestre: `Servidor iniciado en puerto 6000`.
3. Ejecutar el script `script_mysql_identificador.sql` en MySQL.
4. Ejecutar:

```bash
python Capa_Presentacion/ServidorIdentificador.py
```

5. Ejecutar el simulador C#:

```bash
cd ../Simulador_CSharp
dotnet run
```

El simulador ya trae precargados los datos de prueba del script MySQL y envia con
AES los campos sensibles: `telefono`, `identificadorTelefono` e
`identificadorTarjeta`.

## Trama de consulta de saldo hacia Python

```json
{
  "telefono": "88889999",
  "identificadorTelefono": "1234567890123456",
  "identificadorTarjeta": "1234567890123456789",
  "ubicacion": "9.935,-84.091",
  "tipoTransaccion": "saldo"
}
```

## Trama de solicitud de llamada hacia Python

```json
{
  "telefono": "88889999",
  "identificadorTelefono": "1234567890123456",
  "identificadorTarjeta": "1234567890123456789",
  "ubicacion": "9.935,-84.091",
  "tipoTransaccion": "solicitud",
  "telefonoDestino": "4915112345678"
}
```

## Trama de inicio de llamada hacia Python

Luego de una solicitud autorizada, el simulador envia:

```json
{
  "telefono": "88889999",
  "identificadorTelefono": "1234567890123456",
  "identificadorTarjeta": "1234567890123456789",
  "ubicacion": "9.935,-84.091",
  "tipoTransaccion": "llamada",
  "telefonoDestino": "4915112345678",
  "tiempoMaximo": "001025",
  "tarifa": "0000000054"
}
```

## Trama de finalizacion de llamada hacia Python

```json
{
  "telefono": "88889999",
  "identificadorTelefono": "1234567890123456",
  "identificadorTarjeta": "1234567890123456789",
  "ubicacion": "9.935,-84.091",
  "tipoTransaccion": "finalizacion",
  "telefonoDestino": "4915112345678"
}
```

El campo `tipoLlamada` no es obligatorio desde el simulador. El Identificador lo
calcula antes de consultar al Proveedor Java:

- `1`: llamada nacional al mismo proveedor.
- `2`: llamada nacional a otro proveedor.
- `3`: llamada internacional.

Para llamadas nacionales, si el destino viene con prefijo `506`, el Identificador
lo normaliza a 8 digitos antes de enviarlo al Proveedor Java.

## AES

El archivo `Capa_Negocio/CifradorAES.py` usa `pycryptodome`.

Instalacion:

```bash
pip install pycryptodome
```

Por ahora el servidor recibe los campos sin cifrar para facilitar la integracion inicial.
Cuando el simulador C# envie AES, se descifran `telefono`, `identificadorTelefono` e
`identificadorTarjeta` antes de validar.
