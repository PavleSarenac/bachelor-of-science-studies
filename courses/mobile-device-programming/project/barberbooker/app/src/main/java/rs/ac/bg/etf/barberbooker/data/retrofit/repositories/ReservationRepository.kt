package rs.ac.bg.etf.barberbooker.data.retrofit.repositories

import android.util.Log
import retrofit2.HttpException
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.ReservationApi
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithBarber
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithClient
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Reservation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepository @Inject constructor(
    private val reservationApi: ReservationApi
) {
    private val httpExceptionLogTag = "ReservationRepository"

    suspend fun addNewReservation(reservation: Reservation) {
        try {
            reservationApi.addNewReservation(reservation)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun getAllValidTimeSlots(
        clientEmail: String,
        barberEmail: String,
        date: String
    ): List<String> {
        try {
            val response = reservationApi.getAllValidTimeSlots(clientEmail, barberEmail, date)
            return response.body() ?: listOf()
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun updateReservationStatuses(currentDate: String, currentTime: String) {
        try {
            reservationApi.updateReservationStatuses(currentDate, currentTime)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun updatePendingRequests(currentDate: String, currentTime: String) {
        try {
            reservationApi.updatePendingRequests(currentDate, currentTime)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun getClientPendingReservationRequests(clientEmail: String): List<ExtendedReservationWithBarber> {
        try {
            return reservationApi.getClientPendingReservationRequests(clientEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getClientAppointments(clientEmail: String): List<ExtendedReservationWithBarber> {
        try {
            return reservationApi.getClientAppointments(clientEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getClientRejections(clientEmail: String): List<ExtendedReservationWithBarber> {
        try {
            return reservationApi.getClientRejections(clientEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getClientArchive(clientEmail: String): List<ExtendedReservationWithBarber> {
        try {
            return reservationApi.getClientArchive(clientEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getBarberPendingReservationRequests(barberEmail: String): List<ExtendedReservationWithClient> {
        try {
            return reservationApi.getBarberPendingReservationRequests(barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getBarberAppointments(barberEmail: String): List<ExtendedReservationWithClient> {
        try {
            return reservationApi.getBarberAppointments(barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getBarberArchive(barberEmail: String): List<ExtendedReservationWithClient> {
        try {
            return reservationApi.getBarberArchive(barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getBarberRejections(barberEmail: String): List<ExtendedReservationWithClient> {
        try {
            return reservationApi.getBarberRejections(barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getBarberConfirmations(barberEmail: String): List<ExtendedReservationWithClient> {
        try {
            return reservationApi.getBarberConfirmations(barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun acceptReservationRequest(reservationId: Int) {
        try {
            reservationApi.acceptReservationRequest(reservationId)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun rejectReservationRequest(reservationId: Int) {
        try {
            reservationApi.rejectReservationRequest(reservationId)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun updateDoneReservationStatus(reservationId: Int, status: String) {
        try {
            reservationApi.updateDoneReservationStatus(reservationId, status)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
    }
}