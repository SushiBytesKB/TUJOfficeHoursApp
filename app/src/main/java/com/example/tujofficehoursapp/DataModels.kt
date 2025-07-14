package com.example.tujofficehoursapp

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class User(
    val uid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val role: String = "Student"
)

data class Professor(
    val uid: String = "",
    val name: String = "",
    val email: String = ""
)

// MODIFICATION: Replaced Google's DateTime with Firebase's Timestamp.
// startTime and endTime now only store time-of-day information as strings in "HH:mm" format.
data class ProfessorOfficeHours(
    val daysOfWeek: List<String> = emptyList(),
    val startTime: String = "09:00", // Store as "HH:mm" string
    val endTime: String = "17:00",   // Store as "HH:mm" string
    val slotDurationMinutes: Int = 10,
    val professorId: String = "",
    val location: String = ""
)

// MODIFICATION: Replaced Google's Date/DateTime with Firebase's Timestamp.
// A single Timestamp is sufficient to store the exact date and time of an event.
data class Reservation(
    val professorId: String = "",
    val studentId: String = "",
    // @ServerTimestamp will automatically populate this with the server's time upon creation.
    // We will overwrite it with the actual appointment time.
    @ServerTimestamp val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val note: String = "",
    val reservationId: String = UUID.randomUUID().toString(),
    val professorName: String = "",
    val studentName: String = ""
    // The old endTimeTimestamp is no longer needed.
)