from backend.authentication_algorithms.SHA1 import SHA1
from backend.confidentiality_algorithms.TripleDES import TripleDES


def hash_generation(plaintext_string) -> bytes:
    sha1_digest = SHA1.binary_digest(plaintext_string)
    print("###########################################################################################################")
    print("HASH GENERATION FOR PLAINTEXT")
    print("###########################################################################################################")
    print(f"Input string: {plaintext_string}")
    print(f"SHA-1 digest: {sha1_digest}")
    print("###########################################################################################################")
    print()
    return sha1_digest


def encryption(plaintext, key) -> tuple[bytes, bytes]:
    initialization_vector_bytes, ciphertext_bytes = TripleDES.encrypt(plaintext, key)
    print("###########################################################################################################")
    print("ENCRYPTION USING HASH AS KEY")
    print("###########################################################################################################")
    print(f"Plaintext string: {plaintext}")
    print(f"Key bytes: {key}")
    print(f"Ciphertext bytes: {ciphertext_bytes}")
    print("###########################################################################################################")
    print()
    return initialization_vector_bytes, ciphertext_bytes


def decryption(ciphertext, key, initialization_vector) -> bytes:
    plaintext_bytes = TripleDES.decrypt(ciphertext, initialization_vector, key)
    print("###########################################################################################################")
    print("DECRYPTION USING HASH AS KEY")
    print("###########################################################################################################")
    print(f"Ciphertext bytes: {ciphertext}")
    print(f"Key bytes: {key}")
    print(f"Plaintext string: {plaintext_bytes}")
    print("###########################################################################################################")
    print()
    return plaintext_bytes


def test_TripleDES():
    plaintext_string = "Let's meet up tomorrow at 6."
    # HASH GENERATION FOR PLAINTEXT
    sha1_digest = hash_generation(plaintext_string) + b"\x00" * 4
    # ENCRYPTION USING HASH AS KEY
    initialization_vector_bytes, ciphertext_bytes = encryption(plaintext_string, sha1_digest)
    # DECRYPTION USING HASH AS KEY
    decryption(ciphertext_bytes, sha1_digest, initialization_vector_bytes)


def main():
    test_TripleDES()


if __name__ == "__main__":
    main()
