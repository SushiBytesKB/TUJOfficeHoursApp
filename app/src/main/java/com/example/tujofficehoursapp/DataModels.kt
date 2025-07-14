package com.example.tujofficehoursapp

<<<<<<< HEAD
=======
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
>>>>>>> main
import java.util.UUID

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "Student"
)

data class Professor(
    val uid: String = "",
<<<<<<< HEAD
    val name: String = "Not Set",
    val email: String = "",
    val officeHours: String = "Not specified",
    val classDetails: String = "Not specified"
=======
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
>>>>>>> main
)

// MODIFICATION: Replaced Google's Date/DateTime with Firebase's Timestamp.
// A single Timestamp is sufficient to store the exact date and time of an event.
data class Reservation(
    val id: String = UUID.randomUUID().toString(),
    val professorId: String = "",
    val professorName: String = "",
    val studentId: String = "",
<<<<<<< HEAD
    val studentName: String = "",
    val preferredTime: String = "",
    val note: String = ""
)
=======
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
>>>>>>> main
