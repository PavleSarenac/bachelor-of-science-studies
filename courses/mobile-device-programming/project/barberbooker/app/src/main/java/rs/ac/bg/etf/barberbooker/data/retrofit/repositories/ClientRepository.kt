package rs.ac.bg.etf.barberbooker.data.retrofit.repositories

import android.util.Log
import retrofit2.HttpException
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.ClientApi
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.FcmTokenUpdateData
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Client
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepository @Inject constructor(
    private val clientApi: ClientApi
) {
    private val httpExceptionLogTag = "ClientRepository"

    suspend fun addNewClient(client: Client) {
        try {
            clientApi.addNewClient(client)
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun isEmailAlreadyTaken(email: String): Boolean {
        try {
            val response = clientApi.getClientByEmail(email)
            return response.isSuccessful
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
        return true
    }

    suspend fun getClientByEmail(email: String): Client? {
        try {
            val response = clientApi.getClientByEmail(email)
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

    suspend fun updateClientProfile(email: String, name: String, surname: String) {
        try {
            clientApi.updateClientProfile(email, name, surname)
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun updateFcmToken(fcmTokenUpdateData: FcmTokenUpdateData) {
        try {
            clientApi.updateFcmToken(fcmTokenUpdateData)
        } catch (exception: HttpException) {
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun isClientConnectedToGoogle(email: String): Boolean {
        val messageResponse = clientApi.isClientConnectedToGoogle(email).body()
        return messageResponse?.message == "true"
    }
}