package rs.etf.snippet.rest.ktor.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import rs.etf.snippet.rest.ktor.daos.ReviewDao
import rs.etf.snippet.rest.ktor.entities.tables.Review

fun Route.reviewRouting() {
    route("review") {
        authenticate("jwt-authentication") {
            post("submitReview") {
                val review = call.receive<Review>()
                ReviewDao.submitReview(review)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "New review was successfully added to the database."
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getClientReviewsForBarber") {
                val clientEmail = call.request.queryParameters["clientEmail"]!!
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val reviews = ReviewDao.getClientReviewsForBarber(clientEmail, barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = reviews
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberReviews") {
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val reviews = ReviewDao.getBarberReviews(barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = reviews
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getClientReviews") {
                val clientEmail = call.request.queryParameters["clientEmail"]!!
                val reviews = ReviewDao.getClientReviews(clientEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = reviews
                )
            }
        }

        authenticate("jwt-authentication") {
            get("getBarberAverageGrade") {
                val barberEmail = call.request.queryParameters["barberEmail"]!!
                val averageGrade = ReviewDao.getBarberAverageGrade(barberEmail)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = averageGrade
                )
            }
        }
    }
}