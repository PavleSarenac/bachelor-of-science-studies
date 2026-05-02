package rs.ac.bg.etf.barberbooker.ui.elements.screens.guest.registration

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import rs.ac.bg.etf.barberbooker.ui.elements.composables.guest.WorkingHoursDropdown
import rs.ac.bg.etf.barberbooker.ui.stateholders.guest.registration.BarberRegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SignUpAsBarberScreen(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    barberRegistrationViewModel: BarberRegistrationViewModel = hiltViewModel()
) {
    val snackbarCoroutineScope = rememberCoroutineScope()

    val uiState by barberRegistrationViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    val (mondayCheckedState, onMondayStateChange) = remember { mutableStateOf(true) }
    val (tuesdayCheckedState, onTuesdayStateChange) = remember { mutableStateOf(true) }
    val (wednesdayCheckedState, onWednesdayStateChange) = remember { mutableStateOf(true) }
    val (thursdayCheckedState, onThursdayStateChange) = remember { mutableStateOf(true) }
    val (fridayCheckedState, onFridayStateChange) = remember { mutableStateOf(true) }
    val (saturdayCheckedState, onSaturdayStateChange) = remember { mutableStateOf(false) }
    val (sundayCheckedState, onSundayStateChange) = remember { mutableStateOf(false) }

    val workingDays = listOf(
        "Mon" to mondayCheckedState,
        "Tue" to tuesdayCheckedState,
        "Wed" to wednesdayCheckedState,
        "Thu" to thursdayCheckedState,
        "Fri" to fridayCheckedState,
        "Sat" to saturdayCheckedState,
        "Sun" to sundayCheckedState
    )

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var isDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val generateWorkingHoursJob = barberRegistrationViewModel.generateValidWorkingHours()
        generateWorkingHoursJob.join()
        isDataFetched = true
    }

    if (!isDataFetched) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(vertical = 24.dp, horizontal = 0.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BarberBooker",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.email,
                onValueChange =  { barberRegistrationViewModel.setEmail(it) },
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
                    text = "Email",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isEmailValid || uiState.isEmailAlreadyTaken,
                placeholder = { Text(
                    text = "e.g., milos@gmail.com",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange =  { barberRegistrationViewModel.setPassword(it) },
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
                    text = "Password",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val passwordVisibilityIcon = if (isPasswordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = passwordVisibilityIcon,
                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isPasswordValid,
                placeholder = { Text(
                    text = "e.g., Milos123!",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.barbershopName,
                onValueChange =  { barberRegistrationViewModel.setBarbershopName(it) },
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
                        barberRegistrationViewModel.updateSelectedWorkingDayStartTime(it)
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
                        barberRegistrationViewModel.updateSelectedWorkingDayEndTime(it)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.price,
                onValueChange =  { barberRegistrationViewModel.setPrice(it) },
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
                onValueChange =  { barberRegistrationViewModel.setCountry(it) },
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
                onValueChange =  { barberRegistrationViewModel.setCity(it) },
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
                onValueChange =  { barberRegistrationViewModel.setMunicipality(it) },
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
                value = uiState.streetName,
                onValueChange =  { barberRegistrationViewModel.setStreetName(it) },
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
                isError = !uiState.isStreetNameValid,
                placeholder = { Text(
                    text = "e.g., Marijane Gregoran",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.streetNumber,
                onValueChange =  { barberRegistrationViewModel.setStreetNumber(it) },
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
                    text = "Street number",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                isError = !uiState.isStreetNumberValid,
                placeholder = { Text(
                    text = "e.g., 66",
                    color = MaterialTheme.colorScheme.onPrimary
                ) },
            )

            OutlinedTextField(
                value = uiState.phone,
                onValueChange =  { barberRegistrationViewModel.setPhone(it) },
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
                    barberRegistrationViewModel.registerBarber(
                        snackbarHostState,
                        navHostController,
                        uiState.selectedWorkingDayStartTime,
                        uiState.selectedWorkingDayEndTime,
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
                    text = "Sign up",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "© 2025 BarberBooker",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            )
        }
    }
}
