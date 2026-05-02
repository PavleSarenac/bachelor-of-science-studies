package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    var status: Int,
    var message: String
)