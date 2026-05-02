import base64
from backend.KeyRings import KeyRings
from datetime import datetime
from backend.authentication_algorithms.RSA import RSA
from backend.authentication_algorithms.SHA1 import SHA1
import zlib
import json
from Crypto.Random import get_random_bytes
from backend.confidentiality_algorithms.AES128 import AES128
from backend.confidentiality_algorithms.TripleDES import TripleDES


class Communication:
    @staticmethod
    def send_message(
            plaintext,
            sender,
            authentication,
            compression,
            confidentiality,
            radix64,
            sender_rsa_key_user_id,
            sender_rsa_key_id,
            private_key_password,
            receiver_rsa_user_id,
            receiver_rsa_key_id,
            confidentiality_algorithm
    ) -> str | dict:
        pgp_message = {
            "pgp_message": {
                "message_and_authentication": {
                    "message": {
                        "data": plaintext,
                        "timestamp": datetime.now().isoformat(),
                        "filename": "plaintext.txt"
                    },
                    "authentication": dict()
                },
                "confidentiality": dict()
            },
            "is_signed": authentication,
            "is_compressed": compression,
            "is_encrypted": confidentiality,
            "is_radix64_encoded": radix64
        }
        failure_message = {
            "error": ""
        }

        if authentication:
            sender_private_key = KeyRings.get_private_key(sender, sender_rsa_key_user_id, sender_rsa_key_id, private_key_password)
            if sender_private_key is None:
                failure_message["error"] = "Incorrect private key password!"
                return failure_message
            pgp_message["pgp_message"]["message_and_authentication"]["authentication"] = Communication.sign_message(plaintext, sender_rsa_key_id, sender_private_key)

        if compression:
            pgp_message["pgp_message"]["message_and_authentication"] = Communication.compress_dictionary(pgp_message["pgp_message"]["message_and_authentication"])

        if confidentiality:
            session_key = None
            if confidentiality_algorithm == "AES128":
                session_key = get_random_bytes(16)
            elif confidentiality_algorithm == "TripleDES":
                session_key = get_random_bytes(24)
            receiver_public_key = KeyRings.get_public_key(sender, receiver_rsa_user_id, receiver_rsa_key_id)
            pgp_message["pgp_message"]["confidentiality"] = Communication.encrypt_message_and_signature(pgp_message, session_key, receiver_rsa_key_id, receiver_public_key, confidentiality_algorithm)

        if radix64:
            pgp_message["pgp_message"] = Communication.get_radix64_encoded_pgp_message(pgp_message)

        return json.dumps(pgp_message)

    @staticmethod
    def receive_message(receiver, pgp_message, private_key_password) -> dict:
        response = {
            "pgp_message": dict(),
            "decryption_error": "",
            "verification_error": "",
            "verification": dict()
        }
        pgp_message = json.loads(pgp_message)
        if pgp_message["is_radix64_encoded"]:
            pgp_message = Communication.get_pgp_message_from_radix64_encoded_pgp_message(pgp_message["pgp_message"])
        if pgp_message["is_encrypted"]:
            decrypted_message_and_authentication = Communication.decrypt_message_and_signature(pgp_message, receiver, private_key_password)
            if decrypted_message_and_authentication is None:
                response["decryption_error"] = "Message decryption has failed!"
                return response
            pgp_message["pgp_message"]["message_and_authentication"] = decrypted_message_and_authentication
        if pgp_message["is_compressed"]:
            pgp_message["pgp_message"]["message_and_authentication"] = Communication.decompress_dictionary(pgp_message["pgp_message"]["message_and_authentication"])
        if pgp_message["is_signed"]:
            verification = Communication.verify_message(receiver, pgp_message)
            if verification["status"] == "nok":
                response["verification_error"] = "Message verification has failed!"
                return response
            else:
                response["verification"] = verification
        response["pgp_message"] = pgp_message
        return response

    @staticmethod
    def sign_message(message, sender_rsa_key_id, sender_private_key) -> dict:
        timestamp = datetime.now().isoformat()
        message_digest = SHA1.binary_digest(message + timestamp)
        leading_two_octets_message_digest = message_digest[0:2]
        signed_message_digest = RSA.sign_message(message_digest, sender_private_key)
        return {
            "signed_message_digest": signed_message_digest.hex(),
            "leading_two_octets_message_digest": leading_two_octets_message_digest.hex(),
            "sender_public_key_id": sender_rsa_key_id,
            "timestamp": timestamp
        }

    @staticmethod
    def verify_message(receiver, pgp_message) -> dict:
        verification = {
            "status": "",
            "sender_user_id": "",
            "sender_user_name": "",
            "message_timestamp": ""
        }

        sender_public_key_id = pgp_message["pgp_message"]["message_and_authentication"]["authentication"]["sender_public_key_id"]
        sender_public_key = KeyRings.get_public_key_by_key_id(receiver, sender_public_key_id)
        if sender_public_key is None:
            verification["status"] = "nok"
            return verification
        message_data = pgp_message["pgp_message"]["message_and_authentication"]["message"]["data"]
        signature_timestamp = pgp_message["pgp_message"]["message_and_authentication"]["authentication"]["timestamp"]
        signed_message_digest_bytes = bytes.fromhex(pgp_message["pgp_message"]["message_and_authentication"]["authentication"]["signed_message_digest"])

        if RSA.verify_message(SHA1.binary_digest(message_data + signature_timestamp), signed_message_digest_bytes, sender_public_key):
            sender_public_key_ring_entry = KeyRings.get_public_key_ring_entry_by_key_id(receiver, sender_public_key_id)
            verification["status"] = "ok"
            verification["sender_user_id"] = sender_public_key_ring_entry["user_id"]
            verification["sender_user_name"] = sender_public_key_ring_entry["user_name"]
            verification["message_timestamp"] = pgp_message["pgp_message"]["message_and_authentication"]["message"]["timestamp"]
        else:
            verification["status"] = "nok"
        return verification

    @staticmethod
    def compress_dictionary(dictionary) -> str:
        dictionary_string = json.dumps(dictionary)
        dictionary_bytes = dictionary_string.encode("utf-8")
        dictionary_compressed = zlib.compress(dictionary_bytes)
        return dictionary_compressed.hex()

    @staticmethod
    def decompress_dictionary(dictionary_compressed_hex) -> dict:
        dictionary_bytes = zlib.decompress(bytes.fromhex(dictionary_compressed_hex))
        dictionary_string = dictionary_bytes.decode("utf-8")
        dictionary = json.loads(dictionary_string)
        return dictionary

    @staticmethod
    def encrypt_message_and_signature(pgp_message, session_key, receiver_rsa_key_id, receiver_public_key, confidentiality_algorithm) -> dict:
        encrypted_message_and_authentication = None
        initialization_vector = None

        if pgp_message["is_compressed"]:
            pgp_message["pgp_message"]["message_and_authentication"] = bytes.fromhex(pgp_message["pgp_message"]["message_and_authentication"])
        else:
            pgp_message["pgp_message"]["message_and_authentication"] = json.dumps(pgp_message["pgp_message"]["message_and_authentication"])
            pgp_message["pgp_message"]["message_and_authentication"] = pgp_message["pgp_message"]["message_and_authentication"].encode("utf-8")

        if confidentiality_algorithm == "AES128":
            initialization_vector, encrypted_message_and_authentication = AES128.encrypt(pgp_message["pgp_message"]["message_and_authentication"], session_key)
        elif confidentiality_algorithm == "TripleDES":
            initialization_vector, encrypted_message_and_authentication = TripleDES.encrypt(pgp_message["pgp_message"]["message_and_authentication"], session_key)
        pgp_message["pgp_message"]["message_and_authentication"] = encrypted_message_and_authentication.hex()
        encrypted_session_key = RSA.encrypt(session_key, receiver_public_key)
        return {
            "session_key": encrypted_session_key.hex(),
            "receiver_public_key_id": receiver_rsa_key_id,
            "algorithm": confidentiality_algorithm,
            "initialization_vector": initialization_vector.hex()
        }

    @staticmethod
    def decrypt_message_and_signature(pgp_message, receiver, private_key_password) -> str | dict | None:
        encrypted_session_key_hex = pgp_message["pgp_message"]["confidentiality"]["session_key"]
        receiver_public_key_id = pgp_message["pgp_message"]["confidentiality"]["receiver_public_key_id"]
        algorithm = pgp_message["pgp_message"]["confidentiality"]["algorithm"]
        initialization_vector_bytes = bytes.fromhex(pgp_message["pgp_message"]["confidentiality"]["initialization_vector"])
        encrypted_message_and_authentication_bytes = bytes.fromhex(pgp_message["pgp_message"]["message_and_authentication"])

        receiver_private_key = KeyRings.get_private_key_by_key_id(receiver, receiver_public_key_id, private_key_password)
        if receiver_private_key is None:
            return None
        session_key_bytes = RSA.decrypt(bytes.fromhex(encrypted_session_key_hex), receiver_private_key)
        if session_key_bytes is None:
            return None

        message_and_authentication_bytes = None
        try:
            if algorithm == "AES128":
                message_and_authentication_bytes = AES128.decrypt(encrypted_message_and_authentication_bytes, initialization_vector_bytes, session_key_bytes)
            elif algorithm == "TripleDES":
                message_and_authentication_bytes = TripleDES.decrypt(encrypted_message_and_authentication_bytes, initialization_vector_bytes, session_key_bytes)
        except ValueError:
            return None

        if pgp_message["is_compressed"]:
            pgp_message["pgp_message"]["message_and_authentication"] = message_and_authentication_bytes.hex()
        else:
            pgp_message["pgp_message"]["message_and_authentication"] = message_and_authentication_bytes.decode("utf-8")
            pgp_message["pgp_message"]["message_and_authentication"] = json.loads(pgp_message["pgp_message"]["message_and_authentication"])

        return pgp_message["pgp_message"]["message_and_authentication"]

    @staticmethod
    def get_radix64_encoded_pgp_message(pgp_message_dictionary) -> str:
        pgp_message_string = json.dumps(pgp_message_dictionary)
        pgp_message_bytes = pgp_message_string.encode("utf-8")
        return base64.b64encode(pgp_message_bytes).decode("utf-8")

    @staticmethod
    def get_pgp_message_from_radix64_encoded_pgp_message(radix64_encoded_pgp_message) -> dict:
        pgp_message_bytes = base64.b64decode(radix64_encoded_pgp_message)
        pgp_message_string = pgp_message_bytes.decode("utf-8")
        pgp_message_dictionary = json.loads(pgp_message_string)
        return pgp_message_dictionary
