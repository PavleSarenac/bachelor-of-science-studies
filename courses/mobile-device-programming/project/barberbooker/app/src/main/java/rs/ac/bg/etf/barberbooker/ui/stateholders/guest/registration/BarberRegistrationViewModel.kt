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
import rs.ac.bg.etf.barberbooker.data.daysOfTheWeek
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Barber
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.BarberRepository
import rs.ac.bg.etf.barberbooker.data.*
import java.security.MessageDigest
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BarberRegistrationUiState(
    var email: String = "",
    var password: String = "",
    var barbershopName: String = "",
    var surname: String = "",
    var phone: String = "",

    var price: String = "",
    var country: String = "",
    var city: String = "",
    var municipality: String = "",
    var streetName: String = "",
    var streetNumber: String = "",

    var isEmailValid: Boolean = true,
    var isEmailAlreadyTaken: Boolean = false,
    var isPasswordValid: Boolean = true,
    var isBarbershopNameValid: Boolean = true,
    var isPhoneValid: Boolean = true,

    var isPriceValid: Boolean = true,
    var isCountryValid: Boolean = true,
    var isCityValid: Boolean = true,
    var isMunicipalityValid: Boolean = true,
    var isStreetNameValid: Boolean = true,
    var isStreetNumberValid: Boolean = true,

    var allValidWorkingDayTimes: List<String> = listOf(),
    var validWorkingDayStartTimes: List<String> = listOf(),
    var validWorkingDayEndTimes: List<String> = listOf(),
    var selectedWorkingDayStartTime: String = "",
    var selectedWorkingDayEndTime: String = ""
)

@HiltViewModel
class BarberRegistrationViewModel @Inject constructor(
    private val barberRepository: BarberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberRegistrationUiState())
    val uiState = _uiState

    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun setBarbershopName(barbershopName: String) {
        _uiState.update { it.copy(barbershopName = barbershopName) }
    }

    fun setPhone(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun setPrice(price: String) {
        _uiState.update { it.copy(price = price) }
    }

    fun setCountry(country: String) {
        _uiState.update { it.copy(country = country) }
    }

    fun setCity(city: String) {
        _uiState.update { it.copy(city = city) }
    }

    fun setMunicipality(municipality: String) {
        _uiState.update { it.copy(municipality = municipality) }
    }

    fun setStreetName(streetName: String) {
        _uiState.update { it.copy(streetName = streetName) }
    }

    fun setStreetNumber(streetNumber: String) {
        _uiState.update { it.copy(streetNumber = streetNumber) }
    }

    fun registerBarber(
        snackbarHostState: SnackbarHostState,
        navHostController: NavHostController,
        workingDayStartTime: String,
        workingDayEndTime: String,
        isMondayChecked: Boolean,
        isTuesdayChecked: Boolean,
        isWednesdayChecked: Boolean,
        isThursdayChecked: Boolean,
        isFridayChecked: Boolean,
        isSaturdayChecked: Boolean,
        isSundayChecked: Boolean,
        snackbarCoroutineScope: CoroutineScope
    ) = viewModelScope.launch(Dispatchers.Main) {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val barbershopName = _uiState.value.barbershopName
        val phone = _uiState.value.phone
        val price = _uiState.value.price
        val country = _uiState.value.country
        val city = _uiState.value.city
        val municipality = _uiState.value.municipality
        val streetName = _uiState.value.streetName
        val streetNumber = _uiState.value.streetNumber
        val selectedWorkingDays = getSelectedWorkingDays(
            isMondayChecked,
            isTuesdayChecked,
            isWednesdayChecked,
            isThursdayChecked,
            isFridayChecked,
            isSaturdayChecked,
            isSundayChecked,
        )

        if (!isDataValid(
                email,
                password,
                barbershopName,
                phone,
                price,
                country,
                city,
                municipality,
                streetName,
                streetNumber,
                workingDayStartTime
        )) {
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
        val sha256HashedPassword = getSHA256HashedPassword(password)

        withContext(Dispatchers.IO) {
            addNewBarber(
                email,
                sha256HashedPassword,
                barbershopName,
                price,
                phone,
                country,
                city,
                municipality,
                "$streetName $streetNumber",
                selectedWorkingDays,
                "$workingDayStartTime - $workingDayEndTime"
            )
        }

        _uiState.update { BarberRegistrationUiState() }
        snackbarCoroutineScope.launch {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = "Registration successful!",
                withDismissAction = true,
                actionLabel = "Log in",
                duration = SnackbarDuration.Indefinite
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                navHostController.navigate(staticRoutes[LOG_IN_AS_BARBER_SCREEN_ROUTE_INDEX])
            }
        }
    }

    private fun getSHA256HashedPassword(password: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hashBytes = sha256.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private suspend fun addNewBarber(
        email: String,
        password: String,
        barbershopName: String,
        price: String,
        phone: String,
        country: String,
        city: String,
        municipality: String,
        address: String,
        workingDays: String,
        workingHours: String
    ) {
        val newBarber = Barber(
            0,
            email,
            password,
            barbershopName,
            price.toDouble(),
            phone,
            country,
            city,
            municipality,
            address,
            workingDays,
            workingHours,
            ""
        )
        barberRepository.addNewBarber(newBarber)
    }

    private fun isDataValid(
        email: String,
        password: String,
        barbershopName: String,
        phone: String,
        price: String,
        country: String,
        city: String,
        municipality: String,
        streetName: String,
        streetNumber: String,
        selectedWorkingDays: String
    ): Boolean {
        var isDataValid = true
        if (!isEmailValid(email)) isDataValid = false
        if (!isPasswordValid(password)) isDataValid = false
        if (!isBarbershopNameValid(barbershopName)) isDataValid = false
        if (!isPhoneValid(phone)) isDataValid = false
        if (!isPriceValid(price)) isDataValid = false
        if (!isCountryValid(country)) isDataValid = false
        if (!isCityValid(city)) isDataValid = false
        if (!isMunicipalityValid(municipality)) isDataValid = false
        if (!isStreetNameValid(streetName)) isDataValid = false
        if (!isStreetNumberValid(streetNumber)) isDataValid = false
        if (!areSelectedWorkingDaysValid(selectedWorkingDays)) isDataValid = false
        return isDataValid
    }

    private fun getSelectedWorkingDays(
        isMondayChecked: Boolean,
        isTuesdayChecked: Boolean,
        isWednesdayChecked: Boolean,
        isThursdayChecked: Boolean,
        isFridayChecked: Boolean,
        isSaturdayChecked: Boolean,
        isSundayChecked: Boolean
    ): String {
        var selectedWorkingDays = ""
        if (isMondayChecked) {
            selectedWorkingDays += daysOfTheWeek[0]
        }
        if (isTuesdayChecked) {
            selectedWorkingDays += if (selectedWorkingDays == "") {
                daysOfTheWeek[1]
            }
            else {
                ", ${daysOfTheWeek[1]}"
            }
        }
        if (isWednesdayChecked) {
            selectedWorkingDays += if (selectedWorkingDays == "") {
                daysOfTheWeek[2]
            }
            else {
                ", ${daysOfTheWeek[2]}"
            }
        }
        if (isThursdayChecked) {
            selectedWorkingDays += if (selectedWorkingDays == "") {
                daysOfTheWeek[3]
            }
            else {
                ", ${daysOfTheWeek[3]}"
            }
        }
        if (isFridayChecked) {
            selectedWorkingDays += if (selectedWorkingDays == "") {
                daysOfTheWeek[4]
            }
            else {
                ", ${daysOfTheWeek[4]}"
            }
        }
        if (isSaturdayChecked) {
            selectedWorkingDays += if (selectedWorkingDays == "") {
                daysOfTheWeek[5]
            }
            else {
                ", ${daysOfTheWeek[5]}"
            }
        }
        if (isSundayChecked) {
            selectedWorkingDays += if (selectedWorkingDays == "") {
                daysOfTheWeek[6]
            }
            else {
                ", ${daysOfTheWeek[6]}"
            }
        }
        return selectedWorkingDays
    }

    private suspend fun isEmailAlreadyTaken(email: String): Boolean {
        val isEmailAlreadyTaken = barberRepository.isEmailAlreadyTaken(email)
        _uiState.update { it.copy(isEmailAlreadyTaken = isEmailAlreadyTaken) }
        return isEmailAlreadyTaken
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

    private fun isBarbershopNameValid(barbershopName: String): Boolean {
        val isBarbershopNameValid = barbershopName.isNotEmpty()
        if (!isBarbershopNameValid) _uiState.update { it.copy(isBarbershopNameValid = false) }
        else _uiState.update { it.copy(isBarbershopNameValid = true) }
        return isBarbershopNameValid
    }

    private fun isPhoneValid(phone: String): Boolean {
        val phoneRegex = Regex("^06[0-6]/[0-9]{3}-[0-9]{3,4}$")
        val isPhoneValid = phoneRegex.matches(phone)
        if (!isPhoneValid) _uiState.update { it.copy(isPhoneValid = false) }
        else _uiState.update { it.copy(isPhoneValid = true) }
        return isPhoneValid
    }

    private fun isPriceValid(price: String): Boolean {
        val isPriceValid = price.toDoubleOrNull() != null
        if (!isPriceValid) _uiState.update { it.copy(isPriceValid = false) }
        else _uiState.update { it.copy(isPriceValid = true) }
        return isPriceValid
    }

    private fun isCountryValid(country: String): Boolean {
        val isCountryValid = country.isNotEmpty()
        if (!isCountryValid) _uiState.update { it.copy(isCountryValid = false) }
        else _uiState.update { it.copy(isCountryValid = true) }
        return isCountryValid
    }

    private fun isCityValid(city: String): Boolean {
        val isCityValid = city.isNotEmpty()
        if (!isCityValid) _uiState.update { it.copy(isCityValid = false) }
        else _uiState.update { it.copy(isCityValid = true) }
        return isCityValid
    }

    private fun isMunicipalityValid(municipality: String): Boolean {
        val isMunicipalityValid = municipality.isNotEmpty()
        if (!isMunicipalityValid) _uiState.update { it.copy(isMunicipalityValid = false) }
        else _uiState.update { it.copy(isMunicipalityValid = true) }
        return isMunicipalityValid
    }

    private fun isStreetNameValid(streetName: String): Boolean {
        val isStreetNameValid = streetName.isNotEmpty()
        if (!isStreetNameValid) _uiState.update { it.copy(isStreetNameValid = false) }
        else _uiState.update { it.copy(isStreetNameValid = true) }
        return isStreetNameValid
    }

    private fun isStreetNumberValid(streetNumber: String): Boolean {
        val isStreetNumberValid = streetNumber.toIntOrNull() != null
        if (!isStreetNumberValid) _uiState.update { it.copy(isStreetNumberValid = false) }
        else _uiState.update { it.copy(isStreetNumberValid = true) }
        return isStreetNumberValid
    }

    private fun areSelectedWorkingDaysValid(selectedWorkingDays: String): Boolean {
        return selectedWorkingDays.isNotEmpty()
    }

    fun generateValidWorkingHours() = viewModelScope.launch(Dispatchers.Default) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTime = LocalTime.MIN

        val validWorkingHours = generateSequence(startTime) { time ->
            val next = time.plusMinutes(30)
            if (!next.equals(LocalTime.parse("00:00", formatter))) next else null
        }
            .map { it.format(formatter) }
            .toList()

        val validWorkingDayStartTimes = validWorkingHours.toList()
        val validWorkingDayEndTimes = validWorkingHours.toList()

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(
                allValidWorkingDayTimes = validWorkingHours,
                validWorkingDayStartTimes = validWorkingDayStartTimes,
                validWorkingDayEndTimes = validWorkingDayEndTimes,
                selectedWorkingDayStartTime = validWorkingDayStartTimes.first(),
                selectedWorkingDayEndTime = validWorkingDayEndTimes.first()
            ) }
        }
    }

    fun updateSelectedWorkingDayStartTime(selectedTime: String) = viewModelScope.launch(Dispatchers.Main) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val selectedLocalTime = LocalTime.parse(selectedTime, formatter)
        var newValidWorkingDayEndTimes: List<String>
        withContext(Dispatchers.Default) {
            newValidWorkingDayEndTimes = _uiState.value.allValidWorkingDayTimes.filter { LocalTime.parse(it, formatter).isAfter(selectedLocalTime) || it == "00:00" }
        }

        _uiState.update { it.copy(
            selectedWorkingDayStartTime = selectedTime,
            validWorkingDayEndTimes = newValidWorkingDayEndTimes,
            selectedWorkingDayEndTime = newValidWorkingDayEndTimes.first()
        ) }
    }

    fun updateSelectedWorkingDayEndTime(selectedTime: String) = viewModelScope.launch(Dispatchers.Main) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val selectedLocalTime = LocalTime.parse(selectedTime, formatter)
        var newValidWorkingDayStartTimes: List<String>
        withContext(Dispatchers.Default) {
            newValidWorkingDayStartTimes = _uiState.value.allValidWorkingDayTimes.filter { LocalTime.parse(it, formatter).isBefore(selectedLocalTime) || it == "00:00" }
        }

        _uiState.update { it.copy(
            selectedWorkingDayEndTime = selectedTime,
            validWorkingDayStartTimes = newValidWorkingDayStartTimes,
            selectedWorkingDayStartTime = newValidWorkingDayStartTimes.first()
        ) }
    }

    fun setSelectedWorkingDayStartTime(selectedWorkingDayStartTime: String) = viewModelScope.launch(Dispatchers.Main) {
        _uiState.update { it.copy(selectedWorkingDayStartTime = selectedWorkingDayStartTime) }
    }

    fun setSelectedWorkingDayEndTime(selectedWorkingDayEndTime: String) = viewModelScope.launch(Dispatchers.Main) {
        _uiState.update { it.copy(selectedWorkingDayEndTime = selectedWorkingDayEndTime) }
    }
}