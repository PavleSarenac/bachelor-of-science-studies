from datetime import datetime
import json
import os
import base64
from backend.authentication_algorithms.SHA1 import SHA1
from backend.confidentiality_algorithms.TripleDES import TripleDES
from rsa import PublicKey, PrivateKey


class KeyRings:
    current_script_path = os.path.dirname(__file__)
    paths = {
        "a": {
            "private_key_ring_path": os.path.join(current_script_path, f"files/user_a/key_rings/private_key_ring.json"),
            "public_key_ring_path": os.path.join(current_script_path, f"files/user_a/key_rings/public_key_ring.json"),
            "exported_private_key_path": os.path.join(current_script_path, f"files/user_a/exports/exported_private_key.json"),
            "exported_public_key_path": os.path.join(current_script_path, f"files/user_a/exports/exported_public_key.json")
        },
        "b": {
            "private_key_ring_path": os.path.join(current_script_path, f"files/user_b/key_rings/private_key_ring.json"),
            "public_key_ring_path": os.path.join(current_script_path, f"files/user_b/key_rings/public_key_ring.json"),
            "exported_private_key_path": os.path.join(current_script_path, f"files/user_b/exports/exported_private_key.json"),
            "exported_public_key_path": os.path.join(current_script_path, f"files/user_b/exports/exported_public_key.json")
        }
    }

    @staticmethod
    def insert_into_private_key_ring(person, user_name, user_email, private_key_password, public_key, private_key):
        all_entries = KeyRings.get_all_private_key_ring_entries(person)
        all_entries.append(KeyRings.create_new_private_key_ring_entry(user_name, user_email, private_key_password, public_key, private_key))
        with open(KeyRings.paths[person.lower()]["private_key_ring_path"], "w") as file:
            json.dump(all_entries, file, indent=4)

    @staticmethod
    def delete_entry_from_private_key_ring(person_deleting, person_affected, user_id, key_id, private_key_password) -> bool:
        all_entries = KeyRings.get_all_private_key_ring_entries(person_deleting)
        modified_entries = []
        is_deletion_successful = False
        for entry in all_entries:
            entry_not_found = not (entry["user_id"] == user_id and entry["key_id"] == key_id)
            if entry_not_found or not KeyRings.is_private_key_password_correct(entry, private_key_password):
                modified_entries.append(entry)
            else:
                is_deletion_successful = True
                KeyRings.delete_entry_from_public_key_ring(person_affected, user_id, key_id)
        with open(KeyRings.paths[person_deleting.lower()]["private_key_ring_path"], "w") as file:
            json.dump(modified_entries, file, indent=4)
        return is_deletion_successful

    @staticmethod
    def export_public_key(person, user_id, key_id) -> bool:
        entry = KeyRings.get_private_key_ring_entry(person, user_id, key_id)
        is_export_successful = False
        entry_without_private_key = KeyRings.create_new_public_key_ring_entry(entry)
        if entry is not None:
            with open(KeyRings.paths[person.lower()]["exported_public_key_path"], "w") as file:
                json.dump(entry_without_private_key, file, indent=4)
            is_export_successful = True
        return is_export_successful

    @staticmethod
    def import_public_key(import_person, export_person) -> dict:
        status = {
            "success": "",
            "failure": ""
        }
        if os.path.exists(KeyRings.paths[export_person.lower()]["exported_public_key_path"]):
            with open(KeyRings.paths[export_person.lower()]["exported_public_key_path"], "r") as file:
                new_entry = json.load(file)
            is_public_key_already_imported = KeyRings.get_public_key_ring_entry(import_person, new_entry["user_id"], new_entry["key_id"]) is not None
            is_public_key_valid = KeyRings.get_private_key_ring_entry(export_person, new_entry["user_id"], new_entry["key_id"]) is not None
            if not is_public_key_already_imported and is_public_key_valid:
                new_entry["timestamp"] = datetime.now().isoformat()
                all_entries = KeyRings.get_all_public_key_ring_entries(import_person)
                all_entries.append(new_entry)
                with open(KeyRings.paths[import_person.lower()]["public_key_ring_path"], "w") as file:
                    json.dump(all_entries, file, indent=4)
                status["success"] = "Public key was successfully imported!"
            else:
                if is_public_key_already_imported:
                    status["failure"] = "Exported public key is already in the public key ring!"
                elif not is_public_key_valid:
                    status["failure"] = "User who exported the public key has deleted it from their private key ring!"
        else:
            status["failure"] = "Exported public key is missing!"
        return status

    @staticmethod
    def delete_entry_from_public_key_ring(person, user_id, key_id) -> bool:
        all_entries = KeyRings.get_all_public_key_ring_entries(person)
        modified_entries = []
        is_deletion_successful = False
        for entry in all_entries:
            if not (entry["user_id"] == user_id and entry["key_id"] == key_id):
                modified_entries.append(entry)
            else:
                is_deletion_successful = True
        with open(KeyRings.paths[person.lower()]["public_key_ring_path"], "w") as file:
            json.dump(modified_entries, file, indent=4)
        return is_deletion_successful

    @staticmethod
    def export_private_key(person, user_id, key_id, private_key_password) -> bool:
        entry = KeyRings.get_private_key_ring_entry(person, user_id, key_id)
        is_export_successful = False
        if entry is not None and KeyRings.is_private_key_password_correct(entry, private_key_password):
            with open(KeyRings.paths[person.lower()]["exported_private_key_path"], "w") as file:
                json.dump(entry, file, indent=4)
            is_export_successful = True
        return is_export_successful

    @staticmethod
    def import_private_key(person) -> dict:
        status = {
            "success": "",
            "failure": ""
        }
        if os.path.exists(KeyRings.paths[person.lower()]["exported_private_key_path"]):
            with open(KeyRings.paths[person.lower()]["exported_private_key_path"], "r") as file:
                new_entry = json.load(file)
            if KeyRings.get_private_key_ring_entry(person, new_entry["user_id"], new_entry["key_id"]) is None:
                new_entry["timestamp"] = datetime.now().isoformat()
                all_entries = KeyRings.get_all_private_key_ring_entries(person)
                all_entries.append(new_entry)
                with open(KeyRings.paths[person.lower()]["private_key_ring_path"], "w") as file:
                    json.dump(all_entries, file, indent=4)
                status["success"] = "Key pair was successfully imported!"
            else:
                status["failure"] = "Exported key pair is already in the private key ring!"
        else:
            status["failure"] = "Exported key pair is missing!"
        return status

    @staticmethod
    def is_private_key_password_correct(entry, private_key_password) -> bool:
        private_key = KeyRings.get_private_key_from_entry(entry, private_key_password)
        if private_key is None:
            return False
        public_key_pem_format = entry["public_key_pem_format"].encode("utf-8")
        public_key = KeyRings.import_public_key_from_pem_format(public_key_pem_format)
        return (private_key.p * private_key.q) == public_key.n

    @staticmethod
    def get_all_private_key_ring_entries(person) -> list:
        all_entries = []
        if os.path.exists(KeyRings.paths[person.lower()]["private_key_ring_path"]):
            with open(KeyRings.paths[person.lower()]["private_key_ring_path"], "r") as file:
                all_entries = json.load(file)
        return all_entries

    @staticmethod
    def get_all_public_key_ring_entries(person) -> list:
        all_entries = []
        if os.path.exists(KeyRings.paths[person.lower()]["public_key_ring_path"]):
            with open(KeyRings.paths[person.lower()]["public_key_ring_path"], "r") as file:
                all_entries = json.load(file)
        return all_entries

    @staticmethod
    def get_private_key_ring_entry(person, user_id, key_id) -> dict | None:
        all_entries = KeyRings.get_all_private_key_ring_entries(person)
        for entry in all_entries:
            if entry["user_id"] == user_id and entry["key_id"] == key_id:
                return entry
        return None

    @staticmethod
    def get_private_key_ring_entry_by_key_id(person, key_id) -> dict | None:
        all_entries = KeyRings.get_all_private_key_ring_entries(person)
        for entry in all_entries:
            if entry["key_id"] == key_id:
                return entry
        return None

    @staticmethod
    def get_private_key_from_entry(entry, private_key_password) -> PrivateKey | None:
        try:
            encrypted_private_key_pem_format = base64.b64decode(entry["private_key_pem_format"]["encrypted_private_key_pem_format"])
            initialization_vector = base64.b64decode(entry["private_key_pem_format"]["initialization_vector"])
            key = SHA1.binary_digest(private_key_password) + b"\x00" * 4
            private_key_pem_format = TripleDES.decrypt(encrypted_private_key_pem_format, initialization_vector, key).decode("utf-8")
            private_key = KeyRings.import_private_key_from_pem_format(private_key_pem_format)
        except ValueError:
            return None
        return private_key

    @staticmethod
    def get_public_key_ring_entry(person, user_id, key_id) -> dict | None:
        all_entries = KeyRings.get_all_public_key_ring_entries(person)
        for entry in all_entries:
            if entry["user_id"] == user_id and entry["key_id"] == key_id:
                return entry
        return None

    @staticmethod
    def get_public_key_ring_entry_by_key_id(person, key_id) -> dict | None:
        all_entries = KeyRings.get_all_public_key_ring_entries(person)
        for entry in all_entries:
            if entry["key_id"] == key_id:
                return entry
        return None

    @staticmethod
    def get_public_key_from_entry(entry) -> PublicKey:
        return KeyRings.import_public_key_from_pem_format(entry["public_key_pem_format"].encode("utf-8"))

    @staticmethod
    def get_private_key(person, user_id, key_id, private_key_password) -> PrivateKey | None:
        entry = KeyRings.get_private_key_ring_entry(person, user_id, key_id)
        if entry is None:
            return None
        return KeyRings.get_private_key_from_entry(entry, private_key_password)

    @staticmethod
    def get_private_key_by_key_id(person, key_id, private_key_password) -> PrivateKey | None:
        entry = KeyRings.get_private_key_ring_entry_by_key_id(person, key_id)
        if entry is None:
            return None
        return KeyRings.get_private_key_from_entry(entry, private_key_password)

    @staticmethod
    def get_public_key(person, user_id, key_id) -> PublicKey | None:
        entry = KeyRings.get_public_key_ring_entry(person, user_id, key_id)
        if entry is None:
            return None
        return KeyRings.get_public_key_from_entry(entry)

    @staticmethod
    def get_public_key_by_key_id(person, key_id) -> PublicKey | None:
        entry = KeyRings.get_public_key_ring_entry_by_key_id(person, key_id)
        if entry is None:
            return None
        return KeyRings.get_public_key_from_entry(entry)

    @staticmethod
    def create_new_private_key_ring_entry(user_name, user_email, private_key_password, public_key, private_key) -> dict:
        initialization_vector, encrypted_private_key_pem_format = KeyRings.encrypt_private_key(private_key, private_key_password)
        new_entry = {
            "user_id": user_email,
            "key_id": str(public_key.n % pow(2, 64)),
            "timestamp": datetime.now().isoformat(),
            "user_name": user_name,
            "public_key_pem_format": KeyRings.export_key_to_pem_format(public_key).decode("utf-8"),
            "private_key_pem_format": {
                "encrypted_private_key_pem_format": base64.b64encode(encrypted_private_key_pem_format).decode("utf-8"),
                "initialization_vector": base64.b64encode(initialization_vector).decode("utf-8")
            }
        }
        return new_entry

    @staticmethod
    def create_new_public_key_ring_entry(export_person_private_key_ring_entry):
        new_entry = {
            "user_id": export_person_private_key_ring_entry["user_id"],
            "key_id": export_person_private_key_ring_entry["key_id"],
            "timestamp": export_person_private_key_ring_entry["timestamp"],
            "user_name": export_person_private_key_ring_entry["user_name"],
            "public_key_pem_format": export_person_private_key_ring_entry["public_key_pem_format"]
        }
        return new_entry

    @staticmethod
    def encrypt_private_key(private_key, private_key_password) -> tuple[bytes, bytes]:
        des3_key = SHA1.binary_digest(private_key_password) + b"\x00" * 4
        private_key_pem_format = KeyRings.export_key_to_pem_format(private_key)
        initialization_vector, encrypted_private_key_pem_format = TripleDES.encrypt(private_key_pem_format, des3_key)
        return initialization_vector, encrypted_private_key_pem_format

    @staticmethod
    def export_key_to_pem_format(key) -> bytes:
        return key.save_pkcs1(format="PEM")

    @staticmethod
    def import_public_key_from_pem_format(public_key_bytes) -> PublicKey:
        return PublicKey.load_pkcs1(keyfile=public_key_bytes, format="PEM")

    @staticmethod
    def import_private_key_from_pem_format(private_key_bytes) -> PrivateKey:
        return PrivateKey.load_pkcs1(keyfile=private_key_bytes, format="PEM")

