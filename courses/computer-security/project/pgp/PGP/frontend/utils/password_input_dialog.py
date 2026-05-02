from PyQt5.QtWidgets import QDialog, QVBoxLayout, QLabel, QLineEdit, QPushButton, QHBoxLayout


class PasswordInputDialog(QDialog):
    def __init__(self, dialog_label="", parent=None):
        super().__init__(parent)
        self.dialog_label = dialog_label
        self.setWindowTitle("Password")
        self.setMinimumSize(200, 100)
        self.dialog_layout = QVBoxLayout(self)
        self.add_label()
        self.add_input_field()
        self.add_buttons()

    def add_label(self):
        dialog_label = "Please enter the password:" if self.dialog_label == "" else self.dialog_label
        self.label = QLabel(dialog_label, self)
        self.dialog_layout.addWidget(self.label)

    def add_input_field(self):
        self.password_input_field = QLineEdit(self)
        self.password_input_field.setEchoMode(QLineEdit.Password)
        self.dialog_layout.addWidget(self.password_input_field)

    def add_buttons(self):
        self.button_layout = QHBoxLayout()

        self.ok_button = QPushButton("OK", self)
        self.ok_button.clicked.connect(self.accept)
        self.button_layout.addWidget(self.ok_button)

        self.cancel_button = QPushButton("Cancel", self)
        self.cancel_button.clicked.connect(self.reject)
        self.button_layout.addWidget(self.cancel_button)

        self.dialog_layout.addLayout(self.button_layout)

    def get_password(self):
        return self.password_input_field.text()
