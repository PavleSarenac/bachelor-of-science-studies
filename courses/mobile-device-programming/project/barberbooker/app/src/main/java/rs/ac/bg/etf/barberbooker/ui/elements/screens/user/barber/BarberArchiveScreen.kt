package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber

import rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber.BarberArchiveViewModel
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rs.ac.bg.etf.barberbooker.data.DONE_SUCCESS_RESERVATION_STATUS_INDEX
import rs.ac.bg.etf.barberbooker.data.reservationStatuses
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber.BarberProfileViewModel

@Composable
fun BarberArchiveScreen(
    barberEmail: String,
    barberArchiveViewModel: BarberArchiveViewModel = hiltViewModel(),
    barberProfileViewModel: BarberProfileViewModel = hiltViewModel()
) {
    val barberArchiveUiState by barberArchiveViewModel.uiState.collectAsState()
    var isDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val updateReservationsJob = barberProfileViewModel.updateReservationStatuses()
        updateReservationsJob.join()
        val archiveJob = barberArchiveViewModel.getArchive(barberEmail)
        archiveJob.join()
        isDataFetched = true
    }

    if (!isDataFetched) return

    if (barberArchiveUiState.archive.isEmpty()) {
        Text(
            text = "There are no past appointments.",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(top = 10.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = barberArchiveUiState.archive.size) {
                val currentRequest = barberArchiveUiState.archive[it]
                ListItem(
                    headlineContent = {
                        Row {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Client Name",
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.CenterVertically),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentRequest.clientName} ${currentRequest.clientSurname}",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    supportingContent = {
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
                    },
                    trailingContent = {
                        if (currentRequest.status == reservationStatuses[DONE_SUCCESS_RESERVATION_STATUS_INDEX]) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.Green
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Divider()
            }
        }
    }
}