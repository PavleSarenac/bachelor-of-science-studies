package rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables

data class Reservation(
    val id: Int = 0,
    var clientEmail: String,
    var barberEmail: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String
)