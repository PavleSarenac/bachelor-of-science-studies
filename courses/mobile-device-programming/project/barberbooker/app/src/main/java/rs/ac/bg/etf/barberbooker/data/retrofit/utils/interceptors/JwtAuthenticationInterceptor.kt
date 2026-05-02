package rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.JwtAuthenticationUtils

class JwtAuthenticationInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val jwtAccessToken = JwtAuthenticationUtils.getJwtAccessToken()
        if (jwtAccessToken.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $jwtAccessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}