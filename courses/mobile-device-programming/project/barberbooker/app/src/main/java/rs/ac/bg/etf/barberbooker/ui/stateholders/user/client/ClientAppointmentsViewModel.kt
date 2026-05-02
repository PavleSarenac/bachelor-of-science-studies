package rs.ac.bg.etf.barberbooker.ui.stateholders.user.client

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithBarber
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ReservationRepository
import java.text.SimpleDateFormat
import javax.inject.Inject

data class ClientAppointmentsUiState(
    var appointments: List<ExtendedReservationWithBarber> = listOf()
)

@HiltViewModel
class ClientAppointmentsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientAppointmentsUiState())
    val uiState = _uiState

    @SuppressLint("SimpleDateFormat")
    fun getAppointments(clientEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        var appointments = reservationRepository.getClientAppointments(clientEmail)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        appointments = appointments.sortedBy { it.startTime }
        appointments = appointments.sortedBy { dateFormat.parse(it.date) }

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(appointments = appointments) }
        }
    }

}