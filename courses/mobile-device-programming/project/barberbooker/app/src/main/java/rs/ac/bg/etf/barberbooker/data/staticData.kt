package rs.ac.bg.etf.barberbooker.data

const val INITIAL_SCREEN_ROUTE_INDEX = 0
const val LOGIN_SCREEN_ROUTE_INDEX = 1
const val SIGN_UP_SCREEN_ROUTE_INDEX = 2
const val SIGN_UP_AS_CLIENT_SCREEN_ROUTE_INDEX = 3
const val SIGN_UP_AS_BARBER_SCREEN_ROUTE_INDEX = 4
const val LOG_IN_AS_CLIENT_SCREEN_ROUTE_INDEX = 5
const val LOG_IN_AS_BARBER_SCREEN_ROUTE_INDEX = 6
const val CLIENT_INITIAL_SCREEN_ROUTE_INDEX = 7
const val BARBER_INITIAL_SCREEN_ROUTE_INDEX = 8
const val BARBER_PENDING_SCREEN_ROUTE_INDEX = 9
const val BARBER_REVIEWS_SCREEN_ROUTE_INDEX = 10
const val BARBER_ARCHIVE_SCREEN_ROUTE_INDEX = 11
const val BARBER_REJECTIONS_SCREEN_ROUTE_INDEX = 12
const val BARBER_VIEW_PROFILE_SCREEN_ROUTE_INDEX = 13
const val BARBER_EDIT_PROFILE_SCREEN_ROUTE_INDEX = 14
const val CLIENT_SEARCH_BARBERS_SCREEN_ROUTE_INDEX = 15
const val CLIENT_ARCHIVE_SCREEN_ROUTE_INDEX = 16
const val CLIENT_REVIEWS_SCREEN_ROUTE_INDEX = 17
const val CLIENT_VIEW_PROFILE_SCREEN_ROUTE_INDEX = 18
const val CLIENT_EDIT_PROFILE_SCREEN_ROUTE_INDEX = 19
const val CLIENT_VIEW_BARBER_PROFILE_SCREEN_ROUTE_INDEX = 20
const val CLIENT_PENDING_SCREEN_ROUTE_INDEX = 21
const val CLIENT_REJECTIONS_SCREEN_ROUTE_INDEX = 22
const val CLIENT_VIEW_BARBER_REVIEWS_SCREEN_ROUTE_INDEX = 23
const val BARBER_CONFIRMATIONS_SCREEN_ROUTE_INDEX = 24

val staticRoutes = listOf(
    "InitialScreen",
    "LogInScreen",
    "SignUpScreen",
    "SignUpAsClientScreen",
    "SignUpAsBarberScreen",
    "LogInAsClientScreen",
    "LogInAsBarberScreen",
    "ClientInitialScreen",
    "BarberInitialScreen",
    "BarberPendingScreen",
    "BarberReviewsScreen",
    "BarberArchiveScreen",
    "BarberRejectionsScreen",
    "BarberViewProfileScreen",
    "BarberEditProfileScreen",
    "ClientSearchBarbersScreen",
    "ClientArchiveScreen",
    "ClientReviewsScreen",
    "ClientViewProfileScreen",
    "ClientEditProfileScreen",
    "ClientViewBarberProfileScreen",
    "ClientPendingScreen",
    "ClientRejectionsScreen",
    "ClientViewBarberReviewsScreen",
    "BarberConfirmationsScreen"
)

val daysOfTheWeek = listOf(
    "MON",
    "TUE",
    "WED",
    "THU",
    "FRI",
    "SAT",
    "SUN"
)

const val PENDING_RESERVATION_STATUS_INDEX = 0
const val DONE_SUCCESS_RESERVATION_STATUS_INDEX = 4
const val DONE_FAILURE_RESERVATION_STATUS_INDEX = 5

val reservationStatuses = listOf(
    "PENDING",
    "ACCEPTED",
    "REJECTED",
    "WAITING_CONFIRMATION",
    "DONE_SUCCESS",
    "DONE_FAILURE"
)