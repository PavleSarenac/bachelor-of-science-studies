package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.barber

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import rs.ac.bg.etf.barberbooker.R
import rs.ac.bg.etf.barberbooker.data.daysOfTheWeek
import rs.ac.bg.etf.barberbooker.ui.elements.composables.guest.WorkingHoursDropdown
import rs.ac.bg.etf.barberbooker.ui.stateholders.BarberBookerViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.guest.registration.BarberRegistrationViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber.BarberProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BarberEditProfileScreen(
    barberEmail: String,
    snackbarHostState: SnackbarHostState,
    barberBookerViewModel: BarberBookerViewModel,
    barberProfileViewModel: BarberProfileViewModel = hiltViewModel(),
    barberRegistrationViewModel: BarberRegistrationViewModel = hiltViewModel()
) {
    val snackbarCoroutineScope = rememberCoroutineScope()
    var isBarberDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val job = barberProfileViewModel.fetchBarberData(barberEmail)
        job.join()
        isBarberDataFetched = true
    }

    if (!isBarberDataFetched) return

    val uiState by barberProfileViewModel.uiState.collectAsState()
    val barberRegistrationUiState by barberRegistrationViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    var isDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val generateWorkingHoursJob = barberRegistrationViewModel.generateValidWorkingHours()
        generateWorkingHoursJob.join()
        val setWorkingDaySelectedStartTimeJob = barberRegistrationViewModel.setSelectedWorkingDayStartTime(uiState.workingHours.split(" - ")[0])
        setWorkingDaySelectedStartTimeJob.join()
        val setWorkingDaySelectedEndTimeJob = barberRegistrationViewModel.setSelectedWorkingDayEndTime(uiState.workingHours.split(" - ")[1])
        setWorkingDaySelectedEndTimeJob.join()
        isDataFetched = true
    }

    if (!isDataFetched) return

    val activityContext = LocalContext.current as? Activity
    val regularContext = LocalContext.current as? Context

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val googleSignInAccount = task.getResult(ApiException::class.java)
            barberBookerViewModel.logSuccessfulGoogleSignIn(
                googleSignInAccount,
                snackbarCoroutineScope,
                snackbarHostState
            )
            barberBookerViewModel.connectWithGoogle(googleSignInAccount, regularContext!!)
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign-in failed with status code ${e.statusCode}")
        } catch (e: Exception) {
            Log.e("BarberEditProfileScreen", e.message ?: "ERROR")
        }
    }

    val (mondayCheckedState, onMondayStateChange) = remember { mutableStateOf(
        uiState.workingDays.contains(daysOfTheWeek[0])
    ) }
    val (tuesdayCheckedState, onTuesdayStateChange) = remember { mutableStateOf(
        uiState.workingDays.contains(daysOfTheWeek[1])
    ) }
    val (wednesdayCheckedState, onWednesdayStateChange) = remember { mutableStateOf(
        uiState.workingDays.contains(daysOfTheWeek[2])
    ) }
    val (thursdayCheckedState, onThursdayStateChange) = remember { mutableStateOf(
        uiState.workingDays.contains(daysOfTheWeek[3])
    ) }
    val (fridayCheckedState, onFridayStateChange) = remember { mutableStateOf(
        uiState.workingDays.contains(daysOfTheWeek[4])
    ) }
    val (saturdayCheckedState, onSaturdayStateChange) = remember { mutableStateOf(
        uiState.workingDays.contains(daysOfTheWeek[5])
    ) }
    val (sundayCheckedState, onSundayStateChange) = remember { mutableStateOf(
        uiState.workingDays.contains(daysOfTheWeek[6])
    ) }

    val workingDays = listOf(
        "Mon" to mondayCheckedState,
        "Tue" to tuesdayCheckedState,
        "Wed" to wednesdayCheckedState,
        "Thu" to thursdayCheckedState,
        "Fri" to fridayCheckedState,
        "Sat" to saturdayCheckedState,
        "Sun" to sundayCheckedState
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(vertical = 24.dp, horizontal = 0.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.barbershopName,
                onValueChange =  { barberProfileViewModel.setBarbershopName(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    errorBorderColor = Color.Red,
                    errorLabelColor = MaterialTheme.colorScheme.onPrimary,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                    errorTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(
                    text = "Barbershop name",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isBarbershopNameValid,
                placeholder = { Text(
                    text = "e.g., Cut&Go",
                    color = MaterialTheme.colorScheme.onPrimary
                ) }
            )

            Text(
                text = "Working days:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            FlowRow(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                workingDays.forEachIndexed { index, (label, checked) ->
                    FilterChip(
                        selected = checked,
                        onClick = {
                            when (index) {
                                0 -> onMondayStateChange(!checked)
                                1 -> onTuesdayStateChange(!checked)
                                2 -> onWednesdayStateChange(!checked)
                                3 -> onThursdayStateChange(!checked)
                                4 -> onFridayStateChange(!checked)
                                5 -> onSaturdayStateChange(!checked)
                                6 -> onSundayStateChange(!checked)
                            }
                        },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary,
                            containerColor = Color.Transparent,
                            labelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    )
                }
            }

            Text(
                text = "Working day start time:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Box(modifier = Modifier.padding(horizontal = 48.dp))
            {
                WorkingHoursDropdown(
                    barberRegistrationViewModel,
                    true,
                    onTimeSelected = {
                        barberRegistrationViewModel.setSelectedWorkingDayStartTime(it)
                    }
                )
            }

            Text(
                text = "Working day end time:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Box(modifier = Modifier.padding(horizontal = 48.dp)) {
                WorkingHoursDropdown(
                    barberRegistrationViewModel,
                    false,
                    onTimeSelected = {
                        barberRegistrationViewModel.setSelectedWorkingDayEndTime(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.price,
                onValueChange =  { barberProfileViewModel.setPrice(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    errorBorderColor = Color.Red,
                    errorLabelColor = MaterialTheme.colorScheme.onPrimary,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                    errorTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(
                    text = "Price",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isPriceValid,
                placeholder = { Text(
                    text = "e.g., 1199.99",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.country,
                onValueChange =  { barberProfileViewModel.setCountry(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    errorBorderColor = Color.Red,
                    errorLabelColor = MaterialTheme.colorScheme.onPrimary,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                    errorTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(
                    text = "Country",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isCountryValid,
                placeholder = { Text(
                    text = "e.g., Serbia",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.city,
                onValueChange =  { barberProfileViewModel.setCity(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    errorBorderColor = Color.Red,
                    errorLabelColor = MaterialTheme.colorScheme.onPrimary,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                    errorTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(
                    text = "City",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isCityValid,
                placeholder = { Text(
                    text = "e.g., Belgrade",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.municipality,
                onValueChange =  { barberProfileViewModel.setMunicipality(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    errorBorderColor = Color.Red,
                    errorLabelColor = MaterialTheme.colorScheme.onPrimary,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                    errorTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(
                    text = "Municipality",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isMunicipalityValid,
                placeholder = { Text(
                    text = "e.g., Karaburma",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.address,
                onValueChange =  { barberProfileViewModel.setAddress(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    errorBorderColor = Color.Red,
                    errorLabelColor = MaterialTheme.colorScheme.onPrimary,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                    errorTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(
                    text = "Street name",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isAddressValid,
                placeholder = { Text(
                    text = "e.g., Marijane Gregoran 68",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.phone,
                onValueChange =  { barberProfileViewModel.setPhone(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    errorBorderColor = Color.Red,
                    errorLabelColor = MaterialTheme.colorScheme.onPrimary,
                    errorPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                    errorTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(
                    text = "Phone",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isPhoneValid,
                placeholder = { Text(
                    text = "e.g., 063/222-3333",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedButton(
                onClick = {
                    activityContext?.let {
                        val googleSignInIntent = barberBookerViewModel.googleSignInClient.signInIntent
                        googleSignInLauncher.launch(googleSignInIntent)
                    }
                },
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Connected to Google",
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Connect with Google",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    barberProfileViewModel.updateProfile(
                        snackbarHostState,
                        barberRegistrationUiState.selectedWorkingDayStartTime,
                        barberRegistrationUiState.selectedWorkingDayEndTime,
                        mondayCheckedState,
                        tuesdayCheckedState,
                        wednesdayCheckedState,
                        thursdayCheckedState,
                        fridayCheckedState,
                        saturdayCheckedState,
                        sundayCheckedState,
                        snackbarCoroutineScope
                    )
                },
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = "Submit",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}