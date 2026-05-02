package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenUpdateData(
    var email: String,
    var fcmToken: String
)