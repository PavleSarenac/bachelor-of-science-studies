from PyQt5.QtWidgets import QMessageBox


class MessageBox():
    @staticmethod
    def show_error_message_box(error_text):
        error_message_box = QMessageBox()
        error_message_box.setIcon(QMessageBox.Warning)
        error_message_box.setText(error_text)
        error_message_box.setWindowTitle("Error")
        error_message_box.exec_()

    @staticmethod
    def show_success_message_box(success_text):
        success_message_box = QMessageBox()
        success_message_box.setIcon(QMessageBox.Information)
        success_message_box.setText(success_text)
        success_message_box.setWindowTitle("Success")
        success_message_box.exec_()