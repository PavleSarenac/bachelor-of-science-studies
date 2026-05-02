package rs.etf.snippet.rest.ktor.entities.tables

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: Int = 0,
    var email: String,
    var password: String,
    var name: String,
    var surname: String,
    var fcmToken: String = "",
    var googleAccessToken: String = "",
    var googleRefreshToken: String = ""
)