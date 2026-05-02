package rs.ac.bg.etf.barberbooker.data.retrofit.apis

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithBarber
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithClient
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Reservation

const val RESERVATION_URL = "http://172.20.10.4:8080/reservation/"

interface ReservationApi {
    @POST("addNewReservation")
    suspend fun addNewReservation(@Body reservation: Reservation)

    @GET("getAllValidTimeSlots")
    suspend fun getAllValidTimeSlots(
        @Query("clientEmail") clientEmail: String,
        @Query("barberEmail") barberEmail: String,
        @Query("date") date: String
    ): Response<List<String>>

    @GET("updateReservationStatuses")
    suspend fun updateReservationStatuses(
        @Query("currentDate") currentDate: String,
        @Query("currentTime") currentTime: String
    )

    @GET("updatePendingRequests")
    suspend fun updatePendingRequests(
        @Query("currentDate") currentDate: String,
        @Query("currentTime") currentTime: String
    )

    @GET("getClientPendingReservationRequests")
    suspend fun getClientPendingReservationRequests(
        @Query("clientEmail") clientEmail: String
    ): List<ExtendedReservationWithBarber>

    @GET("getClientAppointments")
    suspend fun getClientAppointments(
        @Query("clientEmail") clientEmail: String
    ): List<ExtendedReservationWithBarber>

    @GET("getClientRejections")
    suspend fun getClientRejections(
        @Query("clientEmail") clientEmail: String
    ): List<ExtendedReservationWithBarber>

    @GET("getClientArchive")
    suspend fun getClientArchive(
        @Query("clientEmail") clientEmail: String
    ): List<ExtendedReservationWithBarber>

    @GET("getBarberPendingReservationRequests")
    suspend fun getBarberPendingReservationRequests(
        @Query("barberEmail") barberEmail: String
    ): List<ExtendedReservationWithClient>

    @GET("getBarberAppointments")
    suspend fun getBarberAppointments(
        @Query("barberEmail") barberEmail: String
    ): List<ExtendedReservationWithClient>

    @GET("getBarberArchive")
    suspend fun getBarberArchive(
        @Query("barberEmail") barberEmail: String
    ): List<ExtendedReservationWithClient>

    @GET("getBarberRejections")
    suspend fun getBarberRejections(
        @Query("barberEmail") barberEmail: String
    ): List<ExtendedReservationWithClient>

    @GET("getBarberConfirmations")
    suspend fun getBarberConfirmations(
        @Query("barberEmail") barberEmail: String
    ): List<ExtendedReservationWithClient>

    @GET("acceptReservationRequest")
    suspend fun acceptReservationRequest(
        @Query("reservationId") reservationId: Int
    )

    @GET("rejectReservationRequest")
    suspend fun rejectReservationRequest(
        @Query("reservationId") reservationId: Int
    )

    @GET("updateDoneReservationStatus")
    suspend fun updateDoneReservationStatus(
        @Query("reservationId") reservationId: Int,
        @Query("status") status: String,
    )
}