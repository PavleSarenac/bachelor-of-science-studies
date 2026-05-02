package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import rs.ac.bg.etf.barberbooker.data.*
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber.BarberProfileViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientAppointmentsViewModel

@Composable
fun ClientInitialScreen(
    clientEmail: String,
    navHostController: NavHostController,
    clientAppointmentsViewModel: ClientAppointmentsViewModel = hiltViewModel(),
    barberProfileViewModel: BarberProfileViewModel = hiltViewModel()
) {
    val clientAppointmentsUiState by clientAppointmentsViewModel.uiState.collectAsState()
    var isDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val updateReservationsJob = barberProfileViewModel.updateReservationStatuses()
        updateReservationsJob.join()
        val appointmentsJob = clientAppointmentsViewModel.getAppointments(clientEmail)
        appointmentsJob.join()
        isDataFetched = true
    }

    if (!isDataFetched) return

    if (clientAppointmentsUiState.appointments.isEmpty()) {
        Text(
            text = "There are no appointments.",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = clientAppointmentsUiState.appointments.size) {
                val currentRequest = clientAppointmentsUiState.appointments[it]
                ListItem(
                    headlineContent = {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.ContentCut,
                                contentDescription = "Barbershop name",
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.CenterVertically),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentRequest.barbershopName,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    supportingContent = {
                        Column {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Location",
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.CenterVertically),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentRequest.barberMunicipality}, ${currentRequest.barberCity}",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = "Date & Time",
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.CenterVertically),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${currentRequest.date}, ${currentRequest.startTime} - ${currentRequest.endTime}",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navHostController.navigate(
                                "${staticRoutes[CLIENT_VIEW_BARBER_PROFILE_SCREEN_ROUTE_INDEX]}/${currentRequest.barberEmail}/${clientEmail}"
                            )
                        }
                )
                Divider()
            }
        }
    }
}