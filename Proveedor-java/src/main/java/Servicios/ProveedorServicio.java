package Servicios;

import modelos.Cuenta;
import modelos.Llamada;
import modelos.Tarifa;
import org.json.JSONObject;
import repositorio.CuentaRepositorio;
import repositorio.LlamadaRepositorio;
import repositorio.TarifaRepositorio;

public class ProveedorServicio {
    private final CuentaRepositorio cuentaRepo = new CuentaRepositorio();
    private final TarifaRepositorio tarifaRepo = new TarifaRepositorio();
    private final LlamadaRepositorio llamadaRepo = new LlamadaRepositorio();

    public JSONObject verificarSaldo(String telefono, int tipoTransaccion,
                                     int tipoLlamada, String telefonoDestino) {
        JSONObject respuesta = new JSONObject();

        try {
            Cuenta cuenta = cuentaRepo.obtenerPorTelefono(telefono);
            if (cuenta == null) {
                return respuesta.put("status", "ERROR");
            }

            if (tipoTransaccion == 2) {
                return consultarSaldo(cuenta);
            }

            if (tipoTransaccion != 1) {
                return respuesta.put("status", "ERROR");
            }

            return autorizarLlamada(cuenta, tipoLlamada, telefonoDestino);
        } catch (Exception e) {
            e.printStackTrace();
            return respuesta.put("status", "ERROR");
        }
    }

    public JSONObject registrarLlamada(String telefonoOrigen, int fecha, int hora,
                                       String telefonoDestino, long costoTotal,
                                       int duracionSegundos) {
        JSONObject respuesta = new JSONObject();

        try {
            Cuenta cuenta = cuentaRepo.obtenerPorTelefono(telefonoOrigen);
            if (cuenta == null) {
                return respuesta.put("status", "ERROR");
            }

            boolean mismoProveedor = esDestinoMismoProveedor(cuenta, telefonoDestino);
            double costoColones = costoTotal / 100.0;
            if (!cuenta.esPostpago() && !tieneFondosParaRebajo(cuenta, costoColones, mismoProveedor)) {
                return respuesta.put("status", "ERROR");
            }

            Llamada llamada = new Llamada(
                    telefonoOrigen,
                    fecha,
                    hora,
                    telefonoDestino,
                    costoTotal,
                    duracionSegundos
            );

            boolean registrada = llamadaRepo.registrarLlamada(llamada);
            if (!registrada) {
                return respuesta.put("status", "ERROR");
            }

            if (!cuenta.esPostpago()) {
                boolean saldoActualizado = rebajarPrepago(cuenta, telefonoOrigen, costoColones, mismoProveedor);
                if (!saldoActualizado) {
                    return respuesta.put("status", "ERROR");
                }
            }

            return respuesta.put("status", "OK");
        } catch (Exception e) {
            e.printStackTrace();
            return respuesta.put("status", "ERROR");
        }
    }

    private JSONObject consultarSaldo(Cuenta cuenta) {
        JSONObject respuesta = new JSONObject();
        respuesta.put("status", "OK");

        if (cuenta.esPostpago()) {
            respuesta.put("saldo", "-1");
        } else {
            long centesimas = Math.round(cuenta.getSaldo() * 100);
            respuesta.put("saldo", formatearSaldo(centesimas));
        }

        return respuesta;
    }

    private JSONObject autorizarLlamada(Cuenta cuenta, int tipoLlamada, String telefonoDestino) {
        JSONObject respuesta = new JSONObject();

        if (tipoLlamada < 1 || tipoLlamada > 3) {
            return respuesta.put("status", "ERROR");
        }

        String tipoDestino = determinarTipoDestino(telefonoDestino, tipoLlamada);
        String prefijo = tipoLlamada == 3 ? extraerPrefijo(telefonoDestino) : null;
        Tarifa tarifa = tarifaRepo.obtenerTarifa(tipoLlamada, tipoDestino, prefijo);

        if (tarifa == null) {
            return respuesta.put("status", "ERROR");
        }

        double costoPorMinuto = tarifa.getCostoPorMinuto();

        if (cuenta.esPostpago()) {
            respuesta.put("status", "OK");
            respuesta.put("tarifa", "9999999999");
            respuesta.put("tiempo", "245959");
            return respuesta;
        }

        double saldoDisponible = saldoDisponibleParaLlamada(cuenta, tipoLlamada);

        if (saldoDisponible < costoPorMinuto) {
            return respuesta.put("status", "INSUF");
        }

        long tarifaCentesimas = Math.round(costoPorMinuto * 100);
        respuesta.put("status", "OK");
        respuesta.put("tarifa", formatearTarifa(tarifaCentesimas));
        respuesta.put("tiempo", calcularTiempoDisponible(saldoDisponible, costoPorMinuto));
        return respuesta;
    }

    private double saldoDisponibleParaLlamada(Cuenta cuenta, int tipoLlamada) {
        if (tipoLlamada == 1) {
            return cuenta.getSaldo() + cuenta.getBonoMismoProveedor();
        }
        return cuenta.getSaldo();
    }

    private boolean rebajarPrepago(Cuenta cuenta, String telefonoOrigen,
                                   double costoColones, boolean mismoProveedor) {
        double nuevoSaldo = cuenta.getSaldo();
        double nuevoBono = cuenta.getBonoMismoProveedor();

        if (mismoProveedor && nuevoBono > 0) {
            double aplicadoBono = Math.min(nuevoBono, costoColones);
            nuevoBono -= aplicadoBono;
            costoColones -= aplicadoBono;
        }

        if (nuevoSaldo < costoColones) {
            return false;
        }

        nuevoSaldo -= costoColones;
        return cuentaRepo.actualizarSaldoYBono(telefonoOrigen, nuevoSaldo, nuevoBono);
    }

    private boolean tieneFondosParaRebajo(Cuenta cuenta, double costoColones, boolean mismoProveedor) {
        double disponible = cuenta.getSaldo();
        if (mismoProveedor) {
            disponible += cuenta.getBonoMismoProveedor();
        }
        return disponible >= costoColones;
    }

    private boolean esDestinoMismoProveedor(Cuenta cuentaOrigen, String telefonoDestino) {
        String destino = normalizarTelefonoNacional(telefonoDestino);
        Cuenta cuentaDestino = cuentaRepo.obtenerPorTelefono(destino);
        return cuentaDestino != null
                && cuentaOrigen.getProveedor() != null
                && cuentaOrigen.getProveedor().equalsIgnoreCase(cuentaDestino.getProveedor());
    }

    private String determinarTipoDestino(String telefonoDestino, int tipoLlamada) {
        if (tipoLlamada == 3) {
            return null;
        }

        if (telefonoDestino == null || telefonoDestino.isBlank()) {
            return "fijo";
        }

        char primero = telefonoDestino.charAt(0);
        if (primero == '6' || primero == '7' || primero == '8') {
            return "movil";
        }

        return "fijo";
    }

    private String extraerPrefijo(String telefonoDestino) {
        if (telefonoDestino == null) {
            return "";
        }

        String numero = telefonoDestino.replace("+", "").trim();
        return numero.length() >= 4 ? numero.substring(0, 4) : numero;
    }

    private String normalizarTelefonoNacional(String telefono) {
        if (telefono == null) {
            return "";
        }

        String numero = telefono.replace("+", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "")
                .trim();

        if (numero.startsWith("506") && numero.length() == 11) {
            return numero.substring(3);
        }

        return numero;
    }

    private String calcularTiempoDisponible(double saldo, double costoPorMinuto) {
        if (costoPorMinuto <= 0) {
            return "000000";
        }

        int totalSegundos = (int) ((saldo / costoPorMinuto) * 60);
        int horas = totalSegundos / 3600;
        int minutos = (totalSegundos % 3600) / 60;
        int segundos = totalSegundos % 60;

        if (horas > 99) {
            horas = 99;
            minutos = 59;
            segundos = 59;
        }

        return String.format("%02d%02d%02d", horas, minutos, segundos);
    }

    private String formatearTarifa(long centesimas) {
        return String.format("%010d", centesimas);
    }

    private String formatearSaldo(long centesimas) {
        return String.format("%019d", centesimas);
    }
}
