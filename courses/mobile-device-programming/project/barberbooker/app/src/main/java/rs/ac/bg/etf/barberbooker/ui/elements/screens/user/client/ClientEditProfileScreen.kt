package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import rs.ac.bg.etf.barberbooker.R
import rs.ac.bg.etf.barberbooker.ui.stateholders.BarberBookerViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientProfileViewModel

@Composable
fun ClientEditProfileScreen(
    clientEmail: String,
    snackbarHostState: SnackbarHostState,
    barberBookerViewModel: BarberBookerViewModel,
    clientProfileViewModel: ClientProfileViewModel = hiltViewModel()
) {
    val snackbarCoroutineScope = rememberCoroutineScope()
    var isClientDataFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val job = clientProfileViewModel.fetchClientData(clientEmail)
        job.join()
        isClientDataFetched = true
    }

    if (!isClientDataFetched) return

    val uiState by clientProfileViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

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
            Log.e("ClientEditProfileScreen", e.message ?: "ERROR")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange =  { clientProfileViewModel.setName(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = { Text(text = "Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                isError = !uiState.isNameValid,
                placeholder = {
                    Text(
                        text = "e.g., Milos",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = uiState.surname,
                onValueChange =  { clientProfileViewModel.setSurname(it) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                ),
                label = {
                    Text(text = "Surname")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                isError = !uiState.isSurnameValid,
                placeholder = {
                    Text(
                        text = "e.g., Milosevic",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedButton(
                onClick = {
                    activityContext?.let {
                        val googleSignInIntent = barberBookerViewModel.googleSignInClient.signInIntent
                        googleSignInLauncher.launch(googleSignInIntent)
                    }
                },
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
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
                    clientProfileViewModel.updateProfile(
                        clientEmail,
                        snackbarHostState,
                        snackbarCoroutineScope
                    )
                },
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSecondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = "Submit"
                )
            }
        }
    }
}
