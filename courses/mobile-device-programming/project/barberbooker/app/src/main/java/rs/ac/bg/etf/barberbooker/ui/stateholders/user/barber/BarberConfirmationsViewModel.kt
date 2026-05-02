package rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithClient
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ReservationRepository
import rs.ac.bg.etf.barberbooker.ui.stateholders.BarberBookerViewModel
import java.text.SimpleDateFormat
import javax.inject.Inject

data class BarberConfirmationsUiState(
    var confirmations: List<ExtendedReservationWithClient> = listOf()
)

@HiltViewModel
class BarberConfirmationsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberConfirmationsUiState())
    val uiState = _uiState

    @SuppressLint("SimpleDateFormat")
    fun getConfirmations(barberEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        var confirmations = reservationRepository.getBarberConfirmations(barberEmail)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        confirmations = confirmations.sortedBy { it.startTime }
        confirmations = confirmations.sortedBy { dateFormat.parse(it.date) }

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(confirmations = confirmations) }
        }
    }

    fun confirmPositiveReservation(
        barberEmail: String,
        reservationId: Int,
        status: String,
        barberBookerViewModel: BarberBookerViewModel
    ) = viewModelScope.launch(Dispatchers.IO) {
        reservationRepository.updateDoneReservationStatus(reservationId, status)
        val job = barberBookerViewModel.getConfirmations(barberEmail)
        job.join()
        getConfirmations(barberEmail)
    }

    fun confirmNegativeReservation(
        barberEmail: String,
        reservationId: Int,
        status: String,
        barberBookerViewModel: BarberBookerViewModel
    ) = viewModelScope.launch(Dispatchers.IO) {
        reservationRepository.updateDoneReservationStatus(reservationId, status)
        val job = barberBookerViewModel.getConfirmations(barberEmail)
        job.join()
        getConfirmations(barberEmail)
    }

}