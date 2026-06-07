Colegio Universitario de Cartago

Primer Proyecto Programado

Programación IV

Sistema de Control de Llamadas telefónicas

La empresa telefónica Central General, requiere de un nuevo sistema que facilite tanto

la  identificación  como  el  acceso  a  la  información  para  el  cobro  de  las  llamadas

telefónicas.

Este sistema está formado por varios componentes que se interconectan entre sí, de

tal forma que su interacción permite de manera fluida que se verifiquen los datos sin

alterar las operaciones en las llamadas de sus clientes.

Durante  el  desarrollo  de  este  proyecto,  su  equipo  de  trabajo  construirá  diferentes

componentes del sistema, entregando finalmente en tres iteraciones el producto final.

Primer alcance - Sistema base.

Para la primera parte del proyecto se debe trabajar en los siguientes elementos:

1. Base de datos del sistema identificador (modelo relacional en MySQL).
2. Base de datos del proveedor telefónico XYZ (Modelo relacional en SQL Server).
3. Software Identificador de teléfonos.
4. Componente del Proveedor telefónico XYZ.
5. Simulador de llamadass.

La idea es que los modelos de base de datos propuestos para cada uno de los sistemas

mencionados incorporen los diferentes elementos que atiendan exclusivamente las

historias  de  usuario  a  atender.  Los  datos  que  se  agreguen  a  estas  bases  de  datos

inicialmente se incorporarán mediante scripts.

Página 1

II Cuatrimestre, 2025



A continuación, se detallas las historias de usuario y los criterios de aceptación que

las acompañan.

Programación IV

ID

Rol

PROVEEDOR1

Dueño del sistema

Objetivo

Verificar el saldo telefónico

Para

Autorizar inicio de llamadas

Criterios de aceptación

1. El Proveedor debe recibir una trama en formato de texto plano proveniente del

Identificador que contendrá los datos que se requieren para verificar si el número

de teléfono tiene los fondos suficientes para realizar  una llamada o bien, para

consultar su saldo disponible.

2. La trama debe incluir:

Tipo de transacción (ocupa un espacio)  1: Llamada

2: Consulta

Número de teléfono

Ejemplo: 25743715

Tipo de llamada

1. Mismo proveedor
2. Otro proveedor
3. Fuera del país
4. Al recibir los datos de la trama, si el tipo de transacción es llamada entonces:

a.  Debe  verificar  si  es  un  servicio  de  telefonía  postpago  la  llamada  es

autorizada sin validaciones. Pero si es un tipo de servicio prepago debe

verificar la existencia de saldo suficiente para poder mantener un minuto

de conversación.

•  Para este cálculo se requiere del dato tipo de llamada, si es internacional

las tarifas oscilan entre $0,14 (Grupo C1, por ejemplo, Belice, Guatemala,

Honduras, El Salvador y Panamá) y $0,31 (Grupo B, como Suramérica y el

Caribe,  excluyendo  Cuba). También  hay  grupos  más  costosos  como  el

Página 2

II Cuatrimestre, 2025



Programación IV

Grupo  D  (Europa,  Singapur,  Tailandia,  Australia  y  Nueva  Zelanda)  con

$0,54  por  minuto. Las  llamadas  a  Cuba  y  el  resto  del  mundo  pueden

costar $1,03 por minuto.

•  Si  la  llamada  es  local  sin  importar  si  es  mismo  proveedor  o  distinto,

depende de:

o  Llamadas a Fijos: El costo por minuto excedente es de ₡8,72.

o  Llamadas a Móviles: El costo por minuto excedente es de ₡25,13.

•  Sii la llamada es al mismo proveedor, puede utilizar bonos o saldos.

b.  Si hay fondos suficientes o si es postpago se debe responder “OK” y

debe  indicar  la  tarifa  por  minuto  que  cancelará  usando  el  siguiente

formato  10  espacios,  incluyendo  2  decimales,  sin  punto  decimal

asumiendo  los  últimos  dos  dígitos  como  decimales,  en  el  caso  del

saldo  para  un  servicio  postpago  se  enviaría  el  número  9999999999.

También debe indicar la cantidad de horas, minutos y segundos que

tiene  autorizados  según  el  saldo  disponible  con  un  formato  de    6

dígitos  sin  los  dos  puntos  (:),  donde  los  primeros  dos  dígitos

corresponden a la hora, los siguientes dos dígitos a los minutos y los

últimos dos dígitos a los segundos. Ejemplo: 001025. En el caso de un

servicio postpago el saldo debe ser 245959.

c.  Si no hay fondos suficientes se debe responder “INSUF”.

d.  Cualquier otra situación se debe responder “ERROR”.

4. Si el tipo de transacción es consulta entonces:

a.  Si el servicio es de postpago se responde “OK” y el monto va en -1. En caso

de ser prepago se debe responder “OK” junto con el saldo disponible para

llamadas  asociado  al  número,  el  saldo  se  debe  representar  en  19

espacios, incluyendo decimales, no es necesario incluir el punto decimal,

se asume que los dos últimos dígitos son los decimales. Para completar

los  19  espacios  se  debe  rellenar  con  ceros  a  la  izquierda.  Ejemplo:

000000000009765425\.

Página 3

II Cuatrimestre, 2025



b.  Cualquier otra situación se debe responder “ERROR”.

Programación IV

ID

Rol

Proveedor2

Dueño del sistema

Objetivo

Registrar movimientos

Para

Registrar el cobro y rebajo de saldos

Criterios de aceptación

1. El Proveedor debe recibir una trama en formato de texto plano proveniente del

autorizador que contendrá los datos que se requieren para registrar los  cobros

(postpago) o los rebajos a saldos (prepago).

2. La trama debe incluir:

Tipo de transacción (ocupa un espacio)  1: Rebajo de saldo

Número de teléfono

Ejemplo: 25262020

Fecha  de

la

llamada,  usando  un

Ejemplo: 20250528

formado de año, mes y día

Hora de la llamada, usando un formato

Ejemplo: 101025

de  6  dígitos,  los  dos  primeros  para  la

hora (0 a 24), otros dos para los minutos,

y los últimos dos para los segundos, sin

usar los dos puntos.

Teléfono destino

Costo de la llamada

El  monto  ocupará

8

espacios,

incluyendo dos decimales, siempre los

últimos dos dígitos son decimales.

Ejemplo: 89154242

Ejemplo: 15000000

Duración,  la  duración  debe  indicar  el

Ejemplo: 001025

tiempo  de

la

llamada  usando  un

formato  de  6  dígitos,  los  dos  primeros

para la hora (0 a 24), otros dos para los

Página 4

II Cuatrimestre, 2025



Programación IV

minutos,  y  los  últimos  dos  para  los

segundos, sin usar los dos puntos.

3. Con estos datos debe registrar la información de la llamada. Esto aplica a ambos

servicios. En caso de ser una llamada desde un servicio prepago, debe hacer un

rebajo al saldo.

4. Si la operación es exitosa debe responder “OK”, de lo contrario debe responder

“ERROR”.

ID

Rol

Proveedor3

Dueño del sistema

Objetivo

Registrar bitácoras

Para

Dejar rastros de auditoría de las operaciones

Criterios de aceptación

1. El Proveedor debe llevar una bitácora de operaciones que se almacenará en el

servidor en un archivo de texto.

2. Los  datos  que  se  escribirán  en  la  bitácora  serán  de  acuerdo  con  la  solicitud

recibida

3. El formato del registro de estos datos de la bitácora debe ser JSON y almacenado

así, por ejemplo:

15/01/2025:  {“telefono”:  “25262325”,  “Fecha”:  20250528,  “Hora”:  “101025”,

“TelefonoDestino”: 89154242, “Costo”: 15000000, “Duracion”: “001025” }

4. El registro de bitácora debe realizarse en segundo plano en un hilo independiente

y no debe interrumpir la ejecución de las operaciones del  proveedor de servicios

telefónico.  Las  solicitudes  de  escritura  en  bitácora  deben  “encolarse”,  para

poder acceder al archivo de bitácora de forma ordenada y evitar bloqueos.

5. Se debe registrar bitácora para todo tipo de transacción tanto tramas de entrada

como tramas de salida.

Página 5

II Cuatrimestre, 2025



Programación IV

ID

Rol

Identificador1

Dueño del sistema

Objetivo

Autorizar llamada telefónica

Para

Atender las solicitudes de la red de telefonía

Criterios de aceptación

1. El autorizador debe recibir una trama en formato XML o JSON que incorpore la

siguiente información:

a.  Número de teléfono (debe enviarse y almacenarse cifrado).

b.  Identificador  del  teléfono,  número  de  16  dígitos  (debe  enviarse  y

almacenarse cifrado).

c.  Identificador  de  la  tarjeta  ,  número  de  19  dígitos(debe  enviarse  y

almacenarse cifrado).

d.  Ubicación geográfica.

e.  Tipo de transacción (Ejemplo: solicitud).

f.  Teléfono destino.

2. Deben realizarse las siguientes validaciones contra la base de datos del sistema

identificación:

a.  Todos los datos son obligatorios.

b.  El  número  de  teléfono  debe  existir,  estar  activo  y  pertenecer  a  un

proveedor telefónico.

c.  El  identificador  de  la  tarjeta  telefónica  debe  coincidir  con  el  dato

registrado como identificador en la tarjeta telefónica.

d.  La ubicación geográfica debe ser indicada en coordenadas y son válidas

solamente si corresponde al territorio nacional.

e.  El tipo de transacción puede ser solamente solicitud.

f.  El teléfono destino debe existir y estar activo en caso de ser una llamada

nacional, en caso de ser una llamada internacional el código de país debe

ser válido.

Página 6

II Cuatrimestre, 2025



Programación IV

g.  Si alguno de los datos no es válido, debe indicar alguno de los siguientes

códigos de motivo de acuerdo con lo sucedido, dentro de una trama XML

o JSON:

i.  1: Teléfono destino inválido (inactivo o que no existe).

ii.  2: Datos de tarjeta telefónica no coinciden.

iii.  3: Llamada no permitida (cuando está fuera del área del país).

iv.  4: Acción inválida (cuando no es llamada o consulta).

v.  5. Código de país inválido.

vi.  5: Error no controlado.

3. Al  recibir  una  transacción  si  todas  las  validaciones  anteriores  son  correctas

entonces:

a.  Al ser del tipo de transacción llamada se debe realizar lo siguiente:

i.  El  saldo  debe  ser  verificado  contra  el  Proveedor  Telefónico  si  el

cliente tiene el suficiente  saldo para  realizar el primer minuto de

llamada. Con Ver HU PROVEEDOR1.

ii.  Al  recibir  la  respuesta,  de  forma  afirmativa,  debe  responder  con

una trama XML o JSON, indicando que se puede iniciar la llamada

(ok),  y  con  la  cantidad  de  tiempo  máximo  indicado  en  horas,

minutos  y  segundos  que  puede  mantener  la  llamada,  en  un

formato de 6 dígitos sin puntos. Ejemplo: {“status”: “OK”, “tiempo”:

012310}.

4. Si el tipo de transacción es saldo entonces:

a.  Verificar el saldo con el proveedor telefónico, y responder con una trama

XML o JSON, el monto del saldo disponible representado con 19 espacios,

incluyendo  decimales  (últimos  dos  campos).  Ejemplo:  {“status”: “OK”,

“saldo”: 0000000012345678900}.

b.  Si se reporta error, entonces debe dar respuesta dentro de una trama XML

o JSON:

1: Error no controlado.

Página 7

II Cuatrimestre, 2025



Programación IV

Anotaciones técnicas:

•  El identificador debe implementarse usando sockets síncronos. Puede atender

múltiples solicitudes de llamadas diferentes a la vez.

•  El puerto donde ejecuta debe poder indicarse en configuración.

•  El algoritmo de cifrado de los datos sensibles, debe ser AES.

ID

Rol

Identificador2

Dueño del sistema

Objetivo

Iniciar llamada

Para

Atender el aviso donde indica el inicio de una llamada telefónica

Criterios de aceptación

1. El identificador debe recibir una trama en formato XML o JSON que incorpore la

siguiente información:

a.  Número de teléfono (debe enviarse y almacenarse cifrado).

b.  Identificador del teléfono (debe enviarse y almacenarse cifrado).

c.  Identificador de la tarjeta (debe enviarse y almacenarse cifrado).

d.  Ubicación geográfica.

e.  Tipo de transacción (Ejemplo: llamada).

f.  Teléfono destino.

g.  Tiempo máximo.

2. Como las validaciones fueron realizadas en un proceso anterior, en este caso se

procede  a  contabilizar  el  inicio  de  la  llamada,  para  lo  cual  se  registra  la

información en el ente identificador con el fin de llevar un control de los datos de

la  llamada.  Se  debe  mantener  un  control  de  todas  las  llamadas  activas,  en

especial las de servicio prepago, a las cuales se les debe agregar una fecha y hora

en que la llamada terminará (llamadas desde un servicio prepago) y ordenarlas

de acuerdo con ese saldo, siendo la más cercanas de primeras y con el tiempo

más lejano de últimas.

Anotaciones técnicas:

Página 8

II Cuatrimestre, 2025



•  Se mantienen las indicaciones técnicas expresadas en HU Identificador1.

•  El recurso que implemente la lista o información de las llamadas activas debe

estar disponible a nivel de proceso, con el fin de que no deje hilos pendientes en

llamadas activas.

Programación IV

ID

Rol

Identificador3

Dueño del sistema

Objetivo

Terminación de llamadas

Para

Facilitar el fin de una llamada

Criterios de aceptación

1. Una  llamada  telefónica  puede  terminar  por  múltiples  razones,  dentro  de  ellas

están:

a.  Pérdida de la conexión en alguno de los puntos.

b.  Se termina el tiempo según el saldo de un servicio prepago.

c.  El cliente decide terminar la llamada desde el dispositivo.

2. El  Identificador,  debe  contar  con  un  proceso  que  esté  revisando  la  lista  de

llamadas activas cada cierta cantidad de tiempo, dado que la lista se encuentra

ordenada por la fecha y hora máxima, debe evaluar si ya venció, si es así debe:

a.  Informar al cliente por medio de una trama que la llamada terminó porque

se  le  agotó  el  saldo,  usando  un  formato  XML  o  JSON  que  incorpore  la

siguiente información para que el usuario sepa que pasó:

estado: “finalizada”, razón: “saldo agotado”

b.  Enviar  la  información  con  los  datos  requeridos  al  Proveedor  para  que

registre los movimientos respectivos (registro de llamada, rebajo de saldo

en caso requerido). UH Proveedor2

3. En el caso en que la conexión es interrumpida o porque el cliente decide terminar

la llamada se debe recibir una trama en formato XML o JSON que incorpore la

siguiente información:

Página 9

II Cuatrimestre, 2025



Programación IV

a.  Número de teléfono (debe enviarse y almacenarse cifrado).

b.  Identificador del teléfono (debe enviarse y almacenarse cifrado).

c.  Identificador de la tarjeta (debe enviarse y almacenarse cifrado).

d.  Ubicación geográfica.

e.  Tipo de transacción (Ejemplo: finalizacion).

f.  Teléfono destino.

Con esta información se debe buscar la llamada activa, excluirla de la lista de

llamadas activas, tomar el tiempo de la finalización y construir los datos para

ser enviados al Proveedor para sus respectivos movimientos. UH Proveedor2.

Debe enviar información de vuelta al cliente, generando una trama un formato

XML  o  JSON  que  incorpore  la  siguiente  información  con  el  resultado  de  la

operación si fue exitosa, estado: “ok”. En caso de recibir algún reporte de error

debe indicar estado: “fallido”.

Anotaciones técnicas:

•  Debe mantener las recomendaciones técnicas de la UH Información1.

Página 10

II Cuatrimestre, 2025



Programación IV

ID

Rol

Identificador4

Dueño del sistema

Objetivo

Consulta de saldo

Para

Solicitar el saldo

Criterios de aceptación

•  El autorizador debe recibir una trama en formato XML o JSON que incorpore la

siguiente información:

a.  Número de teléfono (debe enviarse y almacenarse cifrado).

b.  Identificador  del  teléfono,  número  de  16  dígitos  (debe  enviarse  y

almacenarse cifrado).

c.  Identificador  de  la  tarjeta,  número  de  19  dígitos  (debe  enviarse  y

almacenarse cifrado).

d.  Ubicación geográfica.

e.  Tipo de transacción (Ejemplo: saldo).

•  Deben realizarse las siguientes validaciones contra la base de datos del sistema

identificación:

a.  Todos los datos son obligatorios.

b.  El  número  de  teléfono  debe  existir,  estar  activo  y  pertenecer  a  un

proveedor telefónico.

c.  El  identificador  de  la  tarjeta  telefónica  debe  coincidir  con  el  dato

registrado como identificador en la tarjeta telefónica.

d.  La  ubicación  geográfica  debe  ser  indicada  en  coordenadas  y  son

válidas solamente si corresponde al territorio nacional.

e.  El tipo de transacción puede ser solamente saldo.

f.  Si  alguno  de  los  datos  no  es  válido,  debe  indicar  alguno  de  los

siguientes códigos de motivo de acuerdo con lo sucedido, dentro de

una trama XML o JSON:

i.  1: Teléfono destino inválido (inactivo o que no existe).

Página 11

II Cuatrimestre, 2025



Programación IV

ii.  2: Datos de tarjeta telefónica no coinciden.

iii.  3: Llamada no permitida (cuando está fuera del área del país).

iv.  4: Acción inválida (cuando no es llamada o consulta).

v.  5. Código de país inválido.

vi.  5: Error no controlado.

•  Al  recibir  una  transacción  si  todas  las  validaciones  anteriores  son  correctas

entonces:

a.  Verificar  el  saldo  con  el  proveedor  telefónico,  y  responder  con  una

trama XML o JSON, el monto del saldo disponible representado con 19

espacios,  incluyendo  decimales  (últimos  dos  campos).  Ejemplo:

{“status”: “OK”, “saldo”: 0000000012345678900}.

b.  Si se reporta error, entonces debe dar respuesta dentro de una trama

XML o JSON:

1: Error no controlado.

Anotaciones técnicas:

•  Aplicar las indicaciones técnicas descritas en HU Identificador1

ID

Rol

Identificador5

Dueño del sistema

Objetivo

Registrar bitácoras

Para

Dejar rastros de auditoría de las operaciones

Criterios de aceptación

6. El identificador debe llevar una bitácora de operaciones que se almacenará en el

servidor en un archivo de texto.

7. Los datos que se escribirán en la bitácora serán:

a.  Número de teléfono.

b.  Identificador del teléfono.

Página 12

II Cuatrimestre, 2025



c.  Identificador de la tarjeta (debe enviarse y almacenarse cifrado).

d.  Ubicación geográfica.

e.  Tipo de transacción (Ejemplo: solicitud, llamada, finalización, saldo ).

Programación IV

f.  Teléfono destino.

g.  Tiempo máximo (si aplica).

8. El formato del registro de estos datos de la bitácora debe ser JSON y almacenado

así, por ejemplo:

15/01/2025:

{“telefono”:  “25262325”,  “identificadorTel”:  1509565854152635,

“identificadorChip”:

“1123404569899696969”,

“coordenadas”:9°55′57″N

84°04′46″O”,

“Transaccion”:

“solicitud”,

Destino:

“(506)25698526”,

Tiempo:”00:00:00”}

9. El registro de bitácora debe realizarse en segundo plano en un hilo independiente

y  no  debe  interrumpir  la  ejecución  de  las  operaciones  del  autorizador.  Las

solicitudes  de  escritura  en  bitácora  deben “encolarse”,  para  poder  acceder  al

archivo de bitácora de forma ordenada y evitar bloqueos.

10. Se  debe  registrar  bitácora  para  todo  tipo  de  transacción  tanto  de  tramas  de

entrada como tramas de salida.

ID

Rol

SIM1

Dueño del sistema

Objetivo

Un simulador de llamadas telefónicas

Para

Corroborar la funcionalidad de las transacciones del identificador

Criterios de aceptación

1. Debe crear una interfaz que simulará varios teléfonos realizando llamadas. Las

operaciones  serían  marcar  número,  realizar  llamada,  y  rebajo  de  saldo  de

acuerdo con la duración. Si se escribe #9090\* debe consultar el saldo e indicarse

en pantalla el resultado.

Página 13

II Cuatrimestre, 2025



Programación IV

2. Cada una de las transacciones debe tener una pantalla independiente.
3. Cada  llamada  capturará  los  datos  que  solicita  únicamente.  No  es  necesario

validarlos, el software de identificación realizará la validación.

4. Cada  interfaz  debe  armar  la  trama  que  requiere  el  identificador  y  enviársela  y

mostrar el resultado al terminar la operación.

5. Recuerde que los datos sensibles deben transmitirse cifrados.

Aspectos técnicos generales obligatorios por cumplir

1. El simulador debe desarrollarse en C#.
2. El socket del identificador debe realizarlo en Python BD MySQL.
3. El socket del Proveedor debe realizarlo en Java BD SQL Server.

Página 14

II Cuatrimestre, 2025



Diagrama general de arquitectura (Alto nivel)

Programación IV

Entregables

A continuación, se lista lo que debe entregar en el proyecto.

1. Documentación de análisis y diseño.

a.  Portada.

b.  Introducción (Resumen del problema a resolver).

c.  Diagrama de base de datos (presentar propuestas la próxima semana).

d.  Diagramas de casos de uso.

e.  Diagramas de clases.

f.  Conclusiones y recomendaciones.

g.  Bibliografía.

2. Implementación de bases de datos.
3. Código fuente del Identificador.
4. Código fuente del Proveedor Telefónico.
5. Código fuente del Simulador de llamadas.

Página 15

II Cuatrimestre, 2025



Aspectos administrativos

Programación IV

•  El proyecto debe desarrollarse en los equipos de trabajo definidos.

•  Corresponde  desarrollar  3  historias  por  integrante.  Se  desarrollan  de  forma

individual, no se comparten.  Indicar en el canal de TEAMS, la próxima semana

a más tardar la forma de distribución de las historias.

•  La fecha de entrega es el miércoles 10 de junio antes de las 6 p.m.

•  Se  debe  presentar  en  funcionamiento  en  clase,  y  el  código  no  puede  ser

modificado durante ese periodo.

•  Si  modifican  el  código  después  de  la entrega  y  no  coincide  con  lo  entregado

respectivamente pierden el puntaje de las diferencias.

•  El valor de este avance es del 15%.

•  Al realizar la revisión, el grupo decide si desea una evaluación general, o una

evaluación por historias (dependiendo de las historias desarrolladas por cada

uno),  en  ese  caso  la  evaluación  sería  de  forma  individual,  por  lo  que  cada

persona es responsable de sus criterios de aceptación desarrollados.

•  Debe apegarse al uso de tecnologías indicado.

•  Si presentan funcionalidad pero no presentan el código, dicha entrega no será

tomada en cuenta como válida perdiendo el porcentaje respectivo. Así también

si  el  código  no  coincide  con  lo  mostrado  o  si  no  respetan  la  tecnología  e

indicaciones brindadas.

•  Durante  la  presentación,  aunque  son  componentes  distintos  deben  estar

comunicados  entre  sí  como  un  proyecto  integrado,  esto  es  que  pueden

presentarse en equipos individuales o en un solo equipo, pero deben funcionar

como un conjunto de aplicaciones integradas, en caso de presentar de forma

aislada  algún  componente,  esta  revisión  será  sancionada  y  calificada  sobre

base 85 en vez de base 100.

Página 16

II Cuatrimestre, 2025

