import json
import threading
from queue import Queue
from datetime import datetime


class Bitacora:

    cola = Queue()
    hiloIniciado = False

    @staticmethod
    def iniciarHilo():
        if Bitacora.hiloIniciado:
            return

        hilo = threading.Thread(
            target=Bitacora.procesarCola,
            daemon=True
        )

        hilo.start()
        Bitacora.hiloIniciado = True

    @staticmethod
    def procesarCola():
        while True:
            registro = Bitacora.cola.get()
            fecha = datetime.now().strftime("%d/%m/%Y %H:%M:%S")

            with open("bitacora_identificador.txt", "a", encoding="utf-8") as archivo:
                archivo.write(f"{fecha}: {json.dumps(registro, ensure_ascii=False)}\n")

            Bitacora.cola.task_done()

    @staticmethod
    def registrar(direccion, datos):
        Bitacora.cola.put({
            "direccion": direccion,
            "trama": datos
        })
