package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientProfileViewModel

@Composable
fun ClientViewProfileScreen(
    clientEmail: String,
    clientProfileViewModel: ClientProfileViewModel = hiltViewModel()
) {
    val uiState by clientProfileViewModel.uiState.collectAsState()
    var isClientDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val job = clientProfileViewModel.fetchClientData(clientEmail)
        job.join()
        isClientDataFetched = true
    }

    if (!isClientDataFetched) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Name and surname")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${uiState.name} ${uiState.surname}")
            }
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Email, contentDescription = "Email address")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.email)
            }
        }
    }
}