package com.example.tujofficehoursapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
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

class ReservedViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _bookings = MutableStateFlow<List<Reservation>>(emptyList())
    val bookings = _bookings.asStateFlow()

    init {
        fetchProfessorBookings()
    }

    private fun fetchProfessorBookings() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            db.collection("reservations")
                .whereEqualTo("professorId", userId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        _bookings.value = snapshot.toObjects()
                    }
                }
        }
    }
}

@Composable
fun ReservedScreen(
    onNavigateToOfficeHours: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReservedViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Student Reservations",
                style = Typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = TempleRed,
                fontFamily = TujFont,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (bookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You have no upcoming appointments.", color = TextColor.copy(alpha = 0.7f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(bookings) { reservation ->
                        ReservationInfoCard(reservation = reservation, isProfessorView = true)
                    }
                }
            }
        }

        ProfessorBottomNavBar(
            currentRoute = "professor_reserved",
            onNavigateToReservations = { /* Already here */ },
            onNavigateToOfficeHours = onNavigateToOfficeHours,
            onNavigateToSettings = onNavigateToSettings
        )
    }
}