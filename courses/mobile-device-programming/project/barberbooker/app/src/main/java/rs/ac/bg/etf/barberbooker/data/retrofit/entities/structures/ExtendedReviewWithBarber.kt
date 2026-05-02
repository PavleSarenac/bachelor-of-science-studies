package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class ExtendedReviewWithBarber(
    var reviewId: Int,
    var clientEmail: String,
    var barberEmail: String,
    var grade: Int,
    var text: String,
    var date: String,

    var barberId: Int,
    var barbershopName: String
)