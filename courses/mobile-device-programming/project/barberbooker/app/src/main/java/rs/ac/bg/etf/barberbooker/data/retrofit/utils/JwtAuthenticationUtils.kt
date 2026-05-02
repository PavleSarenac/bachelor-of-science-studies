package rs.ac.bg.etf.barberbooker.data.retrofit.utils

import android.content.Context

object JwtAuthenticationUtils {
    private const val FILE_NAME = "jwt_token_storage"
    private const val KEY_JWT_ACCESS_TOKEN = "jwt_access_token"
    private const val KEY_JWT_REFRESH_TOKEN = "jwt_refresh_token"

    const val CLIENT_USER_TYPE = "client"
    const val BARBER_USER_TYPE = "barber"

    private lateinit var applicationContext: Context

    fun initializeApplicationContext(context: Context) {
        applicationContext = context
    }

    fun saveJwtAccessToken(jwtAccessToken: String) {
        val sharedPreferences = applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString(KEY_JWT_ACCESS_TOKEN, jwtAccessToken)
            apply()
        }
    }

    fun saveJwtRefreshToken(jwtRefreshToken: String) {
        val sharedPreferences = applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString(KEY_JWT_REFRESH_TOKEN, jwtRefreshToken)
            apply()
        }
    }

    fun getJwtAccessToken(): String {
        val sharedPreferences = applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_JWT_ACCESS_TOKEN, "") ?: ""
    }

    fun getJwtRefreshToken(): String {
        val sharedPreferences = applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_JWT_REFRESH_TOKEN, "") ?: ""
    }

    fun deleteJwtTokens() {
        val sharedPreferences = applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}
