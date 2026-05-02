package rs.etf.snippet.rest.ktor.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import rs.etf.snippet.rest.ktor.routes.*

fun Application.configureRouting() {
    routing {
        jwtAuthenticationRouting()
        barberRouting()
        clientRouting()
        reservationRouting()
        reviewRouting()
        notificationRouting()
        googleRouting()
    }
}
