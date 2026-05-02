from PyQt5.QtCore import Qt
from PyQt5.QtWidgets import QWidget, QVBoxLayout, QLabel, QTableWidget, QTableWidgetItem, QComboBox, QSizePolicy, \
    QPushButton, QDialog
from backend.PGP import PGP
from frontend.utils.message_box import MessageBox
from frontend.utils.password_input_dialog import PasswordInputDialog


class ShowKeyRingsPage(QWidget):
    def __init__(self, sendMessagePage):
        super().__init__()
        self.layout = QVBoxLayout()
        self.sendMessagePage = sendMessagePage

        self.add_title()
        self.add_dropdown_menu()
        self.add_private_key_ring_table()
        self.add_import_key_pair_button()
        self.add_public_key_ring_table()
        self.add_import_public_key_button()

        self.layout.setAlignment(Qt.AlignTop)
        self.setLayout(self.layout)

    def add_title(self):
        self.title_label = QLabel("Key rings", self)
        self.title_label.setAlignment(Qt.AlignCenter)
        title_font = self.title_label.font()
        title_font.setPointSize(32)
        self.title_label.setFont(title_font)
        self.layout.addWidget(self.title_label, alignment=Qt.AlignTop)

    def add_dropdown_menu(self):
        self.person_label = QLabel("Please say which user you are:", self)
        self.person_label.setSizePolicy(QSizePolicy.MinimumExpanding, QSizePolicy.Fixed)
        self.layout.addWidget(self.person_label)

        self.person_dropdown_menu = QComboBox(self)
        self.person_dropdown_menu.addItem("A")
        self.person_dropdown_menu.addItem("B")
        self.layout.addWidget(self.person_dropdown_menu, alignment=Qt.AlignTop)

        self.person_dropdown_menu.currentTextChanged.connect(self.update_tables)

    def add_private_key_ring_table(self):
        self.add_private_key_ring_table_label()
        self.private_key_ring_table = QTableWidget()
        self.private_key_ring_table.setRowCount(0)
        self.private_key_ring_table.setColumnCount(10)
        self.private_key_ring_table.setHorizontalHeaderLabels([
            "user_id",
            "key_id",
            "timestamp",
            "user_name",
            "public_key_pem_format",
            "encrypted_private_key_pem_format",
            "initialization_vector",
            "delete_key_pair",
            "export_key_pair",
            "export_public_key"
        ])
        self.private_key_ring_table.resizeColumnsToContents()
        self.layout.addWidget(self.private_key_ring_table)
        self.populate_private_key_ring_table()

    def add_import_key_pair_button(self):
        self.import_key_pair_button = QPushButton("Import key pair", self)
        self.import_key_pair_button.clicked.connect(self.import_key_pair)
        import_button_font = self.import_key_pair_button.font()
        import_button_font.setPointSize(12)
        self.import_key_pair_button.setFont(import_button_font)
        self.layout.addWidget(self.import_key_pair_button)

    def add_import_public_key_button(self):
        self.import_public_key_button = QPushButton("Import public key", self)
        self.import_public_key_button.clicked.connect(self.import_public_key)
        import_button_font = self.import_public_key_button.font()
        import_button_font.setPointSize(12)
        self.import_public_key_button.setFont(import_button_font)
        self.layout.addWidget(self.import_public_key_button)

    def import_key_pair(self):
        person = self.person_dropdown_menu.currentText()
        import_status = PGP.import_private_key(person)
        if import_status["success"] != "":
            self.update_tables()
            self.sendMessagePage.update_authentication_dropdown()
            MessageBox.show_success_message_box(import_status["success"])
        else:
            MessageBox.show_error_message_box(import_status["failure"])

    def import_public_key(self):
        import_person = self.person_dropdown_menu.currentText()
        export_person = "B" if import_person == "A" else "A"
        import_status = PGP.import_public_key(import_person, export_person)
        if import_status["success"] != "":
            self.update_tables()
            self.sendMessagePage.update_confidentiality_dropdown()
            MessageBox.show_success_message_box(import_status["success"])
        else:
            MessageBox.show_error_message_box(import_status["failure"])

    def add_public_key_ring_table(self):
        self.add_public_key_ring_table_label()
        self.public_key_ring_table = QTableWidget()
        self.public_key_ring_table.setRowCount(0)
        self.public_key_ring_table.setColumnCount(6)
        self.public_key_ring_table.setHorizontalHeaderLabels([
            "user_id",
            "key_id",
            "timestamp",
            "user_name",
            "public_key_pem_format",
            "delete_public_key"
        ])
        self.public_key_ring_table.resizeColumnsToContents()
        self.layout.addWidget(self.public_key_ring_table)
        self.populate_public_key_ring_table()

    def add_private_key_ring_table_label(self):
        self.title_label = QLabel("Private key ring", self)
        self.title_label.setAlignment(Qt.AlignCenter)
        title_font = self.title_label.font()
        title_font.setPointSize(24)
        self.title_label.setFont(title_font)
        self.layout.addWidget(self.title_label, alignment=Qt.AlignTop)

    def add_public_key_ring_table_label(self):
        self.title_label = QLabel("Public key ring", self)
        self.title_label.setAlignment(Qt.AlignCenter)
        title_font = self.title_label.font()
        title_font.setPointSize(24)
        self.title_label.setFont(title_font)
        self.layout.addWidget(self.title_label, alignment=Qt.AlignTop)

    def populate_private_key_ring_table(self):
        person = self.person_dropdown_menu.currentText()
        all_entries = PGP.get_private_key_ring(person)
        for entry in all_entries:
            self.add_row_to_private_key_ring_table(
                entry["user_id"],
                entry["key_id"],
                entry["timestamp"],
                entry["user_name"],
                entry["public_key_pem_format"],
                entry["private_key_pem_format"]["encrypted_private_key_pem_format"],
                entry["private_key_pem_format"]["initialization_vector"]
            )
        for row in range(self.private_key_ring_table.rowCount()):
            for column in range(self.private_key_ring_table.columnCount()):
                item = self.private_key_ring_table.item(row, column)
                if item:
                    item.setFlags(item.flags() & ~Qt.ItemIsEditable)
                    item.setData(Qt.ToolTipRole, item.text())

    def populate_public_key_ring_table(self):
        person = self.person_dropdown_menu.currentText()
        all_entries = PGP.get_public_key_ring(person)
        for entry in all_entries:
            self.add_row_to_public_key_ring_table(
                entry["user_id"],
                entry["key_id"],
                entry["timestamp"],
                entry["user_name"],
                entry["public_key_pem_format"]
            )
        for row in range(self.public_key_ring_table.rowCount()):
            for column in range(self.public_key_ring_table.columnCount()):
                item = self.public_key_ring_table.item(row, column)
                if item:
                    item.setFlags(item.flags() & ~Qt.ItemIsEditable)
                    item.setData(Qt.ToolTipRole, item.text())

    def add_row_to_private_key_ring_table(
            self,
            user_id,
            key_id,
            timestamp,
            user_name,
            public_key_pem_format,
            encrypted_private_key_pem_format,
            initialization_vector
    ):
        new_row_index = self.private_key_ring_table.rowCount()
        self.private_key_ring_table.insertRow(new_row_index)

        self.private_key_ring_table.setItem(new_row_index, 0, QTableWidgetItem(user_id))
        self.private_key_ring_table.setItem(new_row_index, 1, QTableWidgetItem(key_id))
        self.private_key_ring_table.setItem(new_row_index, 2, QTableWidgetItem(timestamp))
        self.private_key_ring_table.setItem(new_row_index, 3, QTableWidgetItem(user_name))
        self.private_key_ring_table.setItem(new_row_index, 4, QTableWidgetItem(public_key_pem_format))
        self.private_key_ring_table.setItem(new_row_index, 5, QTableWidgetItem(encrypted_private_key_pem_format))
        self.private_key_ring_table.setItem(new_row_index, 6, QTableWidgetItem(initialization_vector))

        delete_row_button = QPushButton("Delete key pair")
        delete_row_button.clicked.connect(lambda: self.delete_private_key_ring_row(delete_row_button))
        self.private_key_ring_table.setCellWidget(new_row_index, 7, delete_row_button)

        export_key_pair_button = QPushButton("Export key pair")
        export_key_pair_button.clicked.connect(lambda: self.export_key_pair(export_key_pair_button))
        self.private_key_ring_table.setCellWidget(new_row_index, 8, export_key_pair_button)

        export_public_key_button = QPushButton("Export public key")
        export_public_key_button.clicked.connect(lambda: self.export_public_key(export_public_key_button))
        self.private_key_ring_table.setCellWidget(new_row_index, 9, export_public_key_button)

    def delete_private_key_ring_row(self, delete_row_button):
        row_index = self.private_key_ring_table.indexAt(delete_row_button.pos()).row()
        person_deleting = self.person_dropdown_menu.currentText()
        person_affected = "B" if person_deleting == "A" else "A"
        user_id = self.private_key_ring_table.item(row_index, 0).text()
        key_id = self.private_key_ring_table.item(row_index, 1).text()
        password_dialog = PasswordInputDialog()
        if password_dialog.exec_() == QDialog.Accepted:
            private_key_password = password_dialog.get_password()
            if PGP.delete_rsa_key_pair_from_private_key_ring(
                    person_deleting,
                    person_affected,
                    user_id,
                    key_id,
                    private_key_password
            ):
                self.private_key_ring_table.removeRow(row_index)
                self.sendMessagePage.update_authentication_dropdown()
                MessageBox.show_success_message_box("Selected key pair was successfully deleted!")
            else:
                MessageBox.show_error_message_box("Incorrect password!")

    def export_key_pair(self, export_key_pair_button):
        row_index = self.private_key_ring_table.indexAt(export_key_pair_button.pos()).row()
        person = self.person_dropdown_menu.currentText()
        user_id = self.private_key_ring_table.item(row_index, 0).text()
        key_id = self.private_key_ring_table.item(row_index, 1).text()
        password_dialog = PasswordInputDialog()
        if password_dialog.exec_() == QDialog.Accepted:
            private_key_password = password_dialog.get_password()
            if PGP.export_private_key(person, user_id, key_id, private_key_password):
                MessageBox.show_success_message_box("Selected key pair was successfully exported!")
            else:
                MessageBox.show_error_message_box("Incorrect password!")

    def export_public_key(self, export_public_key_button):
        row_index = self.private_key_ring_table.indexAt(export_public_key_button.pos()).row()
        person = self.person_dropdown_menu.currentText()
        user_id = self.private_key_ring_table.item(row_index, 0).text()
        key_id = self.private_key_ring_table.item(row_index, 1).text()
        if PGP.export_public_key(person, user_id, key_id):
            MessageBox.show_success_message_box("Selected public key was successfully exported!")
        else:
            MessageBox.show_error_message_box("Export of the selected public key failed!")

    def add_row_to_public_key_ring_table(
            self,
            user_id,
            key_id,
            timestamp,
            user_name,
            public_key_pem_format
    ):
        new_row_index = self.public_key_ring_table.rowCount()
        self.public_key_ring_table.insertRow(new_row_index)

        self.public_key_ring_table.setItem(new_row_index, 0, QTableWidgetItem(user_id))
        self.public_key_ring_table.setItem(new_row_index, 1, QTableWidgetItem(key_id))
        self.public_key_ring_table.setItem(new_row_index, 2, QTableWidgetItem(timestamp))
        self.public_key_ring_table.setItem(new_row_index, 3, QTableWidgetItem(user_name))
        self.public_key_ring_table.setItem(new_row_index, 4, QTableWidgetItem(public_key_pem_format))

        delete_row_button = QPushButton("Delete public key")
        delete_row_button.clicked.connect(lambda: self.delete_public_key_ring_row(delete_row_button))
        self.public_key_ring_table.setCellWidget(new_row_index, 5, delete_row_button)

    def delete_public_key_ring_row(self, delete_row_button):
        row_index = self.public_key_ring_table.indexAt(delete_row_button.pos()).row()
        person_deleting = self.person_dropdown_menu.currentText()
        user_id = self.public_key_ring_table.item(row_index, 0).text()
        key_id = self.public_key_ring_table.item(row_index, 1).text()
        if PGP.delete_public_key_from_public_key_ring(person_deleting, user_id, key_id):
            self.public_key_ring_table.removeRow(row_index)
            self.sendMessagePage.update_confidentiality_dropdown()
            MessageBox.show_success_message_box("Selected public key was successfully deleted!")
        else:
            MessageBox.show_error_message_box("Deletion of selected public key failed!")

    def update_tables(self):
        self.private_key_ring_table.setRowCount(0)
        self.public_key_ring_table.setRowCount(0)
        self.populate_private_key_ring_table()
        self.populate_public_key_ring_table()
