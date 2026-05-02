package rs.ac.bg.etf.barberbooker.ui.elements.composables.user.barber

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import rs.ac.bg.etf.barberbooker.data.*
import rs.ac.bg.etf.barberbooker.ui.stateholders.BarberBookerViewModel

@Composable
fun BarberModalDrawerSheet(
    drawerState: DrawerState,
    navHostController: NavHostController,
    barberBookerViewModel: BarberBookerViewModel
) {
    val uiState by barberBookerViewModel.uiState.collectAsState()
    if (uiState.loggedInUserEmail == "") return

    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerModifier = if (isPortrait) {
        Modifier.width(screenWidthDp * 0.8f)
    } else {
        Modifier.width(screenHeightDp)
    }

    ModalDrawerSheet(modifier = drawerModifier.fillMaxHeight()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "BarberBooker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )

                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    thickness = 1.dp
                )

                NavigationDrawerItem(
                    label = { Text(text = "Pending") },
                    icon = { Icon(Icons.Filled.HourglassTop, contentDescription = "Pending") },
                    selected = currentRoute?.contains("${staticRoutes[BARBER_PENDING_SCREEN_ROUTE_INDEX]}/") ?: false,
                    onClick = {
                        navHostController.navigate("${staticRoutes[BARBER_PENDING_SCREEN_ROUTE_INDEX]}/${uiState.loggedInUserEmail}")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )

                NavigationDrawerItem(
                    label = { Text(text = "Appointments") },
                    icon = { Icon(Icons.Filled.Schedule, contentDescription = "Appointments") },
                    selected = currentRoute?.contains("${staticRoutes[BARBER_INITIAL_SCREEN_ROUTE_INDEX]}/") ?: false,
                    onClick = {
                        navHostController.navigate("${staticRoutes[BARBER_INITIAL_SCREEN_ROUTE_INDEX]}/${uiState.loggedInUserEmail}")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )

                NavigationDrawerItem(
                    label = { Text(text = "Confirmations") },
                    icon = { Icon(Icons.Filled.PendingActions, contentDescription = null) },
                    selected = currentRoute?.contains("${staticRoutes[BARBER_CONFIRMATIONS_SCREEN_ROUTE_INDEX]}/") ?: false,
                    onClick = {
                        navHostController.navigate("${staticRoutes[BARBER_CONFIRMATIONS_SCREEN_ROUTE_INDEX]}/${uiState.loggedInUserEmail}")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )

                Divider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    thickness = 1.dp
                )

                NavigationDrawerItem(
                    label = { Text(text = "Rejections") },
                    icon = { Icon(Icons.Filled.ReportProblem, contentDescription = "Rejected reservation requests") },
                    selected = currentRoute?.contains("${staticRoutes[BARBER_REJECTIONS_SCREEN_ROUTE_INDEX]}/") ?: false,
                    onClick = {
                        navHostController.navigate("${staticRoutes[BARBER_REJECTIONS_SCREEN_ROUTE_INDEX]}/${uiState.loggedInUserEmail}")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )

                NavigationDrawerItem(
                    label = { Text(text = "Archive") },
                    icon = { Icon(Icons.Filled.WorkHistory, contentDescription = "Done haircuts archive") },
                    selected = currentRoute?.contains("${staticRoutes[BARBER_ARCHIVE_SCREEN_ROUTE_INDEX]}/") ?: false,
                    onClick = {
                        navHostController.navigate("${staticRoutes[BARBER_ARCHIVE_SCREEN_ROUTE_INDEX]}/${uiState.loggedInUserEmail}")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )

                NavigationDrawerItem(
                    label = { Text(text = "Reviews") },
                    icon = { Icon(Icons.Filled.Reviews, contentDescription = "Reviews that logged in barber has received") },
                    selected = currentRoute?.contains("${staticRoutes[BARBER_REVIEWS_SCREEN_ROUTE_INDEX]}/") ?: false,
                    onClick = {
                        navHostController.navigate("${staticRoutes[BARBER_REVIEWS_SCREEN_ROUTE_INDEX]}/${uiState.loggedInUserEmail}")
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}
