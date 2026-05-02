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

data class ClientArchiveUiState(
    var archive: List<ExtendedReservationWithBarber> = listOf()
)

@HiltViewModel
class ClientArchiveViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientArchiveUiState())
    val uiState = _uiState

    @SuppressLint("SimpleDateFormat")
    fun getArchive(clientEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        var archive = reservationRepository.getClientArchive(clientEmail)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        archive = archive.sortedByDescending { it.startTime }
        archive = archive.sortedByDescending { dateFormat.parse(it.date) }

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(archive = archive) }
        }
    }

}