package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.ReservationDao
import rs.etf.snippet.rest.ktor.entities.tables.Reservation

fun Route.reservationRouting() {
    route("reservation") {
        authenticate("jwt-authentication") {
            post("addNewReservation") {
                val reservation = call.receive<Reservation>()
                ReservationDao.addNewReservation(reservation)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "New reservation was successfully added to the database."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getAllValidTimeSlots") {
                val clientEmail = call.request.queryParameters["clientEmail"]!!
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val date = call.request.queryParameters["date"]!!
                val allValidTimeSlots = ReservationDao.getAllValidTimeSlots(clientEmail, barberEmail, date)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = allValidTimeSlots
                )
            }
        }

        authenticate("jwt-authentication") {
            get("updateReservationStatuses") {
                val currentDate = call.request.queryParameters["currentDate"]!!
                val currentTime = call.request.queryParameters["currentTime"]!!
                ReservationDao.updateReservationStatuses(currentDate, currentTime)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Reservation statuses updated."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("updatePendingRequests") {
                val currentDate = call.request.queryParameters["currentDate"]!!
                val currentTime = call.request.queryParameters["currentTime"]!!
                ReservationDao.updatePendingRequests(currentDate, currentTime)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Reservation statuses updated."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getClientPendingReservationRequests") {
                val clientEmail = call.request.queryParameters["clientEmail"]!!
                val requests = ReservationDao.getClientPendingReservationRequests(clientEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getClientAppointments") {
                val clientEmail = call.request.queryParameters["clientEmail"]!!
                val requests = ReservationDao.getClientAppointments(clientEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getClientRejections") {
                val clientEmail = call.request.queryParameters["clientEmail"]!!
                val requests = ReservationDao.getClientRejections(clientEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getClientArchive") {
                val clientEmail = call.request.queryParameters["clientEmail"]!!
                val requests = ReservationDao.getClientArchive(clientEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberPendingReservationRequests") {
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val requests = ReservationDao.getBarberPendingReservationRequests(barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberAppointments") {
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val requests = ReservationDao.getBarberAppointments(barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberArchive") {
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val requests = ReservationDao.getBarberArchive(barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberRejections") {
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val requests = ReservationDao.getBarberRejections(barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberConfirmations") {
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val requests = ReservationDao.getBarberConfirmations(barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = requests
                )
            }
        }

        authenticate("jwt-authentication") {
            get("acceptReservationRequest") {
                val reservationId = call.request.queryParameters["reservationId"]!!.toInt()
                ReservationDao.acceptReservationRequest(reservationId)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Reservation request was successfully accepted."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("rejectReservationRequest") {
                val reservationId = call.request.queryParameters["reservationId"]!!.toInt()
                ReservationDao.rejectReservationRequest(reservationId)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Reservation request was successfully rejected."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("updateDoneReservationStatus") {
                val reservationId = call.request.queryParameters["reservationId"]!!.toInt()
                val status = call.request.queryParameters["status"]!!
                ReservationDao.updateDoneReservationStatus(reservationId, status)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Done reservation status was successfully updated."
                )
            }
        }
    }
}