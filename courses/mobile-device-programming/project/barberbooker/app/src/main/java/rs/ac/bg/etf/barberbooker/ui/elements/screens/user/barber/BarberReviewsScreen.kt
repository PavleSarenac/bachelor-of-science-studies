package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientViewBarberReviewsViewModel

@Composable
fun BarberReviewsScreen(
    barberEmail: String,
    clientViewBarberReviewsViewModel: ClientViewBarberReviewsViewModel = hiltViewModel()
) {

    val clientViewBarberReviewsUiState by clientViewBarberReviewsViewModel.uiState.collectAsState()
    var isDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val getReviewsJob = clientViewBarberReviewsViewModel.getBarberReviews(barberEmail)
        getReviewsJob.join()
        isDataFetched = true
    }

    if (!isDataFetched) return

    if (clientViewBarberReviewsUiState.barberReviews.isEmpty()) {
        Text(
            text = "There are no reviews.",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(count = clientViewBarberReviewsUiState.barberReviews.size) {
                val currentReview = clientViewBarberReviewsUiState.barberReviews[it]
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier.width(1000.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "${currentReview.clientName} ${currentReview.clientSurname}, " +
                                    currentReview.date,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (starRating in 1..5) {
                            Icon(
                                imageVector = when {
                                    currentReview.grade >= starRating -> Icons.Filled.StarRate
                                    else -> Icons.Filled.StarOutline
                                },
                                contentDescription = null,
                                tint = when {
                                    currentReview.grade >= starRating -> Color.Yellow
                                    else -> MaterialTheme.colorScheme.onPrimary
                                },
                                modifier = Modifier
                                    .size(30.dp)
                            )
                        }
                    }
                    if (currentReview.text != "") {
                        Row {
                            OutlinedTextField(
                                value = currentReview.text,
                                onValueChange = {},
                                modifier = Modifier
                                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                                    .fillMaxWidth(),
                                singleLine = false,
                                readOnly = true,
                                shape = RoundedCornerShape(16.dp),
                                minLines = 3,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Default
                                ),
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                    focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                    focusedTextColor = MaterialTheme.colorScheme.primary,
                                    unfocusedTextColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

}