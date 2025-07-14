// CommonUI.kt
package com.example.tujofficehoursapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tujofficehoursapp.data.UserSettings
import com.example.tujofficehoursapp.ui.theme.*
import com.google.type.DateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.type.Date as GoogleDate


// StudentBottomNavBar remains the same
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

// ProfessorBottomNavBar remains the same
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

// MODIFICATION: This is the new, centralized card for displaying reservation details.
@Composable
fun ReservationInfoCard(
    reservation: Reservation,
    settings: UserSettings,
    isProfessorView: Boolean = false,
    modifier: Modifier = Modifier
) {
    fun formatTimestamp(dateTime: DateTime, is24Hour: Boolean, zoneId: ZoneId): String {
        val timePattern = if (is24Hour) "HH:mm" else "hh:mm a"
        val formatter = DateTimeFormatter.ofPattern(timePattern).withZone(zoneId)
        val localTime = java.time.LocalTime.of(dateTime.hours, dateTime.minutes)
        return formatter.format(localTime)
    }

    fun formatDate(googleDate: GoogleDate, zoneId: ZoneId): String {
        val localDate = java.time.LocalDate.of(googleDate.year, googleDate.month, googleDate.day)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d").withZone(zoneId)
        return formatter.format(localDate)
    }

    val defaultTime = DateTime.newBuilder().setHours(0).setMinutes(0).build()
    val resultStartTime = if (reservation.startTime == null) defaultTime else reservation.startTime
    val resultEndTime = if (reservation.endTime == null) defaultTime else reservation.endTime

    val zoneId = remember(settings.timezone) { ZoneId.of(settings.timezone) }
    val time = "${formatTimestamp(resultStartTime, settings.is24Hour, zoneId)} - ${formatTimestamp(resultEndTime, settings.is24Hour, zoneId)}"
    val dayAndDate = formatDate(reservation.date, zoneId)

    val title = if (isProfessorView) {
        reservation.studentName
    } else {
        "vs. ${reservation.professorName}"
    }

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
            Text(text = title, style = Typography.titleLarge, color = TextColor)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            ReservationDetailRow(label = "Date:", value = dayAndDate)
            ReservationDetailRow(label = "Time:", value = time)
            if (reservation.note.isNotBlank()) {
                ReservationDetailRow(label = "Note:", value = reservation.note)
            }
        }
    }
}

// MODIFICATION: A reusable row for displaying details in the card, with bold label.
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