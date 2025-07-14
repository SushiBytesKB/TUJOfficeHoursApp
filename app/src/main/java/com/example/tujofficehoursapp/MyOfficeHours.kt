package com.example.tujofficehoursapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyOfficeHoursViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid ?: ""

    private val _professor = MutableStateFlow<Professor?>(null)
    val professor = _professor.asStateFlow()

    init {
        viewModelScope.launch {
            db.collection("professors").document(userId).get()
                .addOnSuccessListener { document ->
                    _professor.value = document.toObject<Professor>()
                }
        }
    }

    fun saveProfile(name: String, officeHours: String, classDetails: String, onComplete: () -> Unit) {
        val updatedData = mapOf(
            "name" to name,
            "officeHours" to officeHours,
            "classDetails" to classDetails
        )
        db.collection("professors").document(userId).update(updatedData)
            .addOnSuccessListener { onComplete() }
    }
}

@Composable
fun MyOfficeHoursScreen(
    onNavigateToReservations: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyOfficeHoursViewModel = viewModel()
) {
    val professor by viewModel.professor.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var officeHours by remember { mutableStateOf("") }
    var classDetails by remember { mutableStateOf("") }

    LaunchedEffect(professor) {
        professor?.let {
            name = it.name
            officeHours = it.officeHours
            classDetails = it.classDetails
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Public Profile",
                style = Typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TempleRed,
                fontFamily = TujFont,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = classDetails,
                onValueChange = { classDetails = it },
                label = { Text("Class Details (e.g., CST-333)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = officeHours,
                onValueChange = { officeHours = it },
                label = { Text("Office Hours (e.g., M/W 10am-12pm)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.saveProfile(name, officeHours, classDetails) {
                        Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = TempleRed)
            ) {
                Text("Save Changes", modifier = Modifier.padding(8.dp))
            }
        }

        ProfessorBottomNavBar(
            currentRoute = "professor_office_hours",
            onNavigateToReservations = onNavigateToReservations,
            onNavigateToOfficeHours = { /* Already here */ },
            onNavigateToSettings = onNavigateToSettings
        )
    }
}