package rs.ac.bg.etf.barberbooker.data.retrofit.apis

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.FcmTokenUpdateData
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.MessageResponse
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Client

const val CLIENT_URL = "http://172.20.10.4:8080/client/"

interface ClientApi {
    @POST("addNewClient")
    suspend fun addNewClient(@Body client: Client)

    @GET("getClientByEmail")
    suspend fun getClientByEmail(@Query("email") email: String): Response<Client>

    @GET("updateClientProfile")
    suspend fun updateClientProfile(
        @Query("email") email: String,
        @Query("name") name: String,
        @Query("surname") surname: String
    )

    @POST("updateFcmToken")
    suspend fun updateFcmToken(@Body fcmTokenUpdateData: FcmTokenUpdateData)

    @GET("isClientConnectedToGoogle")
    suspend fun isClientConnectedToGoogle(@Query("email") email: String): Response<MessageResponse>
}