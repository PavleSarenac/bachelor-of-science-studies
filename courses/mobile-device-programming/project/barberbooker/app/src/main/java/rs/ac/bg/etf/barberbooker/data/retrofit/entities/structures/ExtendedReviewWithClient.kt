package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class ExtendedReviewWithClient(
    var reviewId: Int,
    var clientEmail: String,
    var barberEmail: String,
    var grade: Int,
    var text: String,
    var date: String,

    var clientId: Int,
    var clientName: String,
    var clientSurname: String
)