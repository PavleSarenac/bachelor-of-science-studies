package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class NotificationData(
    var token: String,
    var title: String,
    var body: String,
    var route: String,
    var channelId: String
)