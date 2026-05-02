package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class ExtendedReservationWithClient(
    var reservationId: Int,
    var clientEmail: String,
    var barberEmail: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String,

    var clientId: Int,
    var clientName: String,
    var clientSurname: String,
    var fcmToken: String = ""
)