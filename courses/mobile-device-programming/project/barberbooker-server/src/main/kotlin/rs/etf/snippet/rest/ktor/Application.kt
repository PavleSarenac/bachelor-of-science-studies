package rs.etf.snippet.rest.ktor

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.etf.snippet.rest.ktor.daos.JwtRefreshTokenDao
import rs.etf.snippet.rest.ktor.plugins.*
import rs.etf.snippet.rest.ktor.utils.DatabaseUtils
import rs.etf.snippet.rest.ktor.utils.GoogleUtils
import java.util.concurrent.TimeUnit

fun main() {
    embeddedServer(
        factory = Netty,
        host = "0.0.0.0",
        port = 8080,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    DatabaseUtils.dataSource

    configureJwtAuthentication()
    configureSerialization()
    configureRouting()

    val serviceAccountStream = this::class.java.classLoader.getResourceAsStream("service_account_key.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
        .build()
    FirebaseApp.initializeApp(options)

    environment.monitor.subscribe(ApplicationStopping) {
        (DatabaseUtils.dataSource as HikariDataSource).close()
        GoogleUtils.googleHttpClient.close()
    }

    launch {
        while (true) {
            try {
                val numberOfDeletedRows = JwtRefreshTokenDao.deleteExpiredTokens()
                println("Deleted $numberOfDeletedRows expired refresh tokens from the jwtrefreshtoken table")
            } catch (e: Exception) {
                log.error("Error cleaning up expired refresh tokens", e)
            }
            delay(TimeUnit.DAYS.toMillis(1))
        }
    }
}