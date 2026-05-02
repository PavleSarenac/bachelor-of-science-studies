package rs.etf.snippet.rest.ktor.utils

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.apache.http.client.HttpResponseException
import rs.etf.snippet.rest.ktor.daos.BarberDao
import rs.etf.snippet.rest.ktor.daos.ClientDao
import rs.etf.snippet.rest.ktor.entities.structures.CreateGoogleCalendarEventRequest
import rs.etf.snippet.rest.ktor.entities.structures.GoogleCalendarEvent
import rs.etf.snippet.rest.ktor.entities.structures.GoogleConnectRequest
import rs.etf.snippet.rest.ktor.entities.structures.GoogleTokenResponse
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

object GoogleUtils {
    val googleWebTokenUri = System.getenv("GOOGLE_WEB_TOKEN_URI") ?: error("Missing GOOGLE_WEB_TOKEN_URI environment variable")
    val googleWebClientId = System.getenv("GOOGLE_WEB_CLIENT_ID") ?: error("Missing GOOGLE_WEB_CLIENT_ID environment variable")
    val googleWebClientSecret = System.getenv("GOOGLE_WEB_CLIENT_SECRET") ?: error("Missing GOOGLE_WEB_CLIENT_SECRET environment variable")
    val googleWebRedirectUri = System.getenv("GOOGLE_WEB_REDIRECT_URI") ?: error("Missing GOOGLE_WEB_REDIRECT_URI environment variable")

    val googleHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun connectWithGoogle(googleConnectRequest: GoogleConnectRequest) {
        val httpResponse: HttpResponse = googleHttpClient.submitForm(
            url = googleWebTokenUri,
            formParameters = Parameters.build {
                append("code", googleConnectRequest.serverAuthCode)
                append("client_id", googleWebClientId)
                append("client_secret", googleWebClientSecret)
                append("redirect_uri", googleWebRedirectUri)
                append("grant_type", "authorization_code")
            }
        )

        if (httpResponse.status.value !in 200..299) {
            throw HttpResponseException(httpResponse.status.value, "Failed to connect with Google")
        }

        val googleTokenResponse: GoogleTokenResponse = httpResponse.body()
        updateGoogleTokens(
            userType = googleConnectRequest.userType,
            userEmail = googleConnectRequest.userEmail,
            accessToken = googleTokenResponse.accessToken,
            refreshToken = googleTokenResponse.refreshToken
        )
    }

    fun updateGoogleTokens(
        userType: String,
        userEmail: String,
        accessToken: String,
        refreshToken: String
    ) {
        if (userType == "barber") {
            BarberDao.updateGoogleTokens(
                email = userEmail,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } else if (userType == "client") {
            ClientDao.updateGoogleTokens(
                email = userEmail,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }

    suspend fun createGoogleCalendarEvent(
        createGoogleCalendarEventRequest: CreateGoogleCalendarEventRequest,
        googleCalendarEvent: GoogleCalendarEvent,
        accessToken: String,
        refreshToken: String
    ) {
        var httpResponse: HttpResponse = googleHttpClient.post("https://www.googleapis.com/calendar/v3/calendars/primary/events") {
            headers {
                append("Authorization", "Bearer $accessToken")
                append("Content-Type", "application/json")
            }
            setBody(googleCalendarEvent)
        }

        var newAccessToken: String
        if (httpResponse.status.value == HTTP_UNAUTHORIZED) {
            newAccessToken = refreshGoogleAccessToken(createGoogleCalendarEventRequest, refreshToken)
            httpResponse = googleHttpClient.post("https://www.googleapis.com/calendar/v3/calendars/primary/events") {
                headers {
                    append("Authorization", "Bearer $newAccessToken")
                    append("Content-Type", "application/json")
                }
                setBody(googleCalendarEvent)
            }
            if (httpResponse.status.value == HTTP_UNAUTHORIZED) {
                if (createGoogleCalendarEventRequest.userType == "barber") {
                    BarberDao.updateGoogleTokens(
                        email = createGoogleCalendarEventRequest.userEmail,
                        accessToken = "",
                        refreshToken = ""
                    )
                } else if (createGoogleCalendarEventRequest.userType == "client") {
                    ClientDao.updateGoogleTokens(
                        email = createGoogleCalendarEventRequest.userEmail,
                        accessToken = "",
                        refreshToken = ""
                    )
                }
            }
        }

        if (httpResponse.status.value !in 200..299) {
            throw HttpResponseException(httpResponse.status.value, "Failed to create Google Calendar event")
        }
    }

    suspend fun refreshGoogleAccessToken(
        createGoogleCalendarEventRequest: CreateGoogleCalendarEventRequest,
        refreshToken: String
    ): String {
        val httpResponse: HttpResponse = googleHttpClient.submitForm(
            url = googleWebTokenUri,
            formParameters = Parameters.build {
                append("client_id", googleWebClientId)
                append("client_secret", googleWebClientSecret)
                append("refresh_token", refreshToken)
                append("grant_type", "refresh_token")
            }
        )

        if (httpResponse.status.value !in 200..299) {
            throw HttpResponseException(httpResponse.status.value, "Failed to refresh Google access token")
        }

        val googleTokenResponse: GoogleTokenResponse = httpResponse.body()
        updateGoogleTokens(
            userType = createGoogleCalendarEventRequest.userType,
            userEmail = createGoogleCalendarEventRequest.userEmail,
            accessToken = googleTokenResponse.accessToken,
            refreshToken = refreshToken
        )
        
        return googleTokenResponse.accessToken
    }
}