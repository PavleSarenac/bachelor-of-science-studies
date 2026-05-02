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
import java.text.SimpleDateFormat
import javax.inject.Inject

data class BarberRejectionsUiState(
    var rejections: List<ExtendedReservationWithClient> = listOf()
)

@HiltViewModel
class BarberRejectionsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberRejectionsUiState())
    val uiState = _uiState

    @SuppressLint("SimpleDateFormat")
    fun getRejections(barberEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        var rejections = reservationRepository.getBarberRejections(barberEmail)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        rejections = rejections.sortedByDescending { it.startTime }
        rejections = rejections.sortedByDescending { dateFormat.parse(it.date) }

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(rejections = rejections) }
        }
    }

}