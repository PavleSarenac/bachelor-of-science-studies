import hashlib


class SHA1:
    @staticmethod
    def binary_digest(input_string) -> bytes:
        sha1 = hashlib.sha1()
        input_bytes = input_string.encode("utf-8")
        sha1.update(input_bytes)
        return sha1.digest()
