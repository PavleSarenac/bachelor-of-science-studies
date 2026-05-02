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
import rs.ac.bg.etf.barberbooker.REVIEWS_CHANNEL_ID
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.NotificationData
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Review
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.NotificationRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ReviewRepository
import rs.ac.bg.etf.barberbooker.data.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ClientReviewsUiState(
    var newReviewGrade: Int = 0,
    var newReviewText: String = "",

    var pastReviewsForThisBarber: List<Review> = listOf()
)

@HiltViewModel
class ClientReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientReviewsUiState())
    val uiState = _uiState

    fun setNewReviewGrade(newReviewGrade: Int) {
        _uiState.update { it.copy(newReviewGrade = newReviewGrade) }
    }

    fun setNewReviewText(newReviewText: String) {
        _uiState.update { it.copy(newReviewText = newReviewText) }
    }

    fun getPastReviewsForThisBarber(
        clientEmail: String,
        barberEmail: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val pastReviews = reviewRepository.getClientReviewsForBarber(clientEmail, barberEmail)
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(pastReviewsForThisBarber = pastReviews) }
        }
    }

    fun submitReview(
        snackbarHostState: SnackbarHostState,
        snackbarCoroutineScope: CoroutineScope,
        clientEmail: String,
        barberEmail: String,
        fcmToken: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val grade = _uiState.value.newReviewGrade
        val text = _uiState.value.newReviewText
        val date = convertDateMillisToString(System.currentTimeMillis())

        if (grade == 0) {
            snackbarCoroutineScope.launch(Dispatchers.Main) {
                snackbarHostState.showSnackbar(
                    message = "Invalid review grade!",
                    withDismissAction = true
                )
            }
            return@launch
        }

        reviewRepository.submitReview(
            clientEmail,
            barberEmail,
            grade,
            text,
            date
        )

        val job = getPastReviewsForThisBarber(clientEmail, barberEmail)
        job.join()

        notificationRepository.sendNotification(
            NotificationData(
                token = fcmToken,
                title = "New notification",
                body = "You have a new review",
                route = "${staticRoutes[BARBER_REVIEWS_SCREEN_ROUTE_INDEX]}/${barberEmail}",
                channelId = REVIEWS_CHANNEL_ID
            )
        )

        snackbarCoroutineScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(
                message = "Review submitted!",
                withDismissAction = true
            )
        }

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(newReviewGrade = 0, newReviewText = "") }
        }
    }

    private fun convertDateMillisToString(dateTimeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date(dateTimeInMillis))
    }

}