package com.example.tujofficehoursapp

import com.google.firebase.Timestamp
import com.google.type.Date
import com.google.type.DateTime
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
    val startTime: DateTime? = null,
    val endTime: DateTime? = null,
    val slotDurationMinutes: Int = 10,
    val professorId: String = "",
    val location: String = ""
)

data class Reservation(
    val professorId: String = "",
    val studentId: String = "",
    val date: Date,
    val startTime: DateTime? = null,
    val endTime: DateTime? = null,
    val note: String = "",
    val reservationId: String = UUID.randomUUID().toString(),
    val professorName: String = "",
    val studentName: String = "",
    val endTimeTimestamp: Timestamp? = null
)