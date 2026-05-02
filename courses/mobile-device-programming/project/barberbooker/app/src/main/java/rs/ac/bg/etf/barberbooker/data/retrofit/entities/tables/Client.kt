package rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables

data class Client(
    val id: Int = 0,
    var email: String,
    var password: String,
    var name: String,
    var surname: String,
    var fcmToken: String
)