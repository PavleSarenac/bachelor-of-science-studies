package rs.ac.bg.etf.barberbooker.hilt

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors.JwtAuthenticationInterceptor
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors.LoggingInterceptor
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.interceptors.SessionExpiredInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterceptorModule {
    @Singleton
    @Provides
    fun providesLoggingInterceptor(): LoggingInterceptor = LoggingInterceptor()

    @Singleton
    @Provides
    fun providesJwtAuthenticationInterceptor(): JwtAuthenticationInterceptor = JwtAuthenticationInterceptor()

    @Singleton
    @Provides
    fun providesSessionExpiredInterceptor(): SessionExpiredInterceptor = SessionExpiredInterceptor()
}