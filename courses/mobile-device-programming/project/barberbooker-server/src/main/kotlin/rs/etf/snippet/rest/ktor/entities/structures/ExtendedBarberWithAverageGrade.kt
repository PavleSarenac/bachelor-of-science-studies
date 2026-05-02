package rs.etf.snippet.rest.ktor.entities.structures

import kotlinx.serialization.Serializable

@Serializable
data class ExtendedBarberWithAverageGrade(
    var id: Int,
    var email: String,
    var password: String,
    var barbershopName: String,
    var price: Double,
    var phone: String,
    var country: String,
    var city: String,
    var municipality: String,
    var address: String,
    var workingDays: String,
    var workingHours: String,
    var fcmToken: String,

    var averageGrade: Float
)