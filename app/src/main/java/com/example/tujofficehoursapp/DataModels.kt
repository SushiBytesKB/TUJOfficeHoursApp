package com.example.tujofficehoursapp

import java.util.UUID

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "Student"
)

data class Professor(
    val uid: String = "",
    val name: String = "Not Set",
    val email: String = "",
    val officeHours: String = "Not specified",
    val classDetails: String = "Not specified"
)

data class Reservation(
    val id: String = UUID.randomUUID().toString(),
    val professorId: String = "",
    val professorName: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val preferredTime: String = "",
    val note: String = ""
)