package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.ClientDao
import rs.etf.snippet.rest.ktor.entities.structures.FcmTokenUpdateData
import rs.etf.snippet.rest.ktor.entities.structures.MessageResponse
import rs.etf.snippet.rest.ktor.entities.tables.Client

fun Route.clientRouting() {
    route("client") {
        authenticate("jwt-authentication") {
            post("addNewClient") {
                val newClient = call.receive<Client>()
                ClientDao.addNewClient(newClient)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "New client was successfully added to the database."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getClientByEmail") {
                val clientEmail = call.request.queryParameters["email"]!!
                val client = ClientDao.getClientByEmail(clientEmail)
                if (client == null) {
                    call.respond(
                        status = HttpStatusCode.Unauthorized,
                        message = "Invalid login credentials"
                    )
                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = client
                    )
                }
            }
        }

        authenticate("jwt-authentication") {
            get("updateClientProfile") {
                val email = call.request.queryParameters["email"]!!
                val name = call.request.queryParameters["name"]!!
                val surname = call.request.queryParameters["surname"]!!
                ClientDao.updateClientProfile(email, name, surname)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Client profile successfully updated."
                )
            }
        }

        authenticate("jwt-authentication") {
            post("updateFcmToken") {
                val fcmTokenUpdateData = call.receive<FcmTokenUpdateData>()
                ClientDao.updateFcmToken(
                    email = fcmTokenUpdateData.email,
                    fcmToken = fcmTokenUpdateData.fcmToken
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Fcm token was successfully updated."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("isClientConnectedToGoogle") {
                val email = call.request.queryParameters["email"]!!
                val isConnected = ClientDao.isClientConnectedToGoogle(email)
                call.respond(MessageResponse(
                    status = HttpStatusCode.OK.value,
                    message = if (isConnected) "true" else "false"
                ))
            }
        }
    }
}