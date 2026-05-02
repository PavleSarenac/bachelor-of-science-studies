package rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures

data class JwtAuthenticationData(
    var jwtAccessToken: String = "",
    var jwtRefreshToken: String = ""
)