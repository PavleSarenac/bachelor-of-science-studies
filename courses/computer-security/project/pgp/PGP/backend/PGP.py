from rsa import PublicKey, PrivateKey
from backend.Communication import Communication
from backend.KeyRings import KeyRings
from backend.authentication_algorithms.RSA import RSA


class PGP:
    @staticmethod
    def generate_new_rsa_key_pair(
            person,
            user_name,
            user_email,
            key_size_in_bits,
            private_key_password
    ) -> tuple[PublicKey, PrivateKey]:
        public_key, private_key = RSA.generate_new_key_pair(person, user_name, user_email, key_size_in_bits, private_key_password)
        return public_key, private_key

    @staticmethod
    def get_private_key_ring(person) -> list:
        return KeyRings.get_all_private_key_ring_entries(person)

    @staticmethod
    def get_public_key_ring(person) -> list:
        return KeyRings.get_all_public_key_ring_entries(person)

    @staticmethod
    def delete_rsa_key_pair_from_private_key_ring(
            person_deleting,
            person_affected,
            user_id,
            key_id,
            private_key_password
    ) -> bool:
        return KeyRings.delete_entry_from_private_key_ring(person_deleting, person_affected, user_id, key_id, private_key_password)

    @staticmethod
    def delete_public_key_from_public_key_ring(
            person,
            user_id,
            key_id
    ) -> bool:
        return KeyRings.delete_entry_from_public_key_ring(person, user_id, key_id)

    @staticmethod
    def export_private_key(
            person,
            user_id,
            key_id,
            private_key_password
    ) -> bool:
        return KeyRings.export_private_key(person, user_id, key_id, private_key_password)

    @staticmethod
    def import_private_key(person) -> dict:
        return KeyRings.import_private_key(person)

    @staticmethod
    def export_public_key(person, user_id, key_id) -> bool:
        return KeyRings.export_public_key(person, user_id, key_id)

    @staticmethod
    def import_public_key(import_person, export_person) -> dict:
        return KeyRings.import_public_key(import_person, export_person)

    @staticmethod
    def get_all_private_key_ring_entries(person) -> list:
        return KeyRings.get_all_private_key_ring_entries(person)

    @staticmethod
    def get_all_public_key_ring_entries(person) -> list:
        return KeyRings.get_all_public_key_ring_entries(person)

    @staticmethod
    def send_message(
            plaintext,
            sender,
            authentication,
            compression,
            confidentiality,
            radix64,
            private_key_user_id,
            private_key_key_id,
            private_key_password,
            public_key_user_id,
            public_key_key_id,
            confidentiality_algorithm
    ) -> str | dict:
        return Communication.send_message(
            plaintext,
            sender,
            authentication,
            compression,
            confidentiality,
            radix64,
            private_key_user_id,
            private_key_key_id,
            private_key_password,
            public_key_user_id,
            public_key_key_id,
            confidentiality_algorithm
        )

    @staticmethod
    def receive_message(
            receiver,
            pgp_message,
            private_key_password
    ) -> dict:
        return Communication.receive_message(receiver, pgp_message, private_key_password)
