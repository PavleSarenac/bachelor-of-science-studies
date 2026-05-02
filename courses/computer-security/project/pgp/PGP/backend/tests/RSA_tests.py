from backend.KeyRings import KeyRings
from backend.authentication_algorithms.RSA import RSA
from rsa import PublicKey, PrivateKey


def new_key_pair_generation() -> tuple[PublicKey, PrivateKey]:
    print("###########################################################################################################")
    print("NEW KEY PAIR GENERATION")
    print("###########################################################################################################")
    person = "A"
    user_name = "Ljubica"
    user_email = "ljubmajstorovic9@gmail.com"
    key_size_in_bits = 1024
    private_key_password = "bicabica"
    public_key, private_key = RSA.generate_new_key_pair(person, user_name, user_email, key_size_in_bits, private_key_password)
    KeyRings.delete_entry_from_private_key_ring(person, "B", user_email, str(public_key.n % pow(2, 64)), private_key_password)
    print(public_key)
    print(private_key)
    print("###########################################################################################################")
    print()
    return public_key, private_key


def key_pair_export_to_pem_format(public_key, private_key) -> tuple[bytes, bytes]:
    print("###########################################################################################################")
    print("KEY PAIR EXPORT TO PEM FORMAT")
    print("###########################################################################################################")
    public_key_pem_format = KeyRings.export_key_to_pem_format(public_key)
    private_key_pem_format = KeyRings.export_key_to_pem_format(private_key)
    print(public_key_pem_format.decode("utf-8"))
    print(private_key_pem_format.decode("utf-8"))
    print("###########################################################################################################")
    print()
    return public_key_pem_format, private_key_pem_format


def key_pair_import_from_pem_format(public_key_pem_format, private_key_pem_format) -> tuple[PublicKey, PrivateKey]:
    print("###########################################################################################################")
    print("KEY PAIR IMPORT FROM PEM FORMAT")
    print("###########################################################################################################")
    public_key = KeyRings.import_public_key_from_pem_format(public_key_pem_format)
    private_key = KeyRings.import_private_key_from_pem_format(private_key_pem_format)
    print(public_key)
    print(private_key)
    print("###########################################################################################################")
    print()
    return public_key, private_key


def rsa_encryption(public_key) -> tuple[str, bytes]:
    plaintext_string = "Let's meet up tomorrow at 6."
    ciphertext_bytes = RSA.encrypt(plaintext_string, public_key)
    print("###########################################################################################################")
    print("RSA ENCRYPTION")
    print("###########################################################################################################")
    print(f"Plaintext string: {plaintext_string}")
    print(f"Ciphertext bytes: {ciphertext_bytes}")
    print("###########################################################################################################")
    print()
    return plaintext_string, ciphertext_bytes


def rsa_decryption(ciphertext_bytes, private_key) -> bytes:
    plaintext_bytes = RSA.decrypt(ciphertext_bytes, private_key)
    print("###########################################################################################################")
    print("RSA DECRYPTION")
    print("###########################################################################################################")
    print(f"Plaintext bytes: {plaintext_bytes}")
    print("###########################################################################################################")
    print()
    return plaintext_bytes


def message_signing(plaintext_string, private_key) -> bytes | None:
    signature = RSA.sign_message(plaintext_string, private_key)
    print("###########################################################################################################")
    print("MESSAGE SIGNING")
    print("###########################################################################################################")
    print(f"Signature bytes: {signature}")
    print("###########################################################################################################")
    print()
    return signature


def signature_verification(plaintext_string, signature, public_key) -> bool:
    verification = RSA.verify_message(plaintext_string, signature, public_key)
    print("###########################################################################################################")
    print("SIGNATURE VERIFICATION")
    print("###########################################################################################################")
    if verification:
        print("Signature successfully verified.")
    else:
        print("Signature is not valid.")
    print("###########################################################################################################")
    print()
    return verification


def test_RSA():
    # NEW KEY PAIR GENERATION
    public_key, private_key = new_key_pair_generation()
    # KEY PAIR EXPORT TO PEM FORMAT
    public_key_pem_format, private_key_pem_format = key_pair_export_to_pem_format(public_key, private_key)
    # KEY PAIR IMPORT FROM PEM FORMAT
    public_key, private_key = key_pair_import_from_pem_format(public_key_pem_format, private_key_pem_format)
    # RSA ENCRYPTION
    plaintext_string, ciphertext_bytes = rsa_encryption(public_key)
    # RSA DECRYPTION
    plaintext_string = rsa_decryption(ciphertext_bytes, private_key)
    # MESSAGE SIGNING
    signature = message_signing(plaintext_string, private_key)
    # SIGNATURE VERIFICATION
    signature_verification(plaintext_string, signature, public_key)


def main():
    test_RSA()


if __name__ == "__main__":
    main()
