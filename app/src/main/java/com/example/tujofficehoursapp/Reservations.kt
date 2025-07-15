// Reservations.kt
package com.example.tujofficehoursapp

import android.app.Application
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tujofficehoursapp.data.AppDatabase
import com.example.tujofficehoursapp.data.SettingsRepository
import com.example.tujofficehoursapp.data.UserSettings
import com.example.tujofficehoursapp.ui.theme.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ReservationsUiState(
    val reservations: List<Reservation> = emptyList(),
    val settings: UserSettings = UserSettings()
)

class ReservationsViewModel(settingsRepository: SettingsRepository) : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid ?: ""

    val uiState: StateFlow<ReservationsUiState> = combine(
        // CORRECTION: Added .catch operator to handle potential Firestore index errors gracefully.
        getReservationsFlow().catch { exception ->
            // This will log the error in Logcat without crashing the app.
            Log.e("ReservationsViewModel", "Error fetching reservations, index likely missing.", exception)
            // Emit an empty list so the UI can still display something.
            emit(emptyList())
        },
        settingsRepository.getSettings()
    ) { reservations, settings ->
        ReservationsUiState(reservations, settings ?: UserSettings())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReservationsUiState()
    )

    private fun getReservationsFlow(): Flow<List<Reservation>> = callbackFlow {
        val listenerRegistration = db.collection("reservations")
            .whereEqualTo("studentId", userId)
            .whereGreaterThan("endTime", Timestamp.now())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Propagate the error to be caught by the .catch operator
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects())
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}

class ReservationsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationsViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = SettingsRepository(database.userSettingsDao())
            @Suppress("UNCHECKED_CAST")
            return ReservationsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun ReservationsScreen(
    onNavigateToProfessors: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReservationsViewModel = viewModel(factory = ReservationsViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize())
    {
        Image(
            painter = painterResource(id = R.drawable.backgroundnew),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)

        Column(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
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

                if (uiState.reservations.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("You have no upcoming reservations.", color = TextColor.copy(alpha = 0.7f))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.reservations) { reservation ->
                            ReservationInfoCard(
                                reservation = reservation,
                                settings = uiState.settings
                            )
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
}
