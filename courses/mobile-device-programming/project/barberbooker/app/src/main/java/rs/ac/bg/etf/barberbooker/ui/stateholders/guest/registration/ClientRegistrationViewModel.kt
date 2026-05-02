package rs.ac.bg.etf.barberbooker.ui.stateholders.guest.registration

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Client
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ClientRepository
import rs.ac.bg.etf.barberbooker.data.*
import java.security.MessageDigest
import javax.inject.Inject

data class ClientRegistrationUiState(
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var surname: String = "",

    var isEmailValid: Boolean = true,
    var isEmailAlreadyTaken: Boolean = false,
    var isPasswordValid: Boolean = true,
    var isNameValid: Boolean = true,
    var isSurnameValid: Boolean = true
)

@HiltViewModel
class ClientRegistrationViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientRegistrationUiState())
    val uiState = _uiState

    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun setSurname(surname: String) {
        _uiState.update { it.copy(surname = surname) }
    }

    fun registerClient(
        snackbarHostState: SnackbarHostState,
        navHostController: NavHostController,
        snackbarCoroutineScope: CoroutineScope
    ) = viewModelScope.launch(Dispatchers.Main) {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val name = _uiState.value.name
        val surname = _uiState.value.surname

        if (!isDataValid(email, password, name, surname)) {
            snackbarCoroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Invalid data format!",
                    withDismissAction = true
                )
            }
            return@launch
        }
        val isEmailAlreadyTaken = isEmailAlreadyTaken(email)
        if (isEmailAlreadyTaken) {
            snackbarCoroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Email already taken!",
                    withDismissAction = true
                )
            }
            return@launch
        }
        val md5HashedPassword = getSHA256HashedPassword(password)

        withContext(Dispatchers.IO) {
            addNewClient(email, md5HashedPassword, name, surname)
        }

        _uiState.update { ClientRegistrationUiState() }
        snackbarCoroutineScope.launch {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = "Registration successful!",
                withDismissAction = true,
                actionLabel = "Log in",
                duration = SnackbarDuration.Indefinite
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                navHostController.navigate(staticRoutes[LOG_IN_AS_CLIENT_SCREEN_ROUTE_INDEX])
            }
        }
    }

    private fun getSHA256HashedPassword(password: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hashBytes = sha256.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private suspend fun isEmailAlreadyTaken(email: String): Boolean {
        val isEmailAlreadyTaken = clientRepository.isEmailAlreadyTaken(email)
        _uiState.update { it.copy(isEmailAlreadyTaken = isEmailAlreadyTaken) }
        return isEmailAlreadyTaken
    }

    private suspend fun addNewClient(
        email: String,
        password: String,
        name: String,
        surname: String
    ) {
        val newClient = Client(0, email, password, name, surname, "")
        clientRepository.addNewClient(newClient)
    }

    private fun isDataValid(
        email: String,
        password: String,
        name: String,
        surname: String
    ): Boolean {
        var isDataValid = true
        if (!isEmailValid(email)) isDataValid = false
        if (!isPasswordValid(password)) isDataValid = false
        if (!isNameValid(name)) isDataValid = false
        if (!isSurnameValid(surname)) isDataValid = false
        return isDataValid
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$", RegexOption.IGNORE_CASE)
        val isEmailValid = emailRegex.matches(email)
        if (!isEmailValid) _uiState.update { it.copy(isEmailValid = false) }
        else _uiState.update { it.copy(isEmailValid = true) }
        return isEmailValid
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = Regex("^(?=[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])(?=.*[A-Z])(?=.*[a-z].*[a-z].*[a-z]).{6,10}$")
        val isPasswordValid = passwordRegex.matches(password)
        if (!isPasswordValid) _uiState.update { it.copy(isPasswordValid = false) }
        else _uiState.update { it.copy(isPasswordValid = true) }
        return isPasswordValid
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