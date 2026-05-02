package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import rs.ac.bg.etf.barberbooker.data.*
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.StarRating
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.client.TimeSlotDropdown
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber.BarberProfileViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientArchiveViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientReviewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientViewBarberProfileScreen(
    barberEmail: String,
    clientEmail: String,
    snackbarHostState: SnackbarHostState,
    navHostController: NavHostController,
    barberProfileViewModel: BarberProfileViewModel = hiltViewModel(),
    clientArchiveViewModel: ClientArchiveViewModel = hiltViewModel(),
    clientReviewsViewModel: ClientReviewsViewModel = hiltViewModel()
) {
    val uiState by barberProfileViewModel.uiState.collectAsState()
    val clientArchiveUiState by clientArchiveViewModel.uiState.collectAsState()
    val clientReviewsUiState by clientReviewsViewModel.uiState.collectAsState()

    val context = LocalContext.current
    var isDataFetched by rememberSaveable { mutableStateOf(false) }

    val snackbarCoroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val updateReservationStatusesJob = barberProfileViewModel.updateReservationStatuses()
        updateReservationStatusesJob.join()
        val archiveJob = clientArchiveViewModel.getArchive(clientEmail)
        archiveJob.join()
        val fetchBarberDataJob = barberProfileViewModel.fetchBarberData(barberEmail)
        fetchBarberDataJob.join()
        val pastReviewsJob = clientReviewsViewModel.getPastReviewsForThisBarber(clientEmail, barberEmail)
        pastReviewsJob.join()
        isDataFetched = true
    }

    if (!isDataFetched) return

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = barberProfileViewModel.getFirstValidDateInMillis(System.currentTimeMillis())
    )

    LaunchedEffect(datePickerState) {
        snapshotFlow { datePickerState.selectedDateMillis }
            .collect { selectedDateMillis ->
                selectedDateMillis?.let {
                    uiState.selectedTimeSlot = ""
                    val fetchValidTimeSlotsJob = barberProfileViewModel.getAllValidTimeSlotsForSelectedDate(clientEmail, barberEmail, barberProfileViewModel.convertDateMillisToString(it))
                    fetchValidTimeSlotsJob.join()
                }
            }
    }

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
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Barbershop info",
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.ContentCut, contentDescription = "Barbershop name")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.barbershopName)
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Grade,
                    contentDescription = "Average grade"
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (uiState.averageGrade == "0.00") {
                    Text(
                        text = "No reviews",
                        modifier = Modifier.padding(top = 2.dp)
                    )
                } else {
                    Text(
                        text = uiState.averageGrade,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StarRating(
                        rating = uiState.averageGrade.toFloat()
                    )
                }
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Reviews, contentDescription = "Reviews")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reviews",
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navHostController.navigate("${staticRoutes[CLIENT_VIEW_BARBER_REVIEWS_SCREEN_ROUTE_INDEX]}/${barberEmail}")
                    }
                )
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "Working days")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.workingDays)
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.AccessTime, contentDescription = "Working hours")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.workingHours)
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.AttachMoney, contentDescription = "Price")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${uiState.price} RSD")
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Flag, contentDescription = "Country")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.country)
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.LocationCity, contentDescription = "City")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.city)
            }

            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(imageVector = Icons.Filled.Nature, contentDescription = "Municipality")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = uiState.municipality)
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        val googleMapsAddress = uiState.address
                            .replace(" ", "%20")
                            .replace(",", "%2C")
                        val googleMapsLocation = "${uiState.country}%2C%20" +
                                "${uiState.city}%2C%20" +
                                "${uiState.municipality}%2C%20" +
                                googleMapsAddress
                        val intent = Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = Uri.parse("geo:0,0?q=${googleMapsLocation}")
                        }
                        ContextCompat.startActivity(context, intent, null)
                    }
            ) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Address")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.address,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${uiState.email}")
                        }
                        ContextCompat.startActivity(context, intent, null)
                    }
            ) {
                Icon(imageVector = Icons.Filled.Email, contentDescription = "Email address")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.email,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${uiState.phone}")
                        }
                        ContextCompat.startActivity(context, intent, null)
                    }
            ) {
                Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone number")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.phone,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(imageVector = Icons.Filled.AccessTime, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Make a reservation",
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    colors = DatePickerDefaults.colors(
                        headlineContentColor = MaterialTheme.colorScheme.onPrimary,
                        weekdayContentColor = MaterialTheme.colorScheme.onPrimary,
                        todayContentColor = MaterialTheme.colorScheme.onPrimary,
                        todayDateBorderColor = MaterialTheme.colorScheme.onPrimary,
                        selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                        yearContentColor = MaterialTheme.colorScheme.onPrimary,
                        currentYearContentColor = MaterialTheme.colorScheme.onPrimary,
                        selectedYearContainerColor = MaterialTheme.colorScheme.secondary,
                        dayContentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledDayContentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    dateValidator = barberProfileViewModel.dateValidator
                )
            }

            Divider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 70.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                when {
                    uiState.areTimeSlotsLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(32.dp)
                            )
                        }
                    }
                    uiState.validTimeSlots.isNotEmpty() -> {
                        TimeSlotDropdown(
                            barberProfileViewModel,
                            onTimeSelected = {
                                barberProfileViewModel.updateSelectedTimeSlot(it)
                            }
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No time slots available.",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Divider()

            Row {
                OutlinedButton(
                    onClick = {
                        barberProfileViewModel.clientCreateReservationRequest(
                            barberEmail,
                            clientEmail,
                            barberProfileViewModel.convertDateMillisToString(datePickerState.selectedDateMillis!!),
                            uiState.selectedTimeSlot,
                            snackbarHostState,
                            snackbarCoroutineScope
                        )
                    },
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .padding(horizontal = 70.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    enabled = uiState.selectedTimeSlot.isNotEmpty()
                ) {
                    Text(
                        text = "Submit request"
                    )
                }
            }
        }

        if (clientArchiveUiState.archive.any { it.barberEmail == barberEmail && it.status == reservationStatuses[DONE_SUCCESS_RESERVATION_STATUS_INDEX] } &&
            clientReviewsUiState.pastReviewsForThisBarber.isEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(imageVector = Icons.Filled.RateReview, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Leave a review",
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    for (starRating in 1..5) {
                        Icon(
                            imageVector = when {
                                clientReviewsUiState.newReviewGrade >= starRating -> Icons.Filled.StarRate
                                else -> Icons.Filled.StarOutline
                            },
                            contentDescription = null,
                            tint = when {
                                clientReviewsUiState.newReviewGrade >= starRating -> Color.Yellow
                                else -> MaterialTheme.colorScheme.onPrimary
                            },
                            modifier = Modifier
                                .clickable {
                                    clientReviewsViewModel.setNewReviewGrade(starRating)
                                }
                                .size(40.dp)
                        )
                    }
                }

                Row {
                    OutlinedTextField(
                        value = clientReviewsUiState.newReviewText,
                        onValueChange = { clientReviewsViewModel.setNewReviewText(it) },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .fillMaxWidth(),
                        placeholder = { Text(text = "Share details of your own experience at this barbershop") },
                        singleLine = false,
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Default
                        ),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedPlaceholderColor = Color.Black,
                            focusedPlaceholderColor = Color.Black
                        )
                    )
                }

                Divider()

                Row {
                    OutlinedButton(
                        onClick = {
                            clientReviewsViewModel.submitReview(
                                snackbarHostState,
                                snackbarCoroutineScope,
                                clientEmail,
                                barberEmail,
                                uiState.fcmToken
                            )
                        },
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(horizontal = 90.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(
                            text = "Submit review"
                        )
                    }
                }
            }
        }
    }
}