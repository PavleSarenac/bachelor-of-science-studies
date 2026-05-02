package rs.ac.bg.etf.barberbooker.data.retrofit.repositories

import android.util.Log
import retrofit2.HttpException
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.BarberApi
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedBarberWithAverageGrade
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.FcmTokenUpdateData
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Barber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarberRepository @Inject constructor(
    private val barberApi: BarberApi
) {
    private val httpExceptionLogTag = "BarberRepository"

    suspend fun addNewBarber(barber: Barber) {
        try {
            barberApi.addNewBarber(barber)
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun isEmailAlreadyTaken(email: String): Boolean {
        try {
            val response = barberApi.getBarberByEmail(email)
            return response.isSuccessful
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
        return true
    }

    suspend fun getBarberByEmail(email: String): Barber? {
        try {
            val response = barberApi.getBarberByEmail(email)
            return if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
        return null
    }

    suspend fun updateBarberProfile(
        email: String,
        barbershopName: String,
        price: Double,
        phone: String,
        country: String,
        city: String,
        municipality: String,
        address: String,
        workingDays: String,
        workingHours: String
    ) {
        try {
            barberApi.updateBarberProfile(
                email,
                barbershopName,
                price,
                phone,
                country,
                city,
                municipality,
                address,
                workingDays,
                workingHours
            )
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun getSearchResults(query: String): List<ExtendedBarberWithAverageGrade> {
        try {
            return barberApi.getSearchResults(query)
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun updateFcmToken(fcmTokenUpdateData: FcmTokenUpdateData) {
        try {
            barberApi.updateFcmToken(fcmTokenUpdateData)
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun isBarberConnectedToGoogle(email: String): Boolean {
        val messageResponse = barberApi.isBarberConnectedToGoogle(email).body()
        return messageResponse?.message == "true"
    }
}