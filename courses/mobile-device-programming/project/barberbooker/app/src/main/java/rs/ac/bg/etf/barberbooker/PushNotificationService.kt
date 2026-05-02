package rs.ac.bg.etf.barberbooker

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.FcmTokenUpdateData
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.BarberRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ClientRepository
import javax.inject.Inject

class PushNotificationService: FirebaseMessagingService() {

    @Inject
    lateinit var barberRepository: BarberRepository

    @Inject
    lateinit var clientRepository: ClientRepository

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    /*
    * "token" needs to be sent to the server so that the token in the database for the
    * currently logged in user can be updated so that push notifications can be sent from
    * the server to that specific user even though the token has changed
    * */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch(Dispatchers.IO) {
            val sharedPreferences: SharedPreferences = getSharedPreferences("login_data", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
            val userEmail = sharedPreferences.getString("user_email", "")
            val userType = sharedPreferences.getString("user_type", "")

            if (isLoggedIn) {
                if (userType == "barber") {
                    barberRepository.updateFcmToken(
                        FcmTokenUpdateData(
                            email = userEmail!!,
                            fcmToken = token
                        )
                    )
                } else {
                    clientRepository.updateFcmToken(
                        FcmTokenUpdateData(
                            email = userEmail!!,
                            fcmToken = token
                        )
                    )
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notification = message.notification
        val notificationTitle = notification?.title ?: ""
        val notificationBody = notification?.body ?: ""
        val route = message.data["route"] ?: ""
        val channelId = message.data["channelId"]!!

        serviceScope.launch(Dispatchers.IO) {
            val sharedPreferences: SharedPreferences = getSharedPreferences("login_data", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
            if (isLoggedIn) {
                withContext(Dispatchers.Main) {
                    showNotification(notificationTitle, notificationBody, route, channelId)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        notificationTitle: String,
        notificationBody: String,
        route: String,
        channelId: String
    ) {
        val intent = Intent(this, BarberBookerActivity::class.java).apply {
            putExtra("route", route)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}