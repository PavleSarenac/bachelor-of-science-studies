package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class GoogleCalendarEvent(
    val summary: String,
    val location: String,
    val description: String,
    val start: GoogleCalendarEventDateTime,
    val end: GoogleCalendarEventDateTime
)