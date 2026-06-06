import base64
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad, unpad


class CifradorAES:

    CLAVE = "1234567890123456"

    @staticmethod
    def cifrar(texto):
        clave = CifradorAES.CLAVE.encode("utf-8")
        cipher = AES.new(clave, AES.MODE_ECB)
        cifrado = cipher.encrypt(pad(str(texto).encode("utf-8"), AES.block_size))
        return base64.b64encode(cifrado).decode("utf-8")

    @staticmethod
    def descifrar(textoCifrado):
        clave = CifradorAES.CLAVE.encode("utf-8")
        cipher = AES.new(clave, AES.MODE_ECB)
        datos = base64.b64decode(textoCifrado)
        return unpad(cipher.decrypt(datos), AES.block_size).decode("utf-8")
