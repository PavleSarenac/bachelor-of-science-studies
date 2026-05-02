package rs.ac.bg.etf.barberbooker.data.retrofit.repositories

import rs.ac.bg.etf.barberbooker.data.retrofit.apis.JwtAuthenticationApi
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.JwtAuthenticationData
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.LoginData
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.JwtAuthenticationUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JwtAuthenticationRepository @Inject constructor(
    private val jwtAuthenticationApi: JwtAuthenticationApi
) {
    suspend fun login(loginData: LoginData): Boolean {
        val jwtAuthenticationDataResponse = jwtAuthenticationApi.login(loginData)
        if (jwtAuthenticationDataResponse.isSuccessful) {
            JwtAuthenticationUtils.saveJwtAccessToken(jwtAuthenticationDataResponse.body()?.jwtAccessToken ?: "")
            JwtAuthenticationUtils.saveJwtRefreshToken(jwtAuthenticationDataResponse.body()?.jwtRefreshToken ?: "")
        }
        return jwtAuthenticationDataResponse.isSuccessful
    }

    fun refresh(jwtAuthenticationData: JwtAuthenticationData): Boolean {
        val jwtAuthenticationDataResponse = jwtAuthenticationApi.refresh(jwtAuthenticationData).execute()
        if (jwtAuthenticationDataResponse.isSuccessful) {
            JwtAuthenticationUtils.saveJwtAccessToken(jwtAuthenticationDataResponse.body()?.jwtAccessToken ?: "")
            JwtAuthenticationUtils.saveJwtRefreshToken(jwtAuthenticationDataResponse.body()?.jwtRefreshToken ?: "")
        }
        return jwtAuthenticationDataResponse.isSuccessful
    }

    suspend fun revoke(jwtAuthenticationData: JwtAuthenticationData) {
        jwtAuthenticationApi.revoke(jwtAuthenticationData)
    }
}