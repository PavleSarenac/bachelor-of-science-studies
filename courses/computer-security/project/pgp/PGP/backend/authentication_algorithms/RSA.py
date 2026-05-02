from rsa import *

from backend.KeyRings import KeyRings


class RSA:
    @staticmethod
    def generate_new_key_pair(
            person,
            user_name,
            user_email,
            key_size_in_bits,
            private_key_password
    ) -> tuple[PublicKey, PrivateKey]:
        public_key, private_key = newkeys(key_size_in_bits)
        KeyRings.insert_into_private_key_ring(person, user_name, user_email, private_key_password, public_key, private_key)
        return public_key, private_key

    @staticmethod
    def encrypt(plaintext, public_key) -> bytes:
        plaintext_bytes = plaintext if isinstance(plaintext, bytes) else plaintext.encode("utf-8")
        ciphertext_bytes = encrypt(plaintext_bytes, public_key)
        return ciphertext_bytes

    @staticmethod
    def decrypt(ciphertext, private_key) -> bytes | None:
        ciphertext_bytes = ciphertext if isinstance(ciphertext, bytes) else ciphertext.encode("utf-8")
        try:
            plaintext_bytes = decrypt(ciphertext_bytes, private_key)
            return plaintext_bytes
        except DecryptionError:
            return None

    @staticmethod
    def sign_message(plaintext, private_key) -> bytes | None:
        try:
            plaintext_bytes = plaintext if isinstance(plaintext, bytes) else plaintext.encode("utf-8")
            signature = sign(plaintext_bytes, private_key, "SHA-1")
            return signature
        except OverflowError as exception:
            print(f"Signing error: {exception}")
            return None

    @staticmethod
    def verify_message(plaintext, signature, public_key) -> bool:
        try:
            plaintext_bytes = plaintext if isinstance(plaintext, bytes) else plaintext.encode("utf-8")
            hash_method = verify(plaintext_bytes, signature, public_key)
            return hash_method == "SHA-1"
        except VerificationError as exception:
            print(f"Verification error: {exception}")
            return False
