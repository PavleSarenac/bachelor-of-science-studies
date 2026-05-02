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
import rs.ac.bg.etf.barberbooker.APPOINTMENTS_CHANNEL_ID
import rs.ac.bg.etf.barberbooker.REJECTIONS_CHANNEL_ID
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithClient
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.NotificationData
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.NotificationRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ReservationRepository
import rs.ac.bg.etf.barberbooker.data.*
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.CreateGoogleCalendarEventRequest
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.GoogleRepository
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class BarberPendingRequestsUiState(
    var pendingReservationRequests: List<ExtendedReservationWithClient> = listOf()
)

@HiltViewModel
class BarberPendingRequestsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val notificationRepository: NotificationRepository,
    private val googleRepository: GoogleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BarberPendingRequestsUiState())
    val uiState = _uiState

    @SuppressLint("SimpleDateFormat")
    fun getPendingReservationRequests(barberEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        var pendingReservationRequests = reservationRepository.getBarberPendingReservationRequests(barberEmail)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        pendingReservationRequests = pendingReservationRequests.sortedBy { it.startTime }
        pendingReservationRequests = pendingReservationRequests.sortedBy { dateFormat.parse(it.date) }

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(pendingReservationRequests = pendingReservationRequests) }
        }
    }

    fun acceptReservationRequest(
        barberEmail: String,
        reservationRequest: ExtendedReservationWithClient
    ) = viewModelScope.launch(Dispatchers.IO) {
        reservationRepository.acceptReservationRequest(reservationRequest.reservationId)

        googleRepository.createGoogleCalendarEvent(CreateGoogleCalendarEventRequest(
            userEmail = reservationRequest.clientEmail,
            userType = "client",
            summary = reservationRequest.barberEmail,
            startDateTime = getIso8601DateTime(reservationRequest.date, reservationRequest.startTime),
            endDateTime = getIso8601DateTime(reservationRequest.date, reservationRequest.endTime),
        ))

        googleRepository.createGoogleCalendarEvent(CreateGoogleCalendarEventRequest(
            userEmail = barberEmail,
            userType = "barber",
            summary = "${reservationRequest.clientName} ${reservationRequest.clientSurname}",
            startDateTime = getIso8601DateTime(reservationRequest.date, reservationRequest.startTime),
            endDateTime = getIso8601DateTime(reservationRequest.date, reservationRequest.endTime),
        ))

        getPendingReservationRequests(barberEmail)

        notificationRepository.sendNotification(
            NotificationData(
                token = reservationRequest.fcmToken,
                title = "New notification",
                body = "Your reservation request was accepted",
                route = "${staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]}/${reservationRequest.clientEmail}",
                channelId = APPOINTMENTS_CHANNEL_ID
            )
        )
    }

    fun rejectReservationRequest(
        barberEmail: String,
        reservationId: Int,
        clientEmail: String,
        fcmToken: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        reservationRepository.rejectReservationRequest(reservationId)
        getPendingReservationRequests(barberEmail)
        notificationRepository.sendNotification(
            NotificationData(
                token = fcmToken,
                title = "New notification",
                body = "Your reservation request was rejected",
                route = "${staticRoutes[CLIENT_REJECTIONS_SCREEN_ROUTE_INDEX]}/${clientEmail}",
                channelId = REJECTIONS_CHANNEL_ID
            )
        )
    }

    private fun getIso8601DateTime(
        dateString: String,
        timeString: String,
        timeZoneString: String = "Europe/Belgrade"
    ): String {
        val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
        val zonedDateTime = ZonedDateTime.of(date, time, ZoneId.of(timeZoneString))
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}