package rs.ac.bg.etf.barberbooker.ui.elements.screens.user.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.ac.bg.etf.barberbooker.data.*
import rs.ac.bg.etf.barberbooker.ui.elements.composables.user.StarRating
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.barber.BarberProfileViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientArchiveViewModel
import rs.ac.bg.etf.barberbooker.ui.stateholders.user.client.ClientSearchBarbersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSearchBarbersScreen(
    clientEmail: String,
    navHostController: NavHostController,
    clientSearchBarbersViewModel: ClientSearchBarbersViewModel = hiltViewModel(),
    clientArchiveViewModel: ClientArchiveViewModel = hiltViewModel(),
    barberProfileViewModel: BarberProfileViewModel = hiltViewModel()
) {
    var isArchiveFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val updateReservationsJob = barberProfileViewModel.updateReservationStatuses()
        updateReservationsJob.join()
        val archiveJob = clientArchiveViewModel.getArchive(clientEmail)
        archiveJob.join()
        isArchiveFetched = true
    }

    if (!isArchiveFetched) return

    val uiState by clientSearchBarbersViewModel.uiState.collectAsState()
    val clientArchiveUiState by clientArchiveViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var expanded by rememberSaveable { mutableStateOf(false) }
    var areSearchResultsFetched by rememberSaveable { mutableStateOf(false) }

    var showSortingDialog by rememberSaveable { mutableStateOf(false) }

    val (sortingByGradeState, onSortingByGradeStateChange) = rememberSaveable { mutableStateOf(false) }
    val (sortingByGradeAscendingState, onSortingByGradeAscendingStateChange) = rememberSaveable { mutableStateOf(true) }
    val (sortingByGradeDescendingState, onSortingByGradeDescendingStateChange) = rememberSaveable { mutableStateOf(false) }

    val (sortingByPriceState, onSortingByPriceStateChange) = rememberSaveable { mutableStateOf(false) }
    val (sortingByPriceAscendingState, onSortingByPriceAscendingStateChange) = rememberSaveable { mutableStateOf(true) }
    val (sortingByPriceDescendingState, onSortingByPriceDescendingStateChange) = rememberSaveable { mutableStateOf(false) }

    val barbershopSuggestions = clientArchiveUiState.archive
        .filter { it.status == reservationStatuses[DONE_SUCCESS_RESERVATION_STATUS_INDEX] }
        .distinctBy { it.barberEmail }
        .filter { currentRequest ->
            uiState.query.isBlank() ||
            currentRequest.barbershopName.startsWith(uiState.query, ignoreCase = true) ||
            currentRequest.barberCity.startsWith(uiState.query, ignoreCase = true) ||
            currentRequest.barberMunicipality.startsWith(uiState.query, ignoreCase = true)
        }

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            query = uiState.query,
            onQueryChange = { clientSearchBarbersViewModel.setQuery(it) },
            onSearch = {
                coroutineScope.launch {
                    val job = clientSearchBarbersViewModel.getSearchResults()
                    job.join()
                    expanded = false
                    areSearchResultsFetched = true
                }
                       },
            active = expanded,
            onActiveChange = { expanded = it },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondary,
                dividerColor = MaterialTheme.colorScheme.onSecondary,
                inputFieldColors = inputFieldColors(
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSecondary,
                    focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.onSecondary
                )
            ),
            leadingIcon = {
                if (!expanded) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.clickable {
                            clientSearchBarbersViewModel.setQuery("")
                            expanded = false
                        }
                    )
                }
            },
            trailingIcon = {
                if (expanded && uiState.query != "") {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = "Delete query",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.clickable {
                            clientSearchBarbersViewModel.setQuery("")
                        }
                    )
                }
                if (!expanded && areSearchResultsFetched && uiState.searchResults.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.clickable {
                            showSortingDialog = true
                        }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            placeholder = { Text(text = "Search barbershops here") }
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                barbershopSuggestions.forEach {currentRequest ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = currentRequest.barbershopName,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "${currentRequest.barberMunicipality}, ${currentRequest.barberCity}",
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier =
                        Modifier
                            .clickable {
                                coroutineScope.launch {
                                    expanded = false
                                    delay(500)
                                    navHostController.navigate(
                                        "${staticRoutes[CLIENT_VIEW_BARBER_PROFILE_SCREEN_ROUTE_INDEX]}/${currentRequest.barberEmail}/${clientEmail}"
                                    )
                                }
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }

        if (!areSearchResultsFetched) return@Box

        if (uiState.searchResults.isEmpty()) {
            Text(
                text = "No results found on BarberBooker.",
                modifier = Modifier
                    .padding(start = 16.dp, top = 80.dp, end = 16.dp, bottom = 16.dp)
            )
        } else {
            val sortedSearchResults = clientSearchBarbersViewModel.getSortedSearchResults(
                sortingByGradeState, sortingByGradeAscendingState, sortingByGradeDescendingState,
                sortingByPriceState, sortingByPriceAscendingState, sortingByPriceDescendingState
            )
            LazyColumn(
                contentPadding = PaddingValues(top = 72.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.semantics { traversalIndex = 1f },
            ) {
                items(count = uiState.searchResults.size) {
                    val currentBarbershop = sortedSearchResults[it]
                    ListItem(
                        headlineContent = {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.ContentCut,
                                    contentDescription = "Barbershop name",
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.CenterVertically),
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentBarbershop.barbershopName,
                                    color = MaterialTheme.colorScheme.onSecondary
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
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${currentBarbershop.municipality}, ${currentBarbershop.city}",
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                Row {
                                    Icon(
                                        imageVector = Icons.Filled.Reviews,
                                        contentDescription = "Average Rating",
                                        modifier = Modifier
                                            .size(10.dp)
                                            .align(Alignment.CenterVertically),
                                        tint = MaterialTheme.colorScheme.onSecondary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    val averageGrade = currentBarbershop.averageGrade
                                    if (averageGrade == 0.00f) {
                                        Text(
                                            text = "No reviews",
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                    } else {
                                        Text(
                                            text = clientSearchBarbersViewModel.decimalFormat.format(averageGrade)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        StarRating(
                                            rating = averageGrade,
                                            size = 18.dp
                                        )
                                    }
                                }
                            }
                        },
                        trailingContent = {
                            Row {
                                Icon(
                                    imageVector = Icons.Filled.AttachMoney,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "${clientSearchBarbersViewModel.decimalFormat.format(currentBarbershop.price)} RSD",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }

                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable {
                                navHostController.navigate(
                                    "${staticRoutes[CLIENT_VIEW_BARBER_PROFILE_SCREEN_ROUTE_INDEX]}/${currentBarbershop.email}/${clientEmail}"
                                )
                            }
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    if (showSortingDialog) {
        AlertDialog(
            onDismissRequest = { showSortingDialog = false },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.secondary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row {
                    IconButton(onClick = { showSortingDialog = false }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close dialog",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .toggleable(
                            value = sortingByGradeState,
                            onValueChange = { onSortingByGradeStateChange(!sortingByGradeState) },
                            role = Role.Checkbox
                        )
                ) {
                    Checkbox(
                        checked = sortingByGradeState,
                        onCheckedChange = null
                    )
                    Text(
                        text = "Sort by average grade",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 8.dp)
                        .toggleable(
                            value = sortingByGradeAscendingState,
                            onValueChange = {
                                if (sortingByGradeState) {
                                    onSortingByGradeDescendingStateChange(false)
                                    onSortingByGradeAscendingStateChange(true)
                                }
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = sortingByGradeAscendingState,
                        onClick = null,
                        enabled = sortingByGradeState
                    )
                    Text(
                        text = "Ascending",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 8.dp)
                        .toggleable(
                            value = sortingByGradeDescendingState,
                            onValueChange = {
                                if (sortingByGradeState) {
                                    onSortingByGradeAscendingStateChange(false)
                                    onSortingByGradeDescendingStateChange(true)
                                }
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = sortingByGradeDescendingState,
                        onClick = null,
                        enabled = sortingByGradeState
                    )
                    Text(
                        text = "Descending",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .toggleable(
                            value = sortingByPriceState,
                            onValueChange = { onSortingByPriceStateChange(!sortingByPriceState) },
                            role = Role.Checkbox
                        )
                ) {
                    Checkbox(
                        checked = sortingByPriceState,
                        onCheckedChange = null
                    )
                    Text(
                        text = "Sort by price",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 8.dp)
                        .toggleable(
                            value = sortingByPriceAscendingState,
                            onValueChange = {
                                if (sortingByPriceState) {
                                    onSortingByPriceDescendingStateChange(false)
                                    onSortingByPriceAscendingStateChange(true)
                                }
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = sortingByPriceAscendingState,
                        onClick = null,
                        enabled = sortingByPriceState
                    )
                    Text(
                        text = "Ascending",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 8.dp)
                        .toggleable(
                            value = sortingByPriceDescendingState,
                            onValueChange = {
                                if (sortingByPriceState) {
                                    onSortingByPriceAscendingStateChange(false)
                                    onSortingByPriceDescendingStateChange(true)
                                }
                            },
                            role = Role.RadioButton
                        )
                ) {
                    RadioButton(
                        selected = sortingByPriceDescendingState,
                        onClick = null,
                        enabled = sortingByPriceState
                    )
                    Text(
                        text = "Descending",
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}