package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

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