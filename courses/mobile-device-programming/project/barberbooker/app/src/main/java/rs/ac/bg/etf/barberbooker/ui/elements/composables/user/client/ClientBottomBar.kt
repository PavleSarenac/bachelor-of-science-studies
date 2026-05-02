package rs.ac.bg.etf.barberbooker.ui.elements.composables.user.client

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import rs.ac.bg.etf.barberbooker.data.*

@Composable
fun ClientBottomBar(clientEmail: String, navHostController: NavHostController) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = "Appointments",
                tint = MaterialTheme.colorScheme.onPrimary
            ) },
            label = { Text(
                text = "Appointments",
                color = MaterialTheme.colorScheme.onPrimary
            ) },
            selected = currentRoute?.contains("${staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]}/") ?: false,
            onClick = {
                if (currentRoute?.contains("${staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]}/") == true) {
                    return@NavigationBarItem
                }
                navHostController.navigate("${staticRoutes[CLIENT_INITIAL_SCREEN_ROUTE_INDEX]}/${clientEmail}")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        NavigationBarItem(
            icon = { Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onPrimary
            ) },
            label = { Text(
                text = "Search",
                color = MaterialTheme.colorScheme.onPrimary
            ) },
            selected = currentRoute?.contains("${staticRoutes[CLIENT_SEARCH_BARBERS_SCREEN_ROUTE_INDEX]}/") ?: false,
            onClick = {
                if (currentRoute?.contains("${staticRoutes[CLIENT_SEARCH_BARBERS_SCREEN_ROUTE_INDEX]}/") == true) {
                    return@NavigationBarItem
                }
                navHostController.navigate("${staticRoutes[CLIENT_SEARCH_BARBERS_SCREEN_ROUTE_INDEX]}/${clientEmail}")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        NavigationBarItem(
            icon = { Icon(
                imageVector = Icons.Filled.HourglassTop,
                contentDescription = "Pending",
                tint = MaterialTheme.colorScheme.onPrimary
            ) },
            label = { Text(
                text = "Pending",
                color = MaterialTheme.colorScheme.onPrimary
            ) },
            selected = currentRoute?.contains("${staticRoutes[CLIENT_PENDING_SCREEN_ROUTE_INDEX]}/") ?: false,
            onClick = {
                if (currentRoute?.contains("${staticRoutes[CLIENT_PENDING_SCREEN_ROUTE_INDEX]}/") == true) {
                    return@NavigationBarItem
                }
                navHostController.navigate("${staticRoutes[CLIENT_PENDING_SCREEN_ROUTE_INDEX]}/${clientEmail}")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}
