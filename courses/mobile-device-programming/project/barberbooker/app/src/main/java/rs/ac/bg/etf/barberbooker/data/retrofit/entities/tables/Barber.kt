package rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables

data class Barber(
    val id: Int = 0,
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
    var fcmToken: String = "",
    var googleAccessToken: String = "",
    var googleRefreshToken: String = ""
)