package rs.ac.bg.etf.barberbooker.data.retrofit.apis

import retrofit2.http.Body
import retrofit2.http.POST
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.NotificationData

const val NOTIFICATION_URL = "http://172.20.10.4:8080/notification/"

interface NotificationApi {
    @POST("sendNotification")
    suspend fun sendNotification(@Body notificationData: NotificationData)
}