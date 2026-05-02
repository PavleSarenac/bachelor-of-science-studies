package rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables

data class Review(
    val id: Int = 0,
    var clientEmail: String,
    var barberEmail: String,
    var grade: Int,
    var text: String,
    var date: String
)