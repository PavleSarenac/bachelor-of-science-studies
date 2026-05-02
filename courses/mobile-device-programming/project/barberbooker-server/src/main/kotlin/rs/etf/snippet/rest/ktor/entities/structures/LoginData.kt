package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    var email: String = "",
    var hashedPassword: String = "",
    var userType: String = ""
)