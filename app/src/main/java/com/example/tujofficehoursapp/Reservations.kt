package com.example.tujofficehoursapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tujofficehoursapp.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations = _reservations.asStateFlow()

    init {
        fetchStudentReservations()
    }

    private fun fetchStudentReservations() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            db.collection("reservations")
                .whereEqualTo("studentId", userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        _reservations.value = snapshot.toObjects()
                    }
                }
        }
    }
}

@Composable
fun ReservationsScreen(
    onNavigateToProfessors: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReservationsViewModel = viewModel()
) {
    val reservations by viewModel.reservations.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Reservations",
                style = Typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TempleRed,
                fontFamily = TujFont,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (reservations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You have no upcoming reservations.", color = TextColor.copy(alpha = 0.7f))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(reservations) { reservation ->
                        ReservationInfoCard(reservation = reservation)
                    }
                }
            }
        }

        StudentBottomNavBar(
            currentRoute = "student_reservations",
            onNavigateToReservations = { /* Already here */ },
            onNavigateToProfessors = onNavigateToProfessors,
            onNavigateToSettings = onNavigateToSettings
        )
    }
}