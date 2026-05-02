from PyQt5.QtCore import Qt
from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QLineEdit, QComboBox, QSizePolicy, QPushButton, QMessageBox

from backend.PGP import PGP
from frontend.utils.message_box import MessageBox


class GenerateNewRsaKeyPairPage(QWidget):
    def __init__(self, showKeyRingsPage, sendMessagePage):
        super().__init__()
        self.layout = QVBoxLayout()
        self.showKeyRingsPage = showKeyRingsPage
        self.sendMessagePage = sendMessagePage

        self.add_title()
        self.add_input_form()
        self.add_button()

        self.setLayout(self.layout)

    def add_title(self):
        self.title_label = QLabel("New RSA key pair generation", self)
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

        self.user_name_input_field = QLineEdit(self)
        self.user_name_input_field.setPlaceholderText("Please enter your name...")
        self.layout.addWidget(self.user_name_input_field)

        self.user_email_input_field = QLineEdit(self)
        self.user_email_input_field.setPlaceholderText("Please enter your email...")
        self.layout.addWidget(self.user_email_input_field)

        self.rsa_key_size_label = QLabel("Please choose RSA key size in bits:", self)
        self.rsa_key_size_label.setSizePolicy(QSizePolicy.MinimumExpanding, QSizePolicy.Fixed)
        self.layout.addWidget(self.rsa_key_size_label)

        self.rsa_key_size_dropdown_menu = QComboBox(self)
        self.rsa_key_size_dropdown_menu.addItem("1024")
        self.rsa_key_size_dropdown_menu.addItem("2048")
        self.layout.addWidget(self.rsa_key_size_dropdown_menu)

        self.user_password_input_field = QLineEdit(self)
        self.user_password_input_field.setPlaceholderText("Please enter your password...")
        self.user_password_input_field.setEchoMode(QLineEdit.Password)
        self.layout.addWidget(self.user_password_input_field)

    def add_button(self):
        self.generate_new_rsa_key_pair_button = QPushButton("Generate new RSA key pair", self)
        button_font = self.generate_new_rsa_key_pair_button.font()
        button_font.setPointSize(12)
        self.generate_new_rsa_key_pair_button.setFont(button_font)
        self.generate_new_rsa_key_pair_button.clicked.connect(self.on_button_click)
        self.layout.addWidget(self.generate_new_rsa_key_pair_button)

    def on_button_click(self):
        person = self.person_dropdown_menu.currentText()
        user_name = self.user_name_input_field.text()
        user_email = self.user_email_input_field.text()
        key_size_in_bits = int(self.rsa_key_size_dropdown_menu.currentText())
        private_key_password = self.user_password_input_field.text()
        if user_name == "" or user_email == "" or private_key_password == "":
            MessageBox.show_error_message_box("All input fields must be filled out!")
        else:
            PGP.generate_new_rsa_key_pair(person, user_name, user_email, key_size_in_bits, private_key_password)
            MessageBox.show_success_message_box(f"New RSA key pair for user {person} was generated successfully!")
            self.showKeyRingsPage.update_tables()
            self.sendMessagePage.update_authentication_dropdown()
            self.clear_input_form()

    def clear_input_form(self):
        self.user_name_input_field.setText("")
        self.user_email_input_field.setText("")
        self.rsa_key_size_dropdown_menu.setCurrentIndex(0)
        self.user_password_input_field.setText("")
        self.person_dropdown_menu.setCurrentIndex(0)
