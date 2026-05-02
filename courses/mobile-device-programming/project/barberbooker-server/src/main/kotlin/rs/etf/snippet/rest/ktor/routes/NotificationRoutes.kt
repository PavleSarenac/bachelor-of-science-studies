package rs.etf.snippet.rest.ktor.routes

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import rs.etf.snippet.rest.ktor.entities.structures.NotificationData

fun Route.notificationRouting() {
    route("notification") {
        authenticate("jwt-authentication") {
            post("sendNotification") {
                val notificationData = call.receive<NotificationData>()
                val fcmMessage = Message.builder()
                    .setNotification(
                        Notification.builder()
                            .setTitle(notificationData.title)
                            .setBody(notificationData.body)
                            .build()
                    )
                    .putData("route", notificationData.route)
                    .putData("channelId", notificationData.channelId)
                    .setToken(notificationData.token)
                    .build()

                try {
                    FirebaseMessaging.getInstance().send(fcmMessage)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = "Notification was successfully sent."
                    )
                } catch (e: Exception) {
                    println("Error sending FCM message: ${e.message}")
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "Failed to send notification: ${e.message}"
                    )
                }
            }
        }
    }
}