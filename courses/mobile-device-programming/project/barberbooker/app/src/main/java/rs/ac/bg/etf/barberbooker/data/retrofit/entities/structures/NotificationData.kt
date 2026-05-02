package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class NotificationData(
    var token: String,
    var title: String,
    var body: String,
    var route: String,
    var channelId: String
)