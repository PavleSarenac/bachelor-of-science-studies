package rs.ac.bg.etf.barberbooker.data.retrofit.apis

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.JwtAuthenticationData
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.LoginData

const val JWT_AUTHENTICATION_URL = "http://172.20.10.4:8080/jwt/"

interface JwtAuthenticationApi {
    @POST("login")
    suspend fun login(@Body loginData: LoginData): Response<JwtAuthenticationData>

    @POST("refresh")
    fun refresh(@Body jwtAuthenticationData: JwtAuthenticationData): Call<JwtAuthenticationData>

    @POST("revoke")
    suspend fun revoke(@Body jwtAuthenticationData: JwtAuthenticationData)
}