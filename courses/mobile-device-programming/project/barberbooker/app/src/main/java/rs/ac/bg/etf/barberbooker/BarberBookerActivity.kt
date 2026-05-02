package rs.ac.bg.etf.barberbooker

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import rs.ac.bg.etf.barberbooker.ui.elements.BarberBookerApp
import rs.ac.bg.etf.barberbooker.ui.elements.theme.BarberBookerTheme
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.JwtAuthenticationUtils
import rs.ac.bg.etf.barberbooker.ui.stateholders.BarberBookerViewModel

const val REQUESTS_CHANNEL_ID = "REQUESTS_NOTIFICATIONS"
const val REQUESTS_CHANNEL_NAME = "Reservation requests"
const val REQUESTS_CHANNEL_DESCRIPTION = "New reservation requests"

const val REVIEWS_CHANNEL_ID = "REVIEWS_NOTIFICATIONS"
const val REVIEWS_CHANNEL_NAME = "Reviews"
const val REVIEWS_CHANNEL_DESCRIPTION = "New reviews"

const val REJECTIONS_CHANNEL_ID = "REJECTIONS_NOTIFICATIONS"
const val REJECTIONS_CHANNEL_NAME = "Rejections"
const val REJECTIONS_CHANNEL_DESCRIPTION = "New rejections"

const val APPOINTMENTS_CHANNEL_ID = "APPOINTMENTS_NOTIFICATIONS"
const val APPOINTMENTS_CHANNEL_NAME = "Appointments requests"
const val APPOINTMENTS_CHANNEL_DESCRIPTION = "New appointments"

@AndroidEntryPoint
class BarberBookerActivity : ComponentActivity() {
    private val barberBookerViewModel: BarberBookerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationsPermission()
        createRequestsNotificationChannel()
        createReviewsNotificationChannel()
        createRejectionsNotificationChannel()
        createAppointmentsNotificationChannel()

        val notificationRoute = intent.getStringExtra("route") ?: ""

        JwtAuthenticationUtils.initializeApplicationContext(applicationContext)

        barberBookerViewModel.initializeGoogleSignInClient(
            activity = this,
            googleWebClientId = getString(R.string.google_web_client_id)
        )

        setContent {
            BarberBookerTheme {
                BarberBookerApp(
                    notificationRoute = notificationRoute,
                    barberBookerViewModel = barberBookerViewModel
                )
            }
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    private fun createRequestsNotificationChannel() {
        val notificationChannel =
            NotificationChannelCompat.Builder(REQUESTS_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(REQUESTS_CHANNEL_NAME)
                .setDescription(REQUESTS_CHANNEL_DESCRIPTION).build()
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
    }

    private fun createReviewsNotificationChannel() {
        val notificationChannel =
            NotificationChannelCompat.Builder(REVIEWS_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(REVIEWS_CHANNEL_NAME)
                .setDescription(REVIEWS_CHANNEL_DESCRIPTION).build()
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
    }

    private fun createRejectionsNotificationChannel() {
        val notificationChannel =
            NotificationChannelCompat.Builder(REJECTIONS_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(REJECTIONS_CHANNEL_NAME)
                .setDescription(REJECTIONS_CHANNEL_DESCRIPTION).build()
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
    }

    private fun createAppointmentsNotificationChannel() {
        val notificationChannel =
            NotificationChannelCompat.Builder(APPOINTMENTS_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
                .setName(APPOINTMENTS_CHANNEL_NAME)
                .setDescription(APPOINTMENTS_CHANNEL_DESCRIPTION).build()
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
    }
}