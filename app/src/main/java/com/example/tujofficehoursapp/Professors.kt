// Professors.kt
package com.example.tujofficehoursapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tujofficehoursapp.ui.theme.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

data class BookingUiState(
    val professors: List<Professor> = emptyList(),
    val selectedProfessor: Professor? = null,
    val officeHours: ProfessorOfficeHours? = null,
    val showBookingDialog: Boolean = false,
    val selectedDate: LocalDate? = null,
    val availableSlots: List<LocalTime> = emptyList(),
    val isLoadingSlots: Boolean = false
)

class ProfessorsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfessors()
    }

    private fun fetchProfessors() {
        db.collection("professors").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                _uiState.update { it.copy(professors = snapshot.toObjects()) }
            }
        }
    }

    fun onProfessorSelected(professor: Professor) {
        _uiState.update {
            it.copy(
                selectedProfessor = professor,
                showBookingDialog = true,
                officeHours = null,
                selectedDate = null,
                availableSlots = emptyList()
            )
        }
        db.collection("professors").document(professor.uid)
            .collection("config").document("officeHours")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _uiState.update { it.copy(officeHours = document.toObject<ProfessorOfficeHours>()) }
                }
            }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, isLoadingSlots = true, availableSlots = emptyList()) }
        val professorId = _uiState.value.selectedProfessor?.uid ?: return
        val officeHours = _uiState.value.officeHours ?: return

        viewModelScope.launch {
            val allSlots = generateAllPossibleSlots(officeHours)
            val bookedSlots = getBookedSlotsForDate(professorId, date)
            val availableSlots = allSlots.filter { it !in bookedSlots }
            _uiState.update { it.copy(availableSlots = availableSlots, isLoadingSlots = false) }
        }
    }

    private fun generateAllPossibleSlots(officeHours: ProfessorOfficeHours): List<LocalTime> {
        val slots = mutableListOf<LocalTime>()
        // MODIFICATION: Parse time from "HH:mm" string
        val formatter = DateTimeFormatter.ISO_LOCAL_TIME
        val startTime = LocalTime.parse(officeHours.startTime, formatter)
        val endTime = LocalTime.parse(officeHours.endTime, formatter)

        val duration = officeHours.slotDurationMinutes.toLong()
        var currentTime = startTime

        while (currentTime.plusMinutes(duration) <= endTime) {
            slots.add(currentTime)
            currentTime = currentTime.plusMinutes(duration)
        }
        return slots
    }

    private suspend fun getBookedSlotsForDate(professorId: String, date: LocalDate): Set<LocalTime> {
        // MODIFICATION: Query based on a start and end timestamp for the selected day
        val zoneId = ZoneId.systemDefault()
        val startOfDay = Timestamp(date.atStartOfDay(zoneId).toInstant())
        val endOfDay = Timestamp(date.plusDays(1).atStartOfDay(zoneId).toInstant())

        return try {
            val snapshot = db.collection("reservations")
                .whereEqualTo("professorId", professorId)
                .whereGreaterThanOrEqualTo("startTime", startOfDay)
                .whereLessThan("startTime", endOfDay)
                .get()
                .await()

            snapshot.toObjects<Reservation>().mapNotNull { reservation ->
                reservation.startTime?.let {
                    Instant.ofEpochSecond(it.seconds, it.nanoseconds.toLong())
                        .atZone(zoneId)
                        .toLocalTime()
                }
            }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    fun bookSlot(slot: LocalTime, note: String, onComplete: () -> Unit) {
        val studentId = auth.currentUser?.uid ?: return
        val studentName = auth.currentUser?.displayName ?: "Unknown Student"
        val professor = _uiState.value.selectedProfessor!!
        val date = _uiState.value.selectedDate!!
        val officeHours = _uiState.value.officeHours!!
        val duration = officeHours.slotDurationMinutes.toLong()

        // MODIFICATION: Convert LocalDate and LocalTime to Firebase Timestamps
        val zoneId = ZoneId.systemDefault()
        val startDateTime = date.atTime(slot)
        val startTimestamp = Timestamp(startDateTime.atZone(zoneId).toInstant())

        val endDateTime = startDateTime.plusMinutes(duration)
        val endTimestamp = Timestamp(endDateTime.atZone(zoneId).toInstant())

        val reservation = Reservation(
            professorId = professor.uid,
            studentId = studentId,
            studentName = studentName,
            professorName = professor.name,
            startTime = startTimestamp,
            endTime = endTimestamp,
            note = note
        )

        db.collection("reservations").add(reservation).addOnSuccessListener { onComplete() }
    }

    fun onDismissDialog() {
        _uiState.update {
            it.copy(showBookingDialog = false, selectedProfessor = null, officeHours = null, selectedDate = null)
        }
    }
}

// UI Composables (ProfessorCard, BookingDialog, etc.) remain largely the same,
// as they interact with the ViewModel, not directly with the data models.
// The existing UI code should work with the updated ViewModel state.

@Composable
fun ProfessorsScreen(
    onNavigateToReservations: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfessorsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize())
    {
        Image(
            painter = painterResource(id = R.drawable.backgroundnew), // <-- Change this to your file name
            contentDescription = null, // for decorative images
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop)

        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(uiState.professors) { professor ->
                        ProfessorCard(
                            professor = professor,
                            onClick = { viewModel.onProfessorSelected(professor) }
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


    if (uiState.showBookingDialog) {
        BookingDialog(
            uiState = uiState,
            onDismiss = { viewModel.onDismissDialog() },
            onDateSelected = { viewModel.onDateSelected(it) },
            onConfirmBooking = { slot, note ->
                viewModel.bookSlot(slot, note) {
                    Toast.makeText(context, "Booking successful!", Toast.LENGTH_SHORT).show()
                    viewModel.onDismissDialog()
                }
            }
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
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = professor.name,
                    style = Typography.titleLarge,
                    color = TextColor,
                    modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = NewButtonColor,
                    modifier = Modifier.padding(end = 17.dp)
                )
            }
            Text(text = professor.email, color = TextColor, fontSize = 14.sp, fontStyle = FontStyle.Italic)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TempleRed)
            ReservationDetailRow("Days:", "Monday, Wednesday, Friday")
            ReservationDetailRow("Times:", "13:00 - 14:20")
        }
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BookingDialog(
//    uiState: BookingUiState,
//    onDismiss: () -> Unit,
//    onDateSelected: (LocalDate) -> Unit,
//    onConfirmBooking: (LocalTime, String) -> Unit
//) {
//    var note by remember { mutableStateOf("") }
//    var showDatePicker by remember { mutableStateOf(false) }
//    var selectedSlot by remember { mutableStateOf<LocalTime?>(null) }
//    var isSlotDropdownExpanded by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = System.currentTimeMillis()
//    )
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Book with ${uiState.selectedProfessor?.name}") },
//        text = {
//            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
//                OutlinedTextField(
//                    value = uiState.selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "Select a date",
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Date") },
//                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
//                )
//
//                ExposedDropdownMenuBox(
//                    expanded = isSlotDropdownExpanded,
//                    onExpandedChange = {
//                        if (uiState.selectedDate != null && !uiState.isLoadingSlots) {
//                            isSlotDropdownExpanded = !isSlotDropdownExpanded
//                        }
//                    },
//                ) {
//                    OutlinedTextField(
//                        readOnly = true,
//                        value = selectedSlot?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Select a time slot",
//                        onValueChange = {},
//                        label = { Text("Available Slots") },
//                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSlotDropdownExpanded) },
//                        modifier = Modifier.menuAnchor().fillMaxWidth()
//                    )
//                    ExposedDropdownMenu(
//                        expanded = isSlotDropdownExpanded,
//                        onDismissRequest = { isSlotDropdownExpanded = false },
//                    ) {
//                        if (uiState.isLoadingSlots) {
//                            DropdownMenuItem(text = { Text("Loading...") }, onClick = {})
//                        } else if (uiState.availableSlots.isEmpty()) {
//                            DropdownMenuItem(text = { Text("No slots available") }, onClick = {})
//                        } else {
//                            uiState.availableSlots.forEach { slot ->
//                                DropdownMenuItem(
//                                    text = { Text(slot.format(DateTimeFormatter.ofPattern("HH:mm"))) },
//                                    onClick = {
//                                        selectedSlot = slot
//                                        isSlotDropdownExpanded = false
//                                    }
//
//                                )
//                            }
//                        }
//                    }
//                }
//
//                OutlinedTextField(
//                    value = note,
//                    onValueChange = { note = it },
//                    label = { Text("Note for professor (optional)") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
//        },
//        confirmButton = {
//            Button(
//                onClick = {
//                    selectedSlot?.let { onConfirmBooking(it, note) }
//                },
//                enabled = selectedSlot != null
//            ) { Text("Book Now") }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) { Text("Cancel") }
//        }
//    )
//
//    if (showDatePicker) {
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(onClick = {
//                    datePickerState.selectedDateMillis?.let { dateInMillis ->
//                        val selectedLocalDate = Instant.ofEpochMilli(dateInMillis)
//                            .atZone(ZoneId.systemDefault()).toLocalDate()
//
//                        val validDaysOfWeek = uiState.officeHours?.daysOfWeek?.mapNotNull {
//                            try {
//                                DayOfWeek.valueOf(it.uppercase(Locale.ROOT))
//                            } catch (e: IllegalArgumentException) { null }
//                        } ?: emptyList()
//
//                        val today = LocalDate.now()
//
//                        if (selectedLocalDate.dayOfWeek in validDaysOfWeek && !selectedLocalDate.isBefore(today)) {
//                            onDateSelected(selectedLocalDate)
//                            showDatePicker = false
//                        } else {
//                            Toast.makeText(context, "Professor is not available on this day.", Toast.LENGTH_SHORT).show()
//                        }
//                    } ?: run {
//                        Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
//                    }
//                }) { Text("OK") }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    uiState: BookingUiState,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onConfirmBooking: (LocalTime, String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedSlot by remember { mutableStateOf<LocalTime?>(null) }
    var isSlotDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text("Book with ${uiState.selectedProfessor?.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "Select a date",
                    onValueChange = {},
                    readOnly = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = AccentColor,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedLabelColor = AccentColor
                        ),
                    label = { Text("Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                ExposedDropdownMenuBox(
                    expanded = isSlotDropdownExpanded,
                    onExpandedChange = {
                        if (uiState.selectedDate != null && !uiState.isLoadingSlots) {
                            isSlotDropdownExpanded = !isSlotDropdownExpanded
                        }
                    },
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedSlot?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Select a time slot",
                        onValueChange = {},
                        label = { Text("Available Slots") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = AccentColor,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedLabelColor = AccentColor
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSlotDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isSlotDropdownExpanded,
                        onDismissRequest = { isSlotDropdownExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        if (uiState.isLoadingSlots) {
                            DropdownMenuItem(text = { Text("Loading...") }, onClick = {})
                        } else if (uiState.availableSlots.isEmpty()) {
                            DropdownMenuItem(text = { Text("No slots available") }, onClick = {})
                        } else {
                            uiState.availableSlots.forEach { slot ->
                                DropdownMenuItem(
                                    text = { Text(slot.format(DateTimeFormatter.ofPattern("HH:mm"))) },
                                    onClick = {
                                        selectedSlot = slot
                                        isSlotDropdownExpanded = false
                                    }

                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note for professor (optional)") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = AccentColor,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedLabelColor = AccentColor,
                        cursorColor = TempleRed
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(colors = ButtonDefaults.buttonColors(containerColor = NewButtonColor),
                onClick = {
                    selectedSlot?.let { onConfirmBooking(it, note) }
                },
                enabled = selectedSlot != null
            ) { Text("Book Now") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = NewButtonColor)) { Text("Cancel") }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateInMillis ->
                        val selectedLocalDate = Instant.ofEpochMilli(dateInMillis)
                            .atZone(ZoneId.systemDefault()).toLocalDate()

                        // MODIFICATION: Correctly map 3-letter day strings to DayOfWeek enums.
                        val validDaysOfWeek = uiState.officeHours?.daysOfWeek?.mapNotNull { dayString ->
                            when (dayString.uppercase(Locale.ROOT)) {
                                "MON" -> DayOfWeek.MONDAY
                                "TUE" -> DayOfWeek.TUESDAY
                                "WED" -> DayOfWeek.WEDNESDAY
                                "THU" -> DayOfWeek.THURSDAY
                                "FRI" -> DayOfWeek.FRIDAY
                                "SAT" -> DayOfWeek.SATURDAY
                                "SUN" -> DayOfWeek.SUNDAY
                                else -> null
                            }
                        } ?: emptyList()

                        val today = LocalDate.now()

                        if (selectedLocalDate.dayOfWeek in validDaysOfWeek && !selectedLocalDate.isBefore(today)) {
                            onDateSelected(selectedLocalDate)
                            showDatePicker = false
                        } else {
                            Toast.makeText(context, "Professor is not available on this day.", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Please select a date.", Toast.LENGTH_SHORT).show()
                    }
                }, colors = ButtonDefaults.textButtonColors(contentColor = NewButtonColor)) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }, colors = ButtonDefaults.textButtonColors(contentColor = NewButtonColor)) { Text("Cancel") }
            },
            colors = DatePickerDefaults.colors(containerColor = NeutralColor)
        ) {
            DatePicker(state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = NeutralColor,
                    selectedDayContainerColor = TempleRed,
                    selectedDayContentColor = Color.White,
                    todayContentColor = TempleRed,
                    todayDateBorderColor = TempleRed,
                    selectedYearContainerColor = TempleRed,
                    selectedYearContentColor = Color.White,
                    dateTextFieldColors = TextFieldDefaults.textFieldColors(focusedLabelColor = AccentColor, containerColor = NeutralColor, cursorColor = TempleRed, focusedIndicatorColor = AccentColor)
                ))
        }
    }
}