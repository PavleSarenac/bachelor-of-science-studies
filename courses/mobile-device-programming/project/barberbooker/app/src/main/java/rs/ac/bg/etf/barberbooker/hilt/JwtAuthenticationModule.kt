package rs.ac.bg.etf.barberbooker.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.JwtAuthenticationRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.authenticators.JwtAuthenticator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JwtAuthenticationModule {
    @Singleton
    @Provides
    fun providesJwtAuthenticator(
        jwtAuthenticationRepository: JwtAuthenticationRepository
    ): JwtAuthenticator {
        return JwtAuthenticator(jwtAuthenticationRepository)
    }
}