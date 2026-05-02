package rs.etf.snippet.rest.ktor.entities.tables

import kotlinx.serialization.Serializable
import rs.etf.snippet.rest.ktor.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class JwtRefreshToken(
    val id: Int = 0,
    val tokenHash: String,
    val audience: String,
    val issuer: String,
    val subject: String,
    val userType: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val issuedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val expiresAt: LocalDateTime,
    val isRevoked: Boolean
)