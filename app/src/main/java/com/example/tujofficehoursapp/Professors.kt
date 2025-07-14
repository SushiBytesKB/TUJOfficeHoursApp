package com.example.tujofficehoursapp

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tujofficehoursapp.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfessorsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _professors = MutableStateFlow<List<Professor>>(emptyList())
    val professors = _professors.asStateFlow()

    init {
        fetchProfessors()
    }

    private fun fetchProfessors() {
        db.collection("professors").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                _professors.value = snapshot.toObjects()
            }
        }
    }
}

@Composable
fun ProfessorsScreen(
    onNavigateToReservations: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onProfessorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfessorsViewModel = viewModel()
) {
    val professors by viewModel.professors.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "List of Professors",
                style = Typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TempleRed,
                fontFamily = TujFont,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(professors) { professor ->
                    ProfessorCard(
                        professor = professor,
                        onClick = { onProfessorClick(professor.uid) }
                    )
                }
            }
        }

        StudentBottomNavBar(
            currentRoute = "student_professors",
            onNavigateToReservations = onNavigateToReservations,
            onNavigateToProfessors = { /* Already here */ },
            onNavigateToSettings = onNavigateToSettings
        )
    }
}

@Composable
fun ProfessorCard(professor: Professor, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = NeutralColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = professor.name, style = Typography.titleLarge, color = TextColor)
            Text(text = professor.email, style = Typography.bodyMedium, color = TextColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun ProfessorDetailScreen(
    professorId: String,
    onBooked: () -> Unit
) {
    val db = Firebase.firestore
    var professor by remember { mutableStateOf<Professor?>(null) }
    var showBookingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(professorId) {
        db.collection("professors").document(professorId).get()
            .addOnSuccessListener { document ->
                professor = document.toObject<Professor>()
            }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            professor?.let {
                Text(it.name, style = Typography.headlineLarge, fontWeight = FontWeight.Bold, color = TempleRed)
                Spacer(modifier = Modifier.height(8.dp))
                Text(it.email, style = Typography.titleMedium, color = TextColor.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(24.dp))

                DetailItem(label = "Class Details", value = it.classDetails)
                DetailItem(label = "Office Hours", value = it.officeHours)
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { showBookingDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TempleRed)
                ) {
                    Text("Request Appointment", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }

    if (showBookingDialog) {
        BookingDialog(
            professor = professor!!,
            onDismiss = { showBookingDialog = false },
            onConfirmBooking = { studentName, time, note ->
                val newReservation = Reservation(
                    professorId = professor!!.uid,
                    professorName = professor!!.name,
                    studentId = Firebase.auth.currentUser?.uid ?: "",
                    studentName = studentName,
                    preferredTime = time,
                    note = note
                )
                db.collection("reservations").add(newReservation)
                    .addOnSuccessListener {
                        showBookingDialog = false
                        onBooked()
                    }
            }
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(label, style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = TextColor)
        Text(value, style = Typography.bodyLarge, color = TextColor.copy(alpha = 0.8f))
    }
}

@Composable
fun BookingDialog(
    professor: Professor,
    onDismiss: () -> Unit,
    onConfirmBooking: (String, String, String) -> Unit,
) {
    var studentName by remember { mutableStateOf("") }
    var preferredTime by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request appointment with ${professor.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = studentName, onValueChange = { studentName = it }, label = { Text("Your Name") })
                OutlinedTextField(value = preferredTime, onValueChange = { preferredTime = it }, label = { Text("Preferred Time") })
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Quick Note") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (studentName.isNotBlank() && preferredTime.isNotBlank()) {
                        onConfirmBooking(studentName, preferredTime, note)
                    } else {
                        Toast.makeText(context, "Please fill in your name and time.", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TempleRed)
            ) { Text("Submit Request") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}