from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes


class AES128:
    @staticmethod
    def encrypt(plaintext, key) -> tuple[bytes, bytes]:
        plaintext_bytes = plaintext.encode("utf-8") if not isinstance(plaintext, bytes) else plaintext
        key_bytes = key.encode("utf-8") if not isinstance(key, bytes) else key
        initialization_vector_bytes = get_random_bytes(AES.block_size)
        aes128 = AES.new(key_bytes, AES.MODE_CFB, initialization_vector_bytes)
        ciphertext_bytes = aes128.encrypt(plaintext_bytes)
        return initialization_vector_bytes, ciphertext_bytes

    @staticmethod
    def decrypt(ciphertext, initialization_vector, key) -> bytes:
        ciphertext_bytes = ciphertext.encode("utf-8") if not isinstance(ciphertext, bytes) else ciphertext
        initialization_vector_bytes = initialization_vector.encode("utf-8") if not isinstance(initialization_vector, bytes) else initialization_vector
        key_bytes = key.encode("utf-8") if not isinstance(key, bytes) else key
        aes128 = AES.new(key_bytes, AES.MODE_CFB, initialization_vector_bytes)
        plaintext_bytes = aes128.decrypt(ciphertext_bytes)
        return plaintext_bytes
