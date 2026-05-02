package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class GoogleConnectRequest(
    var userEmail: String,
    var userType: String,
    var serverAuthCode: String
)