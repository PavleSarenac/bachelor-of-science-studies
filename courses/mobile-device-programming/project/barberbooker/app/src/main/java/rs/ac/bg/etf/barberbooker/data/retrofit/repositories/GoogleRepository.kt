package rs.ac.bg.etf.barberbooker.data.retrofit.repositories

import android.util.Log
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.GOOGLE_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.GoogleApi
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.CreateGoogleCalendarEventRequest
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.GoogleConnectRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleRepository @Inject constructor(
    private val googleApi: GoogleApi
) {
    private val logTag = "GoogleRepository"

    suspend fun connect(googleConnectRequest: GoogleConnectRequest) {
        val messageResponse = googleApi.connect(googleConnectRequest).body()
        val logMessage = "${GOOGLE_URL}connect response status: ${messageResponse?.status}; message: ${messageResponse?.message}"
        if (messageResponse?.status in 200..299) {
            Log.d(logTag, logMessage)
        } else {
            Log.e(logTag, logMessage)
        }
    }

    suspend fun createGoogleCalendarEvent(createGoogleCalendarEventRequest: CreateGoogleCalendarEventRequest) {
        val messageResponse = googleApi.createGoogleCalendarEvent(createGoogleCalendarEventRequest).body()
        val logMessage = "${GOOGLE_URL}createGoogleCalendarEvent response status: ${messageResponse?.status}; message: ${messageResponse?.message}"
        if (messageResponse?.status in 200..299) {
            Log.d(logTag, logMessage)
        } else {
            Log.e(logTag, logMessage)
        }
    }
}