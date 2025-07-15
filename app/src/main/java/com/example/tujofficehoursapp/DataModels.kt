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

data class ProfessorOfficeHours(
    val daysOfWeek: List<String> = emptyList(),
    val startTime: String = "09:00",
    val endTime: String = "17:00",
    val slotDurationMinutes: Int = 10,
    val professorId: String = "",
    val location: String = ""
)

data class Reservation(
    val professorId: String = "",
    val studentId: String = "",
    // @ServerTimestamp automatically populates this with server's time upon creation.
    @ServerTimestamp val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val note: String = "",
    val reservationId: String = UUID.randomUUID().toString(),
    val professorName: String = "",
    val studentName: String = ""
)