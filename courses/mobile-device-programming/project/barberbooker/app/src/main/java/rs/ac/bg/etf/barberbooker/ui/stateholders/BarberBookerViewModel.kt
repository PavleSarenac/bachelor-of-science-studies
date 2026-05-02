package rs.ac.bg.etf.barberbooker.ui.stateholders

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReservationWithClient
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ReservationRepository
import rs.ac.bg.etf.barberbooker.data.*
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.GoogleConnectRequest
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.JwtAuthenticationData
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.BarberRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.ClientRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.GoogleRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.repositories.JwtAuthenticationRepository
import rs.ac.bg.etf.barberbooker.data.retrofit.utils.JwtAuthenticationUtils
import rs.ac.bg.etf.barberbooker.utils.events.SessionExpiredEventBus
import javax.inject.Inject

data class BarberBookerUiState(
    var startDestination: String = staticRoutes[INITIAL_SCREEN_ROUTE_INDEX],
    var loggedInUserType: String = "",
    var loggedInUserEmail: String = "",
    var confirmations: List<ExtendedReservationWithClient> = listOf(),
    var isClientConnectedToGoogle: Boolean = false,
    var isBarberConnectedToGoogle: Boolean = false
)

@HiltViewModel
class BarberBookerViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val jwtAuthenticationRepository: JwtAuthenticationRepository,
    private val googleRepository: GoogleRepository,
    private val clientRepository: ClientRepository,
    private val barberRepository: BarberRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BarberBookerUiState())
    val uiState = _uiState

    private val _logoutRequested = MutableSharedFlow<Unit>()
    val logoutRequested = _logoutRequested.asSharedFlow()

    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleWebClientId: String

    init {
        viewModelScope.launch(Dispatchers.Main) {
            SessionExpiredEventBus.sessionExpired.collect {
                _logoutRequested.emit(Unit)
            }
        }
    }

    fun initializeGoogleSignInClient(activity: Activity, googleWebClientId: String) {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
            .requestServerAuthCode(googleWebClientId, true)
            .build()
        this.googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions)
        this.googleWebClientId = googleWebClientId
    }

    fun connectWithGoogle(
        googleSignInAccount: GoogleSignInAccount,
        context: Context
    ) = viewModelScope.launch(Dispatchers.IO) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("user_email", "") ?: ""
        val userType = sharedPreferences.getString("user_type", "") ?: ""
        googleRepository.connect(GoogleConnectRequest(
            userEmail = userEmail,
            userType = userType,
            serverAuthCode = googleSignInAccount.serverAuthCode ?: ""
        ))
    }

    fun logSuccessfulGoogleSignIn(
        googleSignInAccount: GoogleSignInAccount,
        snackbarCoroutineScope: CoroutineScope,
        snackbarHostState: SnackbarHostState
    ) {
        Log.d("GoogleSignIn", "Server Auth Code: ${googleSignInAccount.serverAuthCode}")
        Log.d("GoogleSignIn", "Email: ${googleSignInAccount.email}")
        Log.d("GoogleSignIn", "Name: ${googleSignInAccount.displayName}")
        Log.d("GoogleSignIn", "ID: ${googleSignInAccount.id}")

        snackbarCoroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Hello, ${googleSignInAccount.displayName}!",
                withDismissAction = true
            )
        }
    }

    fun updateBarberGoogleConnectionStatus(email: String) = viewModelScope.launch(Dispatchers.IO) {
        val isBarberConnectedToGoogle = barberRepository.isBarberConnectedToGoogle(email)
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(isBarberConnectedToGoogle = isBarberConnectedToGoogle) }
        }
    }

    fun updateClientGoogleConnectionStatus(email: String) = viewModelScope.launch(Dispatchers.IO) {
        val isClientConnectedToGoogle = clientRepository.isClientConnectedToGoogle(email)
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(isClientConnectedToGoogle = isClientConnectedToGoogle) }
        }
    }

    fun updateLoginData(
        context: Context,
        isLoggedIn: Boolean,
        userEmail: String = "",
        userType: String = ""
    ) = viewModelScope.launch(Dispatchers.IO) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", isLoggedIn)
            putString("user_email", userEmail)
            putString("user_type", userType)
            apply()
        }

        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(loggedInUserType = userType, loggedInUserEmail = userEmail) }
        }
    }

    fun updateStartDestination(
        context: Context,
        notificationRoute: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val userEmail = sharedPreferences.getString("user_email", "")
        val userType = sharedPreferences.getString("user_type", "")

        val startDestination = when {
            isLoggedIn && userType == "client" -> {
                if (notificationRoute == "") {
                    "${staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]}/$userEmail"
                } else {
                    notificationRoute
                }
            }
            isLoggedIn && userType == "barber" -> {
                if (notificationRoute == "") {
                    "${staticRoutes[BARBER_PENDING_SCREEN_ROUTE_INDEX]}/$userEmail"
                } else {
                    notificationRoute
                }
            }
            else -> staticRoutes[INITIAL_SCREEN_ROUTE_INDEX]
        }

        withContext(Dispatchers.Main) {
            _uiState.update {
                it.copy(
                    startDestination = startDestination,
                    loggedInUserType = userType ?: "",
                    loggedInUserEmail = userEmail ?: ""
                )
            }
        }
    }

    fun getConfirmations(barberEmail: String) = viewModelScope.launch(Dispatchers.IO) {
        val confirmations = reservationRepository.getBarberConfirmations(barberEmail)
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(
                confirmations = confirmations
            ) }
        }
    }

    fun logOut(context: Context, navHostController: NavHostController) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("is_logged_in", false)
                putString("user_email", "")
                putString("user_type", "")
                apply()
            }

            jwtAuthenticationRepository.revoke(JwtAuthenticationData(
                jwtRefreshToken = JwtAuthenticationUtils.getJwtRefreshToken()
            ))
            JwtAuthenticationUtils.deleteJwtTokens()
        }

        _uiState.update {
            it.copy(
                loggedInUserType = "",
                loggedInUserEmail = ""
            )
        }

        navHostController.navigate(staticRoutes[INITIAL_SCREEN_ROUTE_INDEX])
    }
}