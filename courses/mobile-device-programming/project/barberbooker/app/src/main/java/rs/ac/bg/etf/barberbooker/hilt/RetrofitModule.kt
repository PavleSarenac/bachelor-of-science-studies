package rs.ac.bg.etf.barberbooker.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.BARBER_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.BarberApi
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.CLIENT_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.ClientApi
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.GOOGLE_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.GoogleApi
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.JWT_AUTHENTICATION_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.JwtAuthenticationApi
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.NOTIFICATION_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.NotificationApi
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.RESERVATION_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.REVIEW_URL
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.ReservationApi
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.ReviewApi
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.authenticators.JwtAuthenticator
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors.JwtAuthenticationInterceptor
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors.LoggingInterceptor
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors.SessionExpiredInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Singleton
    @Provides
    fun providesBarberApi(
        loggingInterceptor: LoggingInterceptor,
        jwtAuthenticationInterceptor: JwtAuthenticationInterceptor,
        jwtAuthenticator: JwtAuthenticator,
        sessionExpiredInterceptor: SessionExpiredInterceptor
    ): BarberApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(jwtAuthenticationInterceptor)
            authenticator(jwtAuthenticator)
            addInterceptor(sessionExpiredInterceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BARBER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(BarberApi::class.java)
    }

    @Singleton
    @Provides
    fun providesClientApi(
        loggingInterceptor: LoggingInterceptor,
        jwtAuthenticationInterceptor: JwtAuthenticationInterceptor,
        jwtAuthenticator: JwtAuthenticator,
        sessionExpiredInterceptor: SessionExpiredInterceptor
    ): ClientApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(jwtAuthenticationInterceptor)
            authenticator(jwtAuthenticator)
            addInterceptor(sessionExpiredInterceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(CLIENT_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ClientApi::class.java)
    }

    @Singleton
    @Provides
    fun providesReviewApi(
        loggingInterceptor: LoggingInterceptor,
        jwtAuthenticationInterceptor: JwtAuthenticationInterceptor,
        jwtAuthenticator: JwtAuthenticator,
        sessionExpiredInterceptor: SessionExpiredInterceptor
    ): ReviewApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(jwtAuthenticationInterceptor)
            authenticator(jwtAuthenticator)
            addInterceptor(sessionExpiredInterceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(REVIEW_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ReviewApi::class.java)
    }

    @Singleton
    @Provides
    fun providesReservationApi(
        loggingInterceptor: LoggingInterceptor,
        jwtAuthenticationInterceptor: JwtAuthenticationInterceptor,
        jwtAuthenticator: JwtAuthenticator,
        sessionExpiredInterceptor: SessionExpiredInterceptor
    ): ReservationApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(jwtAuthenticationInterceptor)
            authenticator(jwtAuthenticator)
            addInterceptor(sessionExpiredInterceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(RESERVATION_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ReservationApi::class.java)
    }

    @Singleton
    @Provides
    fun providesNotificationApi(
        loggingInterceptor: LoggingInterceptor,
        jwtAuthenticationInterceptor: JwtAuthenticationInterceptor,
        jwtAuthenticator: JwtAuthenticator,
        sessionExpiredInterceptor: SessionExpiredInterceptor
    ): NotificationApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(jwtAuthenticationInterceptor)
            authenticator(jwtAuthenticator)
            addInterceptor(sessionExpiredInterceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NOTIFICATION_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(NotificationApi::class.java)
    }

    @Singleton
    @Provides
    fun providesGoogleApi(
        loggingInterceptor: LoggingInterceptor,
        jwtAuthenticationInterceptor: JwtAuthenticationInterceptor,
        jwtAuthenticator: JwtAuthenticator,
        sessionExpiredInterceptor: SessionExpiredInterceptor
    ): GoogleApi {
        val okHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(jwtAuthenticationInterceptor)
            authenticator(jwtAuthenticator)
            addInterceptor(sessionExpiredInterceptor)
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(GOOGLE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(GoogleApi::class.java)
    }

    @Singleton
    @Provides
    fun providesJwtAuthenticationApi(): JwtAuthenticationApi {
        val okHttpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(JWT_AUTHENTICATION_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(JwtAuthenticationApi::class.java)
    }
}