package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class LoginData(
    var email: String,
    var hashedPassword: String,
    var userType: String
)