package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import rs.etf.snippet.rest.ktor.daos.BarberDao
import rs.etf.snippet.rest.ktor.daos.ClientDao
import rs.etf.snippet.rest.ktor.entities.structures.CreateGoogleCalendarEventRequest
import rs.etf.snippet.rest.ktor.entities.structures.GoogleCalendarEvent
import rs.etf.snippet.rest.ktor.entities.structures.GoogleCalendarEventDateTime
import rs.etf.snippet.rest.ktor.entities.structures.GoogleConnectRequest
import rs.etf.snippet.rest.ktor.entities.structures.MessageResponse
import rs.etf.snippet.rest.ktor.utils.GoogleUtils

fun Route.googleRouting() {
    route("google") {
        authenticate("jwt-authentication") {
            post("connect") {
                val request = call.receive<GoogleConnectRequest>()
                try {
                    GoogleUtils.connectWithGoogle(request)
                    call.respond(MessageResponse(
                            status = HttpStatusCode.OK.value,
                            message = "Connected with Google successfully!"
                    ))
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    call.respond(MessageResponse(
                        status = HttpStatusCode.InternalServerError.value,
                        message = "Connection with Google failed!"
                    ))
                }
            }

            post("createGoogleCalendarEvent") {
                val request = call.receive<CreateGoogleCalendarEventRequest>()

                if (request.userType == "barber") {
                    try {
                        val barber = BarberDao.getBarberByEmail(request.userEmail) ?: return@post call.respond(MessageResponse(
                            status = HttpStatusCode.NotFound.value,
                            message = "Barber with email '${request.userEmail}' does not exist!"
                        ))

                        if (barber.googleAccessToken.isEmpty()) {
                            return@post call.respond(MessageResponse(
                                status = HttpStatusCode.BadRequest.value,
                                message = "Barber with email '${request.userEmail}' has not connected to Google!"
                            ))
                        }

                        val googleCalendarEvent = GoogleCalendarEvent(
                            summary = request.summary,
                            location = "${barber.address}, ${barber.municipality}, ${barber.city}, ${barber.country}",
                            description = request.description,
                            start = GoogleCalendarEventDateTime(
                                dateTime = request.startDateTime,
                                timeZone = request.timeZone
                            ),
                            end = GoogleCalendarEventDateTime(
                                dateTime = request.endDateTime,
                                timeZone = request.timeZone
                            )
                        )

                        GoogleUtils.createGoogleCalendarEvent(
                            request,
                            googleCalendarEvent,
                            barber.googleAccessToken,
                            barber.googleRefreshToken
                        )

                        return@post call.respond(MessageResponse(
                            status = HttpStatusCode.OK.value,
                            message = "Google Calendar event created successfully!"
                        ))
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        return@post call.respond(MessageResponse(
                            status = HttpStatusCode.InternalServerError.value,
                            message = "Creating Google Calendar event failed!"
                        ))
                    }
                } else if (request.userType == "client") {
                    try {
                        val client = ClientDao.getClientByEmail(request.userEmail) ?: return@post call.respond(MessageResponse(
                            status = HttpStatusCode.NotFound.value,
                            message = "Client with email '${request.userEmail}' does not exist!"
                        ))

                        val barber = BarberDao.getBarberByEmail(request.summary) ?: return@post call.respond(MessageResponse(
                            status = HttpStatusCode.NotFound.value,
                            message = "Barber with email '${request.summary}' does not exist!"
                        ))

                        if (client.googleAccessToken.isEmpty()) {
                            return@post call.respond(MessageResponse(
                                status = HttpStatusCode.BadRequest.value,
                                message = "Client with email '${request.userEmail}' has not connected to Google!"
                            ))
                        }

                        val googleCalendarEvent = GoogleCalendarEvent(
                            summary = "Haircut appointment at ${barber.barbershopName}",
                            location = "${barber.address}, ${barber.municipality}, ${barber.city}, ${barber.country}",
                            description = request.description,
                            start = GoogleCalendarEventDateTime(
                                dateTime = request.startDateTime,
                                timeZone = request.timeZone
                            ),
                            end = GoogleCalendarEventDateTime(
                                dateTime = request.endDateTime,
                                timeZone = request.timeZone
                            )
                        )

                        GoogleUtils.createGoogleCalendarEvent(
                            request,
                            googleCalendarEvent,
                            client.googleAccessToken,
                            client.googleRefreshToken
                        )

                        return@post call.respond(MessageResponse(
                            status = HttpStatusCode.OK.value,
                            message = "Google Calendar event created successfully!"
                        ))
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        return@post call.respond(MessageResponse(
                            status = HttpStatusCode.InternalServerError.value,
                            message = "Creating Google Calendar event failed!"
                        ))
                    }
                }
            }
        }
    }
}