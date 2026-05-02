package rs.etf.snippet.rest.ktor.entities.tables

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id: Int = 0,
    var clientEmail: String,
    var barberEmail: String,
    var grade: Int,
    var text: String,
    var date: String
)