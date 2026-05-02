package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class CreateGoogleCalendarEventRequest(
    val userEmail: String = "",
    val userType: String = "",

    val summary: String = "",
    val location: String = "",
    val description: String = "BarberBooker Appointment",
    val startDateTime: String = "",
    val endDateTime: String = "",
    val timeZone: String = "Europe/Belgrade"
)