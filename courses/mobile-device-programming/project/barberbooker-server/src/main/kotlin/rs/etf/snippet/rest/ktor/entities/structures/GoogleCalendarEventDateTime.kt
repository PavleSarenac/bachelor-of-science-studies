package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class GoogleCalendarEventDateTime(
    val dateTime: String,
    val timeZone: String
)