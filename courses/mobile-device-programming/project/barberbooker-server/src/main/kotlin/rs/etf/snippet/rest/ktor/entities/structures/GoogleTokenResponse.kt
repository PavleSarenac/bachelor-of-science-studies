package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenResponse(
    @SerialName("id_token")
    val idToken: String = "",

    @SerialName("access_token")
    val accessToken: String = "",

    @SerialName("refresh_token")
    val refreshToken: String = "",

    @SerialName("token_type")
    val tokenType: String = "",

    @SerialName("expires_in")
    val expiresInSeconds: Long = 0,

    @SerialName("scope")
    val scope: String = ""
)