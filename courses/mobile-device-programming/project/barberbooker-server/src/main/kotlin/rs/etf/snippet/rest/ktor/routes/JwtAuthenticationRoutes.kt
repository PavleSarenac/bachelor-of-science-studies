package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import rs.etf.snippet.rest.ktor.daos.BarberDao
import rs.etf.snippet.rest.ktor.daos.ClientDao
import rs.etf.snippet.rest.ktor.daos.JwtRefreshTokenDao
import rs.etf.snippet.rest.ktor.entities.structures.JwtAuthenticationData
import rs.etf.snippet.rest.ktor.entities.structures.LoginData
import rs.etf.snippet.rest.ktor.utils.JwtAuthenticationUtils
import rs.etf.snippet.rest.ktor.utils.JwtAuthenticationUtils.generateJwtAccessToken
import rs.etf.snippet.rest.ktor.utils.JwtAuthenticationUtils.generateJwtRefreshToken

fun Route.jwtAuthenticationRouting() {
    route("jwt") {
        post("login") {
            val loginData = call.receive<LoginData>()

            var user: Any? = null
            if (loginData.userType == "client") {
                user = ClientDao.getClientByEmailAndPassword(loginData.email, loginData.hashedPassword)
            } else if (loginData.userType == "barber") {
                user = BarberDao.getBarberByEmailAndPassword(loginData.email, loginData.hashedPassword)
            }

            if (user == null) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "Invalid login credentials"
                )
            } else {
                val jwtAuthenticationData = JwtAuthenticationData(
                    jwtAccessToken = generateJwtAccessToken(
                        subject = loginData.email,
                        userType = loginData.userType
                    ),
                    jwtRefreshToken = generateJwtRefreshToken(
                        subject = loginData.email,
                        userType = loginData.userType
                    )
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = jwtAuthenticationData
                )
            }
        }

        post("refresh") {
            val jwtAuthenticationData = call.receive<JwtAuthenticationData>()

            if (jwtAuthenticationData.jwtRefreshToken.isEmpty()) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "JWT refresh token is missing"
                )
                return@post
            }

            val decodedJwtRefreshToken = try {
                JwtAuthenticationUtils.verifyJwtRefreshToken(jwtAuthenticationData.jwtRefreshToken)
            } catch (_: Exception) {
                null
            }

            if (decodedJwtRefreshToken == null) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "JWT refresh token is invalid or expired"
                )
                return@post
            }

            val hashedRefreshToken = JwtAuthenticationUtils.getJwtTokenHash(jwtAuthenticationData.jwtRefreshToken)
            val isRefreshTokenRevoked = JwtRefreshTokenDao.isRefreshTokenRevoked(hashedRefreshToken)
            if (isRefreshTokenRevoked) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "JWT refresh token is invalid because it has already been revoked"
                )
                return@post
            }

            JwtRefreshTokenDao.revokeRefreshToken(hashedRefreshToken)

            val newJwtAuthenticationData = JwtAuthenticationData(
                jwtAccessToken = generateJwtAccessToken(
                    subject = decodedJwtRefreshToken.getClaim("sub").asString(),
                    userType = decodedJwtRefreshToken.getClaim("userType").asString()
                ),
                jwtRefreshToken = generateJwtRefreshToken(
                    subject = decodedJwtRefreshToken.getClaim("sub").asString(),
                    userType = decodedJwtRefreshToken.getClaim("userType").asString()
                )
            )
            call.respond(
                status = HttpStatusCode.OK,
                message = newJwtAuthenticationData
            )
        }

        post("revoke") {
            val jwtAuthenticationData = call.receive<JwtAuthenticationData>()

            if (jwtAuthenticationData.jwtRefreshToken.isEmpty()) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "JWT refresh token is missing"
                )
                return@post
            }

            val hashedRefreshToken = JwtAuthenticationUtils.getJwtTokenHash(jwtAuthenticationData.jwtRefreshToken)
            JwtRefreshTokenDao.revokeRefreshToken(hashedRefreshToken)

            call.respond(
                status = HttpStatusCode.OK,
                message = "Refresh token has been revoked"
            )
        }
    }
}