package rs.etf.snippet.rest.ktor.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import rs.etf.snippet.rest.ktor.utils.JwtAuthenticationUtils.jwtRealm
import rs.etf.snippet.rest.ktor.utils.JwtAuthenticationUtils.jwtTokenVerifier

fun Application.configureJwtAuthentication() {
    install(Authentication) {
        jwt("jwt-authentication") {
            realm = jwtRealm
            verifier(jwtTokenVerifier)
            validate { credential ->
                if (credential.payload.getClaim("tokenType").asString() == "access" &&
                    (credential.payload.getClaim("userType").asString() == "client" ||
                    credential.payload.getClaim("userType").asString() == "barber")) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "JWT token is not valid or has expired"
                )
            }
        }
    }
}