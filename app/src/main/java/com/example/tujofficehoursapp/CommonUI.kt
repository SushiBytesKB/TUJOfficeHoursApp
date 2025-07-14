package com.example.tujofficehoursapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tujofficehoursapp.ui.theme.*

@Composable
fun StudentBottomNavBar(
    currentRoute: String,
    onNavigateToReservations: () -> Unit,
    onNavigateToProfessors: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    NavigationBar(containerColor = NeutralColor) {
        NavigationBarItem(
            selected = currentRoute == "student_reservations",
            onClick = onNavigateToReservations,
            icon = { Icon(Icons.Default.DateRange, "Reservations") },
            label = { Text("Reservations") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentColor, selectedTextColor = AccentColor, unselectedIconColor = ButtonColor, unselectedTextColor = ButtonColor, indicatorColor = NeutralColor)
        )
        NavigationBarItem(
            selected = currentRoute == "student_professors",
            onClick = onNavigateToProfessors,
            icon = { Icon(Icons.Default.Person, "Professors") },
            label = { Text("Professors") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentColor, selectedTextColor = AccentColor, unselectedIconColor = ButtonColor, unselectedTextColor = ButtonColor, indicatorColor = NeutralColor)
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Default.Settings, "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentColor, selectedTextColor = AccentColor, unselectedIconColor = ButtonColor, unselectedTextColor = ButtonColor, indicatorColor = NeutralColor)
        )
    }
}

@Composable
fun ProfessorBottomNavBar(
    currentRoute: String,
    onNavigateToReservations: () -> Unit,
    onNavigateToOfficeHours: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    NavigationBar(containerColor = NeutralColor) {
        NavigationBarItem(
            selected = currentRoute == "professor_reserved",
            onClick = onNavigateToReservations,
            icon = { Icon(Icons.Default.DateRange, "Reservations") },
            label = { Text("Reservations") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentColor, selectedTextColor = AccentColor, unselectedIconColor = ButtonColor, unselectedTextColor = ButtonColor, indicatorColor = NeutralColor)
        )
        NavigationBarItem(
            selected = currentRoute == "professor_office_hours",
            onClick = onNavigateToOfficeHours,
            icon = { Icon(Icons.Default.Event, "My Office Hours") },
            label = { Text("My Office Hours") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentColor, selectedTextColor = AccentColor, unselectedIconColor = ButtonColor, unselectedTextColor = ButtonColor, indicatorColor = NeutralColor)
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = onNavigateToSettings,
            icon = { Icon(Icons.Default.Settings, "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = AccentColor, selectedTextColor = AccentColor, unselectedIconColor = ButtonColor, unselectedTextColor = ButtonColor, indicatorColor = NeutralColor)
        )
    }
}

@Composable
fun ReservationInfoCard(
    reservation: Reservation,
    isProfessorView: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = NeutralColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show professor name for student, and student name for professor
            val title = if (isProfessorView) reservation.studentName else reservation.professorName
            Text(text = title, style = Typography.titleLarge, fontWeight = FontWeight.Bold, color = TextColor)

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            ReservationDetailRow(label = "Time:", value = reservation.preferredTime)
            if (reservation.note.isNotBlank()) {
                ReservationDetailRow(label = "Note:", value = reservation.note)
            }
        }
    }
}

@Composable
fun ReservationDetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(70.dp),
            color = TextColor
        )
        Text(
            text = value,
            style = Typography.bodyLarge,
            color = TextColor.copy(alpha = 0.8f)
        )
    }
}