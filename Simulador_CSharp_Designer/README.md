# Simulador CSharp Designer

Este proyecto es una version independiente del simulador C# hecha como Windows Forms App para .NET 8.0.

Abrir en Visual Studio:

1. Abrir el archivo `SimuladorTelefoniaDesigner.sln`.
2. En el Explorador de soluciones, abrir `MainForm.cs`.
3. Usar clic derecho y elegir `Ver disenador`.

Importante: no abrir la carpeta con `Abrir carpeta` y no abrir `MainForm.cs` como archivo suelto. El disenador de Windows Forms solo aparece cuando Visual Studio carga el proyecto o la solucion.

Ejecutar por consola:

```powershell
dotnet run --project SimuladorTelefoniaDesigner.csproj
```

La interfaz esta declarada en `MainForm.Designer.cs` y la logica de negocio queda en `MainForm.cs`.

Flujo implementado:

- Dos telefonos simulados en pestañas independientes.
- Consulta de saldo al identificador Python desde cada telefono.
- Solicitud de llamada al identificador Python desde cada telefono.
- Inicio de llamada desde cada telefono.
- Finalizacion de llamada manual o automatica cuando se agota el tiempo autorizado.
- Envio opcional de telefono, identificador del telefono e identificador de tarjeta con AES.
- Visualizacion de la trama enviada y la respuesta recibida.
- Cronometros independientes para probar llamadas simultaneas.

Configuracion por defecto:

- Host Python: `localhost`
- Puerto Python: `8000`
- Telefono 1: `88889999`
- Identificador telefono 1: `1234567890123456`
- Identificador tarjeta 1: `1234567890123456789`
- Telefono 2: `25743715`
- Identificador telefono 2: `3333333333333333`
- Identificador tarjeta 2: `3333333333333333333`
- Ubicacion de ambos telefonos: `9.935,-84.091`

Prueba sugerida para SIM1:

1. Ejecutar el servidor Python del identificador.
2. Ejecutar el proveedor Java.
3. Abrir este simulador.
4. En `Telefono 1`, marcar un destino y presionar `Llamar`.
5. Cambiar a `Telefono 2`, marcar otro destino y presionar `Llamar`.
6. Verificar que ambos cronometros puedan avanzar de forma independiente.
7. Presionar `Colgar` en cada telefono o esperar a que se agote el tiempo maximo.
