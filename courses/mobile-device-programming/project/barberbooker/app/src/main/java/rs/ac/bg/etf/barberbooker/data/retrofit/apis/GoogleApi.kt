package rs.ac.bg.etf.barberbooker.data.retrofit.apis

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.CreateGoogleCalendarEventRequest
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.GoogleConnectRequest
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.MessageResponse

const val GOOGLE_URL = "http://172.20.10.4:8080/google/"

interface GoogleApi {
    @POST("connect")
    suspend fun connect(@Body googleConnectRequest: GoogleConnectRequest): Response<MessageResponse>

    @POST("createGoogleCalendarEvent")
    suspend fun createGoogleCalendarEvent(@Body createGoogleCalendarEventRequest: CreateGoogleCalendarEventRequest): Response<MessageResponse>
}