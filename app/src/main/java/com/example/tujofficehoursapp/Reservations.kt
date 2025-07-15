//// Reservations.kt
//package com.example.tujofficehoursapp
//
//import android.app.Application
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.tujofficehoursapp.data.AppDatabase
//import com.example.tujofficehoursapp.data.SettingsRepository
//import com.example.tujofficehoursapp.data.UserSettings
//import com.example.tujofficehoursapp.ui.theme.*
//import com.google.firebase.Timestamp
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.firestore.ktx.toObjects
//import com.google.firebase.ktx.Firebase
//import com.google.type.DateTime
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.flow.stateIn
//import java.time.ZoneId
//import java.time.format.DateTimeFormatter
//import com.google.type.Date as GoogleDate
//
//// MODIFICATION: New UI state class to hold both reservations and user settings for formatting.
//data class ReservationsUiState(
//    val reservations: List<Reservation> = emptyList(),
//    val settings: UserSettings = UserSettings()
//)
//
//// MODIFICATION: ViewModel now gets settings from the repository to format dates/times correctly.
//class ReservationsViewModel(settingsRepository: SettingsRepository) : ViewModel() {
//    private val db = Firebase.firestore
//    private val auth = Firebase.auth
//    private val userId = auth.currentUser?.uid ?: ""
//
//    // MODIFICATION: Combine the reservations flow and settings flow into one UI state.
//    val uiState: StateFlow<ReservationsUiState> = combine(
//        getReservationsFlow(),
//        settingsRepository.getSettings()
//    ) { reservations, settings ->
//        ReservationsUiState(reservations, settings ?: UserSettings())
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = ReservationsUiState()
//    )
//
//    private fun getReservationsFlow(): StateFlow<List<Reservation>> {
//        val flow = MutableStateFlow<List<Reservation>>(emptyList())
//        // MODIFICATION: Query now checks the endTime to only show upcoming reservations.
//        // This assumes a 'endTimeTimestamp' field of type Timestamp is saved on the Reservation document.
//        db.collection("reservations")
//            .whereEqualTo("studentId", userId)
//            .whereGreaterThan("endTimeTimestamp", Timestamp.now())
//            .addSnapshotListener { snapshot, _ ->
//                if (snapshot != null) {
//                    flow.value = snapshot.toObjects()
//                }
//            }
//        return flow
//    }
//}
//
//// MODIFICATION: New ViewModel Factory to provide the repository to the ViewModel.
//class ReservationsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ReservationsViewModel::class.java)) {
//            val database = AppDatabase.getDatabase(application)
//            val repository = SettingsRepository(database.userSettingsDao())
//            @Suppress("UNCHECKED_CAST")
//            return ReservationsViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
//
//
//// The UI for the Student's Reservations Screen
//@Composable
//fun ReservationsScreen(
//    onNavigateToProfessors: () -> Unit,
//    onNavigateToSettings: () -> Unit,
//    modifier: Modifier = Modifier,
//    // MODIFICATION: ViewModel is now created with the factory.
//    viewModel: ReservationsViewModel = viewModel(factory = ReservationsViewModelFactory(LocalContext.current.applicationContext as Application))
//) {
//    // MODIFICATION: Collect the combined UI state.
//    val uiState by viewModel.uiState.collectAsState()
//
//    Column(modifier = modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier.weight(1f).padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "My Reservations",
//                style = Typography.headlineLarge,
//                fontWeight = FontWeight.Bold,
//                color = TempleRed,
//                fontFamily = TujFont,
//                modifier = Modifier.padding(bottom = 24.dp)
//            )
//
//            if (uiState.reservations.isEmpty()) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Text("You have no upcoming reservations.", color = TextColor.copy(alpha = 0.7f))
//                }
//            } else {
//                LazyColumn(
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    contentPadding = PaddingValues(bottom = 16.dp)
//                ) {
//                    items(uiState.reservations) { reservation ->
//                        // MODIFICATION: Pass the reservation and settings to the card for correct formatting.
//                        ReservationInfoCard(
//                            reservation = reservation,
//                            settings = uiState.settings
//                        )
//                    }
//                }
//            }
//        }
//
//        StudentBottomNavBar(
//            currentRoute = "student_reservations",
//            onNavigateToReservations = { /* Already here */ },
//            onNavigateToProfessors = onNavigateToProfessors,
//            onNavigateToSettings = onNavigateToSettings
//        )
//    }
//}
//

// Reservations.kt
package com.example.tujofficehoursapp

import android.app.Application
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
import com.google.type.DateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.type.Date as GoogleDate

// MODIFICATION: New UI state class to hold both reservations and user settings for formatting.
data class ReservationsUiState(
    val reservations: List<Reservation> = emptyList(),
    val settings: UserSettings = UserSettings()
)

class ReservationsViewModel(settingsRepository: SettingsRepository) : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid ?: ""

    val uiState: StateFlow<ReservationsUiState> = combine(
        getReservationsFlow(),
        settingsRepository.getSettings()
    ) { reservations, settings ->
        ReservationsUiState(reservations, settings ?: UserSettings())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReservationsUiState()
    )

    private fun getReservationsFlow(): StateFlow<List<Reservation>> {
        val flow = MutableStateFlow<List<Reservation>>(emptyList())
        // MODIFICATION: The query now correctly checks the 'endTime' field instead of the non-existent 'endTimeTimestamp'.
        db.collection("reservations")
            .whereEqualTo("studentId", userId)
            .whereGreaterThan("endTime", Timestamp.now()) // <-- Corrected field name
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    flow.value = snapshot.toObjects()
                }
            }
        return flow
    }
}

// MODIFICATION: New ViewModel Factory to provide the repository to the ViewModel.
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


// The UI for the Student's Reservations Screen
@Composable
fun ReservationsScreen(
    onNavigateToProfessors: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    // MODIFICATION: ViewModel is now created with the factory.
    viewModel: ReservationsViewModel = viewModel(factory = ReservationsViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    // MODIFICATION: Collect the combined UI state.
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize())
    {
        Image(
            painter = painterResource(id = R.drawable.backgroundnew), // <-- Change this to your file name
            contentDescription = null, // for decorative images
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)

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
                            // MODIFICATION: Pass the reservation and settings to the card for correct formatting.
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

