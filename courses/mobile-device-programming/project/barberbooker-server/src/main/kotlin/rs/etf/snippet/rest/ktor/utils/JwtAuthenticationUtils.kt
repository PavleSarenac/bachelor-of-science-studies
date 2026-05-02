package rs.etf.snippet.rest.ktor.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.util.toLocalDateTime
import io.ktor.util.InternalAPI
import rs.etf.snippet.rest.ktor.daos.JwtRefreshTokenDao
import rs.etf.snippet.rest.ktor.entities.tables.JwtRefreshToken
import java.security.MessageDigest
import java.util.Date

object JwtAuthenticationUtils {
    val jwtSecret = System.getenv("JWT_SECRET") ?: error("Missing JWT_SECRET environment variable")
    val jwtIssuer = System.getenv("JWT_ISSUER") ?: error("Missing JWT_ISSUER environment variable")
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: error("Missing JWT_AUDIENCE environment variable")
    val jwtRealm = System.getenv("JWT_REALM") ?: error("Missing JWT_REALM environment variable")

    private val signingAlgorithm: Algorithm = Algorithm.HMAC256(jwtSecret)

    val jwtTokenVerifier: JWTVerifier = JWT
        .require(signingAlgorithm)
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .build()

    const val ONE_SECOND_IN_MILLIS = 1000L
    const val ONE_MINUTE_IN_MILLIS = 60 * ONE_SECOND_IN_MILLIS
    const val ONE_HOUR_IN_MILLIS = 60 * ONE_MINUTE_IN_MILLIS
    const val ONE_DAY_IN_MILLIS = 24 * ONE_HOUR_IN_MILLIS

    const val JWT_ACCESS_TOKEN_VALIDITY_IN_MILLIS = 15 * ONE_MINUTE_IN_MILLIS
    const val JWT_REFRESH_TOKEN_VALIDITY_IN_MILLIS = 7 * ONE_DAY_IN_MILLIS

    fun generateJwtAccessToken(subject: String, userType: String): String {
        val currentTimeInMillis = System.currentTimeMillis()
        val expirationTimeInMillis = currentTimeInMillis + JWT_ACCESS_TOKEN_VALIDITY_IN_MILLIS

        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withSubject(subject)
            .withClaim("tokenType", "access")
            .withClaim("userType", userType)
            .withIssuedAt(Date(currentTimeInMillis))
            .withExpiresAt(Date(expirationTimeInMillis))
            .sign(signingAlgorithm)
    }

    @OptIn(InternalAPI::class)
    fun generateJwtRefreshToken(subject: String, userType: String): String {
        val currentTimeInMillis = System.currentTimeMillis()
        val expirationTimeInMillis = currentTimeInMillis + JWT_REFRESH_TOKEN_VALIDITY_IN_MILLIS

        val jwtRefreshToken = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withSubject(subject)
            .withClaim("tokenType", "refresh")
            .withClaim("userType", userType)
            .withIssuedAt(Date(currentTimeInMillis))
            .withExpiresAt(Date(expirationTimeInMillis))
            .sign(signingAlgorithm)

        val decodedJwtRefreshToken = jwtTokenVerifier.verify(jwtRefreshToken)
        JwtRefreshTokenDao.addNewRefreshToken(JwtRefreshToken(
            tokenHash = getJwtTokenHash(decodedJwtRefreshToken.token),
            audience = decodedJwtRefreshToken.audience[0],
            issuer = decodedJwtRefreshToken.issuer,
            subject = decodedJwtRefreshToken.subject,
            userType = decodedJwtRefreshToken.getClaim("userType").asString(),
            issuedAt = decodedJwtRefreshToken.issuedAt.toLocalDateTime(),
            expiresAt = decodedJwtRefreshToken.expiresAt.toLocalDateTime(),
            isRevoked = false
        ))

        return jwtRefreshToken
    }

    fun verifyJwtRefreshToken(jwtToken: String): DecodedJWT? {
        val decodedJwtRefreshToken = try {
            val decodedJwtToken = jwtTokenVerifier.verify(jwtToken)
            val tokenType = decodedJwtToken.getClaim("tokenType").asString()
            if (tokenType == "refresh") decodedJwtToken else null
        } catch (_: Exception) {
            null
        }
        return decodedJwtRefreshToken
    }

    fun getJwtTokenHash(jwtToken: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val hashBytes = sha256.digest(jwtToken.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}