package rs.ac.bg.etf.barberbooker.ui.elements.composables.guest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rs.ac.bg.etf.barberbooker.ui.stateholders.guest.registration.BarberRegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkingHoursDropdown(
    barberRegistrationViewModel: BarberRegistrationViewModel,
    areWorkingDayStartTimes: Boolean,
    onTimeSelected: (String) -> Unit
) {
    val barberRegistrationUiState by barberRegistrationViewModel.uiState.collectAsState()
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = if (areWorkingDayStartTimes) barberRegistrationUiState.selectedWorkingDayStartTime else barberRegistrationUiState.selectedWorkingDayEndTime,
            onValueChange = {},
            readOnly = true,
            label = { Text(
                text = "Select " + (if (areWorkingDayStartTimes) "Start" else "End") + " Time",
                color = MaterialTheme.colorScheme.onSecondary
            ) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                focusedContainerColor = MaterialTheme.colorScheme.secondary,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary,
                focusedLabelColor = MaterialTheme.colorScheme.onSecondary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .menuAnchor()
                .border(2.dp, MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.secondary)
        ) {
            val workingDayTimesList = if (areWorkingDayStartTimes) barberRegistrationUiState.validWorkingDayStartTimes else barberRegistrationUiState.validWorkingDayEndTimes
            workingDayTimesList.forEachIndexed { index, slot ->
                Column(modifier = Modifier.fillMaxWidth())
                {
                    DropdownMenuItem(
                        text = { Text(
                            text = slot,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) },
                        onClick = {
                            onTimeSelected(slot)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSecondary,
                            disabledTextColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.secondary)
                    )
                    if (index < workingDayTimesList.lastIndex)
                    {
                        Divider(
                            color = MaterialTheme.colorScheme.onSecondary,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}