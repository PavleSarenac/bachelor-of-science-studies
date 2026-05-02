package rs.ac.bg.etf.barberbooker.ui.stateholders.user.client

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ClientRepository
import javax.inject.Inject

data class ClientProfileUiState(
    var email: String = "",
    var name: String = "",
    var surname: String = "",

    var isNameValid: Boolean = true,
    var isSurnameValid: Boolean = true
)

@HiltViewModel
class ClientProfileViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientProfileUiState())
    val uiState = _uiState

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun setSurname(surname: String) {
        _uiState.update { it.copy(surname = surname) }
    }

    fun fetchClientData(clientEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        val client = clientRepository.getClientByEmail(clientEmail)
        if (client != null) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(
                    email = client.email,
                    name = client.name,
                    surname = client.surname
                ) }
            }
        }
    }

    fun updateProfile(
        clientEmail: String,
        snackbarHostState: SnackbarHostState,
        snackbarCoroutineScope: CoroutineScope
    ) = viewModelScope.launch(Dispatchers.Main) {
        val name = _uiState.value.name
        val surname = _uiState.value.surname

        if (!isNameValid(name) || !isSurnameValid(surname)) {
            snackbarCoroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Invalid data format!",
                    withDismissAction = true
                )
            }
            return@launch
        }

        withContext(Dispatchers.IO) {
            clientRepository.updateClientProfile(clientEmail, name, surname)
        }

        snackbarCoroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Profile updated!",
                withDismissAction = true
            )
        }
    }

    private fun isNameValid(name: String): Boolean {
        val isNameValid = name.isNotEmpty()
        if (!isNameValid) _uiState.update { it.copy(isNameValid = false) }
        else _uiState.update { it.copy(isNameValid = true) }
        return isNameValid
    }

    private fun isSurnameValid(surname: String): Boolean {
        val isSurnameValid = surname.isNotEmpty()
        if (!isSurnameValid) _uiState.update { it.copy(isSurnameValid = false) }
        else _uiState.update { it.copy(isSurnameValid = true) }
        return isSurnameValid
    }

}