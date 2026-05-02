package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.BarberDao
import rs.etf.snippet.rest.ktor.entities.structures.FcmTokenUpdateData
import rs.etf.snippet.rest.ktor.entities.structures.MessageResponse
import rs.etf.snippet.rest.ktor.entities.tables.Barber

fun Route.barberRouting() {
    route("barber") {
        authenticate("jwt-authentication") {
            post("addNewBarber") {
                val newBarber = call.receive<Barber>()
                BarberDao.addNewBarber(newBarber)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "New barber was successfully added to the database."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberByEmail") {
                val barberEmail = call.request.queryParameters["email"]!!
                val barber = BarberDao.getBarberByEmail(barberEmail)
                if (barber == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = "Barber not found"
                    )
                } else {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = barber
                    )
                }
            }
        }

        authenticate("jwt-authentication") {
            get("updateBarberProfile") {
                val email = call.request.queryParameters["email"]!!
                val barbershopName = call.request.queryParameters["barbershopName"]!!
                val price = call.request.queryParameters["price"]!!.toDouble()
                val phone = call.request.queryParameters["phone"]!!
                val country = call.request.queryParameters["country"]!!
                val city = call.request.queryParameters["city"]!!
                val municipality = call.request.queryParameters["municipality"]!!
                val address = call.request.queryParameters["address"]!!
                val workingDays = call.request.queryParameters["workingDays"]!!
                val workingHours = call.request.queryParameters["workingHours"]!!

                BarberDao.updateBarberProfile(
                    email,
                    barbershopName,
                    price,
                    phone,
                    country,
                    city,
                    municipality,
                    address,
                    workingDays,
                    workingHours
                )

                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Barber profile successfully updated."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getSearchResults") {
                val query = call.request.queryParameters["query"]!!
                val searchResults = BarberDao.getSearchResults(query)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = searchResults
                )
            }
        }

        authenticate("jwt-authentication") {
            post("updateFcmToken") {
                val fcmTokenUpdateData = call.receive<FcmTokenUpdateData>()
                BarberDao.updateFcmToken(
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
            get("isBarberConnectedToGoogle") {
                val email = call.request.queryParameters["email"]!!
                val isConnected = BarberDao.isBarberConnectedToGoogle(email)
                call.respond(MessageResponse(
                    status = HttpStatusCode.OK.value,
                    message = if (isConnected) "true" else "false"
                ))
            }
        }
    }
}