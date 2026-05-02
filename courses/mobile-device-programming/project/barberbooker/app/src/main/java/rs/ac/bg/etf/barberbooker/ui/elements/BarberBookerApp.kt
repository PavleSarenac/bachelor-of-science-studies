package rs.ac.bg.etf.barberbooker.ui.elements

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rs.ac.bg.etf.barberbooker.data.*
import rs.ac.bg.etf.barberbooker.ui.elements.composables.guest.GuestTopBar
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.barber.BarberBottomBar
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.barber.BarberModalDrawerSheet
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.barber.BarberTopBar
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.client.ClientBottomBar
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.client.ClientModalDrawerSheet
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.client.ClientTopBar
import rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.InitialScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.login.LogInAsBarberScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.login.LogInAsClientScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.login.LogInScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.registration.SignUpAsBarberScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.registration.SignUpAsClientScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.registration.SignUpScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberArchiveScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberConfirmationsScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberEditProfileScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberInitialScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberPendingScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberRejectionsScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberReviewsScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber.BarberViewProfileScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientArchiveScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientEditProfileScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientInitialScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientPendingScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientRejectionsScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientReviewsScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientSearchBarbersScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientViewBarberProfileScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientViewBarberReviewsScreen
import rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client.ClientViewProfileScreen
import rs.ac.bg.etf.barberbooker.ui.stateholders.BarberBookerUiState
import rs.ac.bg.etf.barberbooker.ui.stateholders.BarberBookerViewModel

@Composable
fun BarberBookerApp(
    notificationRoute: String,
    barberBookerViewModel: BarberBookerViewModel
) {
    val navHostController = rememberNavController()
    val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: staticRoutes[INITIAL_SCREEN_ROUTE_INDEX]

    val context = LocalContext.current
    val barberBookerActivity = context as Activity?
    val uiState by barberBookerViewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var isStartDestinationLoaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val job = barberBookerViewModel.updateStartDestination(context, notificationRoute)
        job.join()
        isStartDestinationLoaded = true
        barberBookerViewModel.logoutRequested.collect {
            barberBookerViewModel.logOut(context, navHostController)
        }
    }

    if (!isStartDestinationLoaded) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary)
                .fillMaxSize()
        ) {}
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (uiState.loggedInUserType == "client") {
                ClientModalDrawerSheet(drawerState, navHostController, barberBookerViewModel)
            }
            else if (uiState.loggedInUserType == "barber") {
                BarberModalDrawerSheet(drawerState, navHostController, barberBookerViewModel)
            }
        },
        gesturesEnabled = uiState.loggedInUserEmail != "",
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        BarberBookerScaffold(
            navHostController,
            currentRoute,
            context,
            barberBookerActivity,
            uiState,
            snackbarHostState,
            barberBookerViewModel,
            drawerState,
            coroutineScope
        )
    }
}

@Composable
fun BarberBookerScaffold(
    navHostController: NavHostController,
    currentRoute: String,
    context: Context,
    barberBookerActivity: Activity?,
    uiState: BarberBookerUiState,
    snackbarHostState: SnackbarHostState,
    barberBookerViewModel: BarberBookerViewModel,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope
) {
    Scaffold(
        topBar = {
            ScaffoldTopBar(currentRoute, navHostController, drawerState, context, uiState, barberBookerViewModel)
        },
        bottomBar = {
            ScaffoldBottomBar(navHostController, uiState)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            navController = navHostController,
            startDestination = uiState.startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = staticRoutes[INITIAL_SCREEN_ROUTE_INDEX]) {
                BackHandler {
                    barberBookerActivity?.finish()
                }
                InitialScreen(navHostController)
            }
            composable(route = staticRoutes[LOGIN_SCREEN_ROUTE_INDEX]) {
                LogInScreen(navHostController)
            }
            composable(route = staticRoutes[SIGN_UP_SCREEN_ROUTE_INDEX]) {
                SignUpScreen(navHostController)
            }
            composable(route = staticRoutes[SIGN_UP_AS_CLIENT_SCREEN_ROUTE_INDEX]) {
                SignUpAsClientScreen(navHostController, snackbarHostState)
            }
            composable(route = staticRoutes[SIGN_UP_AS_BARBER_SCREEN_ROUTE_INDEX]) {
                SignUpAsBarberScreen(navHostController, snackbarHostState)
            }
            composable(route = staticRoutes[LOG_IN_AS_CLIENT_SCREEN_ROUTE_INDEX]) {
                LogInAsClientScreen(navHostController, snackbarHostState)
            }
            composable(route = staticRoutes[LOG_IN_AS_BARBER_SCREEN_ROUTE_INDEX]) {
                LogInAsBarberScreen(navHostController, snackbarHostState)
            }
            composable(
                route = "${staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: uiState.loggedInUserEmail
                BackHandler {
                    barberBookerActivity?.finish()
                }
                val previousRoute = navHostController.previousBackStackEntry?.destination?.route
                if (previousRoute == staticRoutes[LOG_IN_AS_CLIENT_SCREEN_ROUTE_INDEX]) {
                    barberBookerViewModel.updateLoginData(context, true, clientEmail, "client")
                }
                if (uiState.loggedInUserEmail != "") {
                    ClientInitialScreen(clientEmail, navHostController)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_INITIAL_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: uiState.loggedInUserEmail
                LoggedInBarberRegularScreenBackHandler(drawerState, navHostController, barberEmail)
                if (uiState.loggedInUserEmail != "") {
                    BarberInitialScreen(barberEmail)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_PENDING_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                BackHandler {
                    if (drawerState.isOpen) {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    } else {
                        barberBookerActivity?.finish()
                    }
                }
                val previousRoute = navHostController.previousBackStackEntry?.destination?.route
                if (previousRoute == staticRoutes[LOG_IN_AS_BARBER_SCREEN_ROUTE_INDEX]) {
                    barberBookerViewModel.updateLoginData(context, true, barberEmail, "barber")
                }
                if (uiState.loggedInUserEmail != "") {
                    BarberPendingScreen(barberEmail)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_REVIEWS_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                LoggedInBarberRegularScreenBackHandler(drawerState, navHostController, barberEmail)
                if (uiState.loggedInUserEmail != "") {
                    BarberReviewsScreen(barberEmail)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_ARCHIVE_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                LoggedInBarberRegularScreenBackHandler(drawerState, navHostController, barberEmail)
                if (uiState.loggedInUserEmail != "") {
                    BarberArchiveScreen(barberEmail)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_REJECTIONS_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                LoggedInBarberRegularScreenBackHandler(drawerState, navHostController, barberEmail)
                if (uiState.loggedInUserEmail != "") {
                    BarberRejectionsScreen(barberEmail)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_VIEW_PROFILE_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                LoggedInBarberRegularScreenBackHandler(drawerState, navHostController, barberEmail)
                if (uiState.loggedInUserEmail != "") {
                    BarberViewProfileScreen(barberEmail, navHostController)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_EDIT_PROFILE_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                LoggedInBarberRegularScreenBackHandler(drawerState, navHostController, barberEmail)
                if (uiState.loggedInUserEmail != "") {
                    BarberEditProfileScreen(barberEmail, snackbarHostState, barberBookerViewModel)
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_SEARCH_BARBERS_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                LoggedInClientRegularScreenBackHandler(drawerState, navHostController, clientEmail)
                if (uiState.loggedInUserEmail != "") {
                    ClientSearchBarbersScreen(clientEmail, navHostController)
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_ARCHIVE_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                LoggedInClientRegularScreenBackHandler(drawerState, navHostController, clientEmail)
                if (uiState.loggedInUserEmail != "") {
                    ClientArchiveScreen(clientEmail, navHostController)
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_REVIEWS_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                LoggedInClientRegularScreenBackHandler(drawerState, navHostController, clientEmail)
                if (uiState.loggedInUserEmail != "") {
                    ClientReviewsScreen(clientEmail, navHostController)
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_VIEW_PROFILE_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                LoggedInClientRegularScreenBackHandler(drawerState, navHostController, clientEmail)
                if (uiState.loggedInUserEmail != "") {
                    ClientViewProfileScreen(clientEmail)
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_EDIT_PROFILE_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                LoggedInClientRegularScreenBackHandler(drawerState, navHostController, clientEmail)
                if (uiState.loggedInUserEmail != "") {
                    ClientEditProfileScreen(clientEmail, snackbarHostState, barberBookerViewModel)
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_VIEW_BARBER_PROFILE_SCREEN_ROUTE_INDEX]}/{barberEmail}/{clientEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    },
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                BackHandler {
                    navHostController.popBackStack()
                }
                if (uiState.loggedInUserEmail != "") {
                    ClientViewBarberProfileScreen(barberEmail, clientEmail, snackbarHostState, navHostController)
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_PENDING_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                LoggedInClientRegularScreenBackHandler(drawerState, navHostController, clientEmail)
                if (uiState.loggedInUserEmail != "") {
                    ClientPendingScreen(
                        clientEmail = clientEmail,
                        navHostController = navHostController
                    )
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_REJECTIONS_SCREEN_ROUTE_INDEX]}/{clientEmail}",
                arguments = listOf(
                    navArgument("clientEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val clientEmail = navBackStackEntry.arguments?.getString("clientEmail") ?: ""
                LoggedInClientRegularScreenBackHandler(drawerState, navHostController, clientEmail)
                if (uiState.loggedInUserEmail != "") {
                    ClientRejectionsScreen(
                        clientEmail = clientEmail,
                        navHostController = navHostController
                    )
                }
            }
            composable(
                route = "${staticRoutes[CLIENT_VIEW_BARBER_REVIEWS_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                BackHandler {
                    navHostController.popBackStack()
                }
                if (uiState.loggedInUserEmail != "") {
                    ClientViewBarberReviewsScreen(barberEmail = barberEmail)
                }
            }
            composable(
                route = "${staticRoutes[BARBER_CONFIRMATIONS_SCREEN_ROUTE_INDEX]}/{barberEmail}",
                arguments = listOf(
                    navArgument("barberEmail") {
                        type = NavType.StringType
                    }
                )
            ) {navBackStackEntry ->
                val barberEmail = navBackStackEntry.arguments?.getString("barberEmail") ?: ""
                LoggedInBarberRegularScreenBackHandler(drawerState, navHostController, barberEmail)
                if (uiState.loggedInUserEmail != "") {
                    BarberConfirmationsScreen(barberEmail, barberBookerViewModel)
                }
            }
        }
    }
}

@Composable
fun ScaffoldTopBar(
    currentRoute: String,
    navHostController: NavHostController,
    drawerState: DrawerState,
    context: Context,
    uiState: BarberBookerUiState,
    barberBookerViewModel: BarberBookerViewModel
) {
    when {
        currentRoute == staticRoutes[LOGIN_SCREEN_ROUTE_INDEX] -> GuestTopBar(
            topBarTitle = "Sign in to BarberBooker",
            navHostController = navHostController
        )
        currentRoute == staticRoutes[SIGN_UP_SCREEN_ROUTE_INDEX] -> GuestTopBar(
            topBarTitle = "Sign up for BarberBooker",
            navHostController = navHostController
        )
        currentRoute == staticRoutes[SIGN_UP_AS_CLIENT_SCREEN_ROUTE_INDEX] -> GuestTopBar(
            topBarTitle = "Sign up as a client",
            navHostController = navHostController
        )
        currentRoute == staticRoutes[SIGN_UP_AS_BARBER_SCREEN_ROUTE_INDEX] -> GuestTopBar(
            topBarTitle = "Sign up as a barber",
            navHostController = navHostController
        )
        currentRoute == staticRoutes[LOG_IN_AS_CLIENT_SCREEN_ROUTE_INDEX] -> GuestTopBar(
            topBarTitle = "Sign in as a client",
            navHostController = navHostController
        )
        currentRoute == staticRoutes[LOG_IN_AS_BARBER_SCREEN_ROUTE_INDEX] -> GuestTopBar(
            topBarTitle = "Sign in as a barber",
            navHostController = navHostController
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Appointments",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_INITIAL_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "Appointments",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_PENDING_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "Pending requests",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_REVIEWS_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "Reviews",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_ARCHIVE_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "Archive",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_REJECTIONS_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "Rejections",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_VIEW_PROFILE_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "My profile",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_EDIT_PROFILE_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "Edit Profile",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_SEARCH_BARBERS_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Search",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_ARCHIVE_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Archive",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_REVIEWS_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "My reviews",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_VIEW_PROFILE_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "My profile",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_EDIT_PROFILE_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Edit profile",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_VIEW_BARBER_PROFILE_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Barbershop",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_PENDING_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Pending requests",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_REJECTIONS_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Rejections",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[CLIENT_VIEW_BARBER_REVIEWS_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> ClientTopBar(
            topBarTitle = "Reviews",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            clientEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        currentRoute.split("/")[0] == staticRoutes[BARBER_CONFIRMATIONS_SCREEN_ROUTE_INDEX]
                && uiState.loggedInUserEmail != "" -> BarberTopBar(
            topBarTitle = "Confirm reservations",
            drawerState = drawerState,
            navHostController = navHostController,
            context = context,
            barberEmail = uiState.loggedInUserEmail,
            barberBookerViewModel = barberBookerViewModel
        )
        else -> {}
    }
}

@Composable
fun ScaffoldBottomBar(
    navHostController: NavHostController,
    uiState: BarberBookerUiState
) {
    if (uiState.loggedInUserEmail != "" && uiState.loggedInUserType == "barber") {
        BarberBottomBar(uiState.loggedInUserEmail, navHostController)
    }
    if (uiState.loggedInUserEmail != "" && uiState.loggedInUserType == "client") {
        ClientBottomBar(uiState.loggedInUserEmail, navHostController)
    }
}

@Composable
fun LoggedInBarberRegularScreenBackHandler(
    drawerState: DrawerState,
    navHostController: NavHostController,
    barberEmail: String
) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        if (drawerState.isOpen) {
            coroutineScope.launch {
                drawerState.close()
            }
        } else {
            navHostController.navigate("${staticRoutes[BARBER_PENDING_SCREEN_ROUTE_INDEX]}/${barberEmail}")
        }
    }
}

@Composable
fun LoggedInClientRegularScreenBackHandler(
    drawerState: DrawerState,
    navHostController: NavHostController,
    clientEmail: String
) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        if (drawerState.isOpen) {
            coroutineScope.launch {
                drawerState.close()
            }
        } else {
            navHostController.navigate("${staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]}/${clientEmail}")
        }
    }
}