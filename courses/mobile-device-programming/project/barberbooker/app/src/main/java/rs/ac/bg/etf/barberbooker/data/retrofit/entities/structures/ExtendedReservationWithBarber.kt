package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class ExtendedReservationWithBarber(
    var reservationId: Int,
    var clientEmail: String,
    var barberEmail: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var status: String,

    var barberId: Int,
    var barbershopName: String,
    var barberCity: String = "",
    var barberMunicipality: String = "",
    var barberPrice: Double = 0.0
)