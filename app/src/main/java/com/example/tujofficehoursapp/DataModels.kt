package com.example.tujofficehoursapp

import com.google.type.Date
import com.google.type.DateTime
import java.util.UUID

data class User(
    val uid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val role: String = "Student"
)

data class ProfessorOfficeHours(
    val daysOfWeek: List<String> = emptyList(),
    val startTime: DateTime,
    val endTime: DateTime,
    val slotDurationMinutes: Int = 10,
    val professorId: String = "",
    val location: String = ""
)

data class Reservation(
    val professorId: String = "",
    val studentId: String = "",
    val date: Date,
    val startTime: DateTime,
    val endTime: DateTime,
    val note: String = "",
    val reservationId: String = UUID.randomUUID().toString()
)

data class Professor(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
)