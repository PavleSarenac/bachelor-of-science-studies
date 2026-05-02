package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class JwtAuthenticationData(
    var jwtAccessToken: String = "",
    var jwtRefreshToken: String = ""
)