package com.example.tujofficehoursapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tujofficehoursapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userRole: String,
    onNavigateToReservations: () -> Unit,
    onNavigateToProfessors: () -> Unit,
    onNavigateToOfficeHours: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeFormat24hr by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

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
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingItem(label = "Time Zone") {
                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = "JST (Japan Standard)",
                                onValueChange = {},
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                            ) {

                                DropdownMenuItem(text = { Text("JST") }, onClick = { isDropdownExpanded = false })
                                DropdownMenuItem(text = { Text("EST") }, onClick = { isDropdownExpanded = false })
                            }
                        }
                    }
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingItem(label = "Use 24-hour format") {
                        Switch(
                            checked = timeFormat24hr,
                            onCheckedChange = { timeFormat24hr = it },
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
                modifier = Modifier.fillMaxWidth()
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