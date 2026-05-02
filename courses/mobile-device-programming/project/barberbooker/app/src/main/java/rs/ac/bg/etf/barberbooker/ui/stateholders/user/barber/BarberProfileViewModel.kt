package rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.ac.bg.etf.barberbooker.REQUESTS_CHANNEL_ID
import rs.ac.bg.etf.barberbooker.data.daysOfTheWeek
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.NotificationData
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Reservation
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.BarberRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.NotificationRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ReservationRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ReviewRepository
import rs.ac.bg.etf.barberbooker.data.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class BarberProfileUiState(
    var email: String = "",
    var barbershopName: String = "",
    var price: String = "",
    var phone: String = "",
    var country: String = "",
    var city: String = "",
    var municipality: String = "",
    var address: String = "",
    var workingDays: String = "",
    var workingHours: String = "",
    var fcmToken: String = "",

    var averageGrade: String = "",

    var isBarbershopNameValid: Boolean = true,
    var isPhoneValid: Boolean = true,
    var isPriceValid: Boolean = true,
    var isCountryValid: Boolean = true,
    var isCityValid: Boolean = true,
    var isMunicipalityValid: Boolean = true,
    var isAddressValid: Boolean = true,

    var validTimeSlots: List<String> = listOf(),
    var areTimeSlotsLoading: Boolean = false,
    var selectedTimeSlot: String = ""
)

@HiltViewModel
class BarberProfileViewModel @Inject constructor(
    private val barberRepository: BarberRepository,
    private val reservationRepository: ReservationRepository,
    private val reviewRepository: ReviewRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberProfileUiState())
    val uiState = _uiState

    private val decimalFormat = DecimalFormat("#.00")

    val dateValidator: (Long) -> Boolean = { dateInMillis ->
        val currentDateTimeInMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
        }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val validDaysOfWeekStrings = _uiState.value.workingDays.split(", ")
        val validDaysOfWeekIntegers = mutableListOf<Int>()

        validDaysOfWeekStrings.forEach {
            validDaysOfWeekIntegers.add(
                when (it) {
                    "MON" -> Calendar.MONDAY
                    "TUE" -> Calendar.TUESDAY
                    "WED" -> Calendar.WEDNESDAY
                    "THU" -> Calendar.THURSDAY
                    "FRI" -> Calendar.FRIDAY
                    "SAT" -> Calendar.SATURDAY
                    "SUN" -> Calendar.SUNDAY
                    else -> -1
                }
            )
        }

        validDaysOfWeekIntegers.contains(dayOfWeek) && (dateInMillis + 24 * 60 * 60 * 1000) > currentDateTimeInMillis
    }

    fun getFirstValidDateInMillis(currentDateInMillis: Long): Long {
        var firstValidDateInMillis = currentDateInMillis
        while (!dateValidator(firstValidDateInMillis)) {
            firstValidDateInMillis += 24 * 60 * 60 * 1000
        }
        return firstValidDateInMillis
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

    fun setAddress(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun updateReservationStatuses() = viewModelScope.launch(Dispatchers.IO) {
        val currentDateTimeInMillis = System.currentTimeMillis()
        val currentDateString = convertDateMillisToString(currentDateTimeInMillis)
        val currentTimeString = convertTimeMillisToString(currentDateTimeInMillis)
        reservationRepository.updateReservationStatuses(currentDateString, currentTimeString)
        reservationRepository.updatePendingRequests(currentDateString, currentTimeString)
    }

    fun getAllValidTimeSlotsForSelectedDate(
        clientEmail: String,
        barberEmail: String,
        selectedDate: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(
                areTimeSlotsLoading = true
            ) }
        }
        val validTimeSlots = reservationRepository.getAllValidTimeSlots(clientEmail, barberEmail, selectedDate)
        delay(300)
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(
                validTimeSlots = validTimeSlots,
                areTimeSlotsLoading = false,
                selectedTimeSlot = validTimeSlots.firstOrNull() ?: ""
            ) }
        }
    }

    fun updateSelectedTimeSlot(selectedTimeSlot: String) = viewModelScope.launch(Dispatchers.Main) {
        _uiState.update {it.copy(
            selectedTimeSlot = selectedTimeSlot
        ) }
    }

    fun clientCreateReservationRequest(
        barberEmail: String,
        clientEmail: String,
        date: String,
        timeSlot: String,
        snackbarHostState: SnackbarHostState,
        snackbarCoroutineScope: CoroutineScope
    ) = viewModelScope.launch(Dispatchers.IO) {
        val splittedTimeSlot = timeSlot.split(" - ")
        val startTime = splittedTimeSlot[0]
        val endTime = splittedTimeSlot[1]

        reservationRepository.addNewReservation(
            Reservation(
                id = 0,
                clientEmail = clientEmail,
                barberEmail = barberEmail,
                date = date,
                startTime = startTime,
                endTime = endTime,
                status = reservationStatuses[PENDING_RESERVATION_STATUS_INDEX]
            )
        )

        val updateValidTimeSlotsJob = getAllValidTimeSlotsForSelectedDate(clientEmail, barberEmail, date)
        updateValidTimeSlotsJob.join()

        notificationRepository.sendNotification(
            NotificationData(
                token = _uiState.value.fcmToken,
                title = "New notification",
                body = "You have a new reservation request",
                route = "${staticRoutes[BARBER_PENDING_SCREEN_ROUTE_INDEX]}/${barberEmail}",
                channelId = REQUESTS_CHANNEL_ID
            )
        )

        snackbarCoroutineScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(
                message = "Reservation request submitted!",
                withDismissAction = true
            )
        }

    }

    fun convertDateMillisToString(dateTimeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date(dateTimeInMillis))
    }

    private fun convertTimeMillisToString(dateTimeInMillis: Long): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Date(dateTimeInMillis))
    }

    fun fetchBarberData(barberEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        val barber = barberRepository.getBarberByEmail(barberEmail)
        if (barber != null) {
            val averageGrade = reviewRepository.getBarberAverageGrade(barberEmail)
            val averageGradeString = if (averageGrade != 0.00f) {
                decimalFormat.format(averageGrade)
            } else {
                "0.00"
            }
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(
                    email = barber.email,
                    barbershopName = barber.barbershopName,
                    price = decimalFormat.format(barber.price),
                    phone = barber.phone,
                    country = barber.country,
                    city = barber.city,
                    municipality = barber.municipality,
                    address = barber.address,
                    workingDays = barber.workingDays,
                    workingHours = barber.workingHours,
                    averageGrade = averageGradeString,
                    fcmToken = barber.fcmToken
                ) }
            }
        }
    }

    fun updateProfile(
        snackbarHostState: SnackbarHostState,
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
        val barbershopName = _uiState.value.barbershopName
        val phone = _uiState.value.phone
        val price = _uiState.value.price
        val country = _uiState.value.country
        val city = _uiState.value.city
        val municipality = _uiState.value.municipality
        val address = _uiState.value.address
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
                barbershopName,
                phone,
                price,
                country,
                city,
                municipality,
                address
            )) {
            snackbarCoroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Invalid data format!",
                    withDismissAction = true
                )
            }
            return@launch
        }

        if (!areSelectedWorkingDaysValid(selectedWorkingDays)) {
            snackbarCoroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "You must work at least 1 day!",
                    withDismissAction = true
                )
            }
            return@launch
        }

        if (!areWorkingHoursValid(workingDayStartTime, workingDayEndTime)) {
            snackbarCoroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Invalid working times!",
                    withDismissAction = true
                )
            }
            return@launch
        }

        withContext(Dispatchers.IO) {
            barberRepository.updateBarberProfile(
                email,
                barbershopName,
                price.toDouble(),
                phone,
                country,
                city,
                municipality,
                address,
                selectedWorkingDays,
                "$workingDayStartTime - $workingDayEndTime"
            )
        }

        snackbarCoroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Profile updated!",
                withDismissAction = true
            )
        }
    }

    private fun isDataValid(
        barbershopName: String,
        phone: String,
        price: String,
        country: String,
        city: String,
        municipality: String,
        address: String
    ): Boolean {
        var isDataValid = true
        if (!isBarbershopNameValid(barbershopName)) isDataValid = false
        if (!isPhoneValid(phone)) isDataValid = false
        if (!isPriceValid(price)) isDataValid = false
        if (!isCountryValid(country)) isDataValid = false
        if (!isCityValid(city)) isDataValid = false
        if (!isMunicipalityValid(municipality)) isDataValid = false
        if (!isAddressValid(address)) isDataValid = false
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

    private fun isAddressValid(address: String): Boolean {
        val isAddressValid = address.isNotEmpty()
        if (!isAddressValid) _uiState.update { it.copy(isAddressValid = false) }
        else _uiState.update { it.copy(isAddressValid = true) }
        return isAddressValid
    }

    private fun areWorkingHoursValid(
        workingDayStartTime: String,
        workingDayEndTime: String
    ): Boolean {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val localStartTime = LocalTime.parse(workingDayStartTime, formatter)
        val localEndTime = LocalTime.parse(workingDayEndTime, formatter)
        return localEndTime.isAfter(localStartTime) || workingDayEndTime == "00:00"
    }

    private fun areSelectedWorkingDaysValid(selectedWorkingDays: String): Boolean {
        return selectedWorkingDays.isNotEmpty()
    }
}