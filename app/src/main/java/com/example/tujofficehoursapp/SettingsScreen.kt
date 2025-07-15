package com.example.tujofficehoursapp

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tujofficehoursapp.data.AppDatabase
import com.example.tujofficehoursapp.data.SettingsRepository
import com.example.tujofficehoursapp.data.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tujofficehoursapp.ui.theme.*

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val settingsState = repository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    fun updateTimezone(timezone: String) = viewModelScope.launch {
        val currentSettings = settingsState.value ?: UserSettings()
        repository.saveSettings(currentSettings.copy(timezone = timezone))
    }

    fun updateIs24Hour(is24Hour: Boolean) = viewModelScope.launch {
        val currentSettings = settingsState.value ?: UserSettings()
        repository.saveSettings(currentSettings.copy(is24Hour = is24Hour))
    }
}

class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = SettingsRepository(database.userSettingsDao())
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userRole: String,
    onNavigateToReservations: () -> Unit,
    onNavigateToProfessors: () -> Unit,
    onNavigateToOfficeHours: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val settings by viewModel.settingsState.collectAsState()
    val timeZoneOptions = listOf("Tokyo (JST)", "Philadelphia (GMT-4)", "Austria (GMT+2)")
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize())
    {
        Image(
            painter = painterResource(id = R.drawable.backgroundsupertransparent), // <-- Change this to your file name
            contentDescription = null, // for decorative images
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    style = Typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TempleRed,
                    fontFamily = TujFont,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                SectionHeader(title = "Time & Region")
                Card(
                    colors = CardDefaults.cardColors(containerColor = NeutralColor),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column {
                        SettingItem(label = "Time Zone") {
                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                            ) {
                                OutlinedTextField(
                                    readOnly = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = NeutralColor,
                                        focusedIndicatorColor = AccentColor),
                                    value = settings?.timezone ?: "JST",
                                    onValueChange = {},
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    timeZoneOptions.forEach { zone ->
                                        DropdownMenuItem(
                                            text = { Text(zone) },
                                            onClick = {
                                                viewModel.updateTimezone(zone)
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        SettingItem(label = "Use 24-hour format") {
                            Switch(
                                checked = settings?.is24Hour ?: true,
                                onCheckedChange = { viewModel.updateIs24Hour(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = TempleRed
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(title = "Notifications")
                Card(
                    colors = CardDefaults.cardColors(containerColor = NeutralColor),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    SettingItem(label = "Enable Notifications") {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }, // Changes UI state only
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TempleRed
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Logout")
                }
            }

            if (userRole == "Student") {
                StudentBottomNavBar(
                    currentRoute = "settings",
                    onNavigateToReservations = onNavigateToReservations,
                    onNavigateToProfessors = onNavigateToProfessors,
                    onNavigateToSettings = { /* Already here */ }
                )
            } else {
                ProfessorBottomNavBar(
                    currentRoute = "settings",
                    onNavigateToReservations = onNavigateToReservations,
                    onNavigateToOfficeHours = onNavigateToOfficeHours,
                    onNavigateToSettings = { /* Already here */ }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
        color = TextColor
    )
}

@Composable
private fun SettingItem(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = TextColor
        )
        content()
    }
}