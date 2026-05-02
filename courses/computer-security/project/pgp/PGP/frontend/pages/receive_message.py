import json
import os
import time

from PyQt5.QtCore import Qt
from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QSizePolicy, QComboBox, QPushButton, QFileDialog, QDialog

from backend.Communication import Communication
from backend.KeyRings import KeyRings
from backend.PGP import PGP
from frontend.utils.message_box import MessageBox
from frontend.utils.password_input_dialog import PasswordInputDialog


class ReceiveMessagePage(QWidget):
    def __init__(self):
        super().__init__()
        self.layout = QVBoxLayout()

        self.add_title()
        self.add_input_form()
        self.add_receive_button()

        self.setLayout(self.layout)

    def add_title(self):
        self.title_label = QLabel("Receive message", self)
        self.title_label.setAlignment(Qt.AlignCenter)
        title_font = self.title_label.font()
        title_font.setPointSize(32)
        self.title_label.setFont(title_font)
        self.layout.addWidget(self.title_label, alignment=Qt.AlignTop)

    def add_input_form(self):
        self.person_label = QLabel("Please say which user you are:", self)
        self.person_label.setSizePolicy(QSizePolicy.MinimumExpanding, QSizePolicy.Fixed)
        self.layout.addWidget(self.person_label)

        self.person_dropdown_menu = QComboBox(self)
        self.person_dropdown_menu.addItem("A")
        self.person_dropdown_menu.addItem("B")
        self.layout.addWidget(self.person_dropdown_menu)

    def add_receive_button(self):
        self.receive_button = QPushButton("Receive message", self)
        button_font = self.receive_button.font()
        button_font.setPointSize(12)
        self.receive_button.setFont(button_font)
        self.receive_button.clicked.connect(self.receive_message)
        self.layout.addWidget(self.receive_button)

    def receive_message(self):
        file_path, _ = QFileDialog.getOpenFileName(self, "Select file", "", "Text Files (*.txt)")
        if file_path:
            with open(file_path, "r") as file:
                received_message_string = file.read()
            received_message_dictionary = json.loads(received_message_string)
            private_key_password = ""
            if received_message_dictionary["is_encrypted"]:
                entry = self.get_private_key_ring_entry(received_message_dictionary)
                if entry is None:
                    MessageBox.show_error_message_box("Message decryption has failed!")
                    return
                password_dialog_label = f"Please enter your password for PrivateKey(user_id: {entry['user_id']}; key_id: {entry['key_id']})"
                password_dialog = PasswordInputDialog(password_dialog_label)
                if password_dialog.exec_() == QDialog.Accepted:
                    private_key_password = password_dialog.get_password()
            processed_message = PGP.receive_message(
                self.person_dropdown_menu.currentText(),
                received_message_string,
                private_key_password
            )
            if processed_message["decryption_error"] != "":
                MessageBox.show_error_message_box(processed_message["decryption_error"])
            elif processed_message["verification_error"] != "":
                MessageBox.show_error_message_box(processed_message["verification_error"])
            else:
                self.successful_message_receiving(processed_message)

    def get_private_key_ring_entry(self, received_message_dictionary) -> dict | None:
        receiver = self.person_dropdown_menu.currentText()
        if received_message_dictionary["is_radix64_encoded"]:
            received_message_dictionary = Communication.get_pgp_message_from_radix64_encoded_pgp_message(
                received_message_dictionary["pgp_message"]
            )
        key_id = received_message_dictionary["pgp_message"]["confidentiality"]["receiver_public_key_id"]
        entry = KeyRings.get_private_key_ring_entry_by_key_id(receiver, key_id)
        return entry

    def successful_message_receiving(self, processed_message):
        if processed_message["pgp_message"]["is_signed"]:
            MessageBox.show_success_message_box(
                "Message successfully verified!\n" +
                f"Sender user_id: {processed_message['verification']['sender_user_id']}\n" +
                f"Sender user_name: {processed_message['verification']['sender_user_name']}\n" +
                f"Message timestamp: {processed_message['verification']['message_timestamp']}"
            )
        folder_path = QFileDialog.getExistingDirectory(self, "Select Folder")
        if folder_path:
            current_time_milliseconds = str(int(time.time() * 1000))
            person = self.person_dropdown_menu.currentText()
            file_name = f"user_{person}_received_plaintext_{current_time_milliseconds}.txt"
            file_path = os.path.join(folder_path, file_name)
            with open(file_path, "w") as file:
                plaintext = processed_message["pgp_message"]["pgp_message"]["message_and_authentication"]["message"]["data"]
                file.write(plaintext)
            MessageBox.show_success_message_box("Received plaintext was successfully saved!")
