package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class GoogleConnectRequest(
    var userEmail: String,
    var userType: String,
    var serverAuthCode: String
)