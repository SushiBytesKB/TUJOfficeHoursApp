// MyOfficeHours.kt
package com.example.tujofficehoursapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MyOfficeHoursViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid ?: ""

    private val _officeHours = MutableStateFlow<ProfessorOfficeHours?>(null)
    val officeHours = _officeHours.asStateFlow()
    private val officeHoursDocRef = db.collection("professors").document(userId).collection("config").document("officeHours")

    init {
        viewModelScope.launch {
            officeHoursDocRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        _officeHours.value = document.toObject<ProfessorOfficeHours>()
                    }
                }
        }
    }

    // MODIFICATION: This function now works with LocalTime from the UI
    // and saves times as "HH:mm" strings.
    fun saveOfficeHours(
        days: Set<String>,
        startTime: LocalTime,
        endTime: LocalTime,
        duration: Int,
        location: String,
        onComplete: () -> Unit
    ) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")

        val newOfficeHours = ProfessorOfficeHours(
            daysOfWeek = days.toList().sorted(),
            startTime = startTime.format(formatter), // Format to string
            endTime = endTime.format(formatter),   // Format to string
            slotDurationMinutes = duration,
            professorId = userId,
            location = location
        )

        officeHoursDocRef.set(newOfficeHours)
            .addOnSuccessListener { onComplete() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOfficeHoursScreen(
    onNavigateToReservations: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyOfficeHoursViewModel = viewModel()
) {
    val officeHours by viewModel.officeHours.collectAsState()
    val context = LocalContext.current

    var selectedDays by remember { mutableStateOf<Set<String>>(emptySet()) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    var slotDuration by remember { mutableStateOf("10") }
    var location by remember { mutableStateOf("") }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // MODIFICATION: This now correctly parses the time string from the data model.
    LaunchedEffect(officeHours) {
        officeHours?.let {
            selectedDays = it.daysOfWeek.toSet()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            startTime = LocalTime.parse(it.startTime, formatter)
            endTime = LocalTime.parse(it.endTime, formatter)
            slotDuration = it.slotDurationMinutes.toString()
            location = it.location
        }
    }

    Scaffold(
        bottomBar = {
            ProfessorBottomNavBar(
                currentRoute = "professor_office_hours",
                onNavigateToReservations = onNavigateToReservations,
                onNavigateToOfficeHours = { /* Already here */ },
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { paddingValues ->

        Box(modifier = modifier.fillMaxSize())
        {
            Image(
                painter = painterResource(id = R.drawable.backgroundnew), // <-- Change this to your file name
                contentDescription = null, // for decorative images
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set My Office Hours",
                    style = Typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = TempleRed,
                    fontFamily = TujFont,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                SectionTitle("Available Days")
                DaySelector(selectedDays = selectedDays, onDaySelected = { day ->
                    selectedDays = if (selectedDays.contains(day)) {
                        selectedDays - day
                    } else {
                        selectedDays + day
                    }
                })

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle("Available Time Range")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimePickerField(
                        label = "Start Time",
                        time = startTime,
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                    TimePickerField(
                        label = "End Time",
                        time = endTime,
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle("Appointment Details")
                OutlinedTextField(
                    value = slotDuration,
                    onValueChange = { slotDuration = it.filter { char -> char.isDigit() } },
                    label = { Text("Slot Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = AccentColor,
                        focusedLabelColor = AccentColor,
                        unfocusedLabelColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = TempleRed)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (e.g., Office 503)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = AccentColor,
                        focusedLabelColor = AccentColor,
                        unfocusedLabelColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = TempleRed)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (startTime != null && endTime != null && selectedDays.isNotEmpty()) {
                            viewModel.saveOfficeHours(
                                days = selectedDays,
                                startTime = startTime!!,
                                endTime = endTime!!,
                                duration = slotDuration.toIntOrNull() ?: 10,
                                location = location
                            ) {
                                Toast.makeText(context, "Office Hours Updated!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NewButtonColor)
                ) {
                    Text("Save Changes", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            onTimeSelected = { startTime = it }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            onTimeSelected = { endTime = it }
        )
    }
}

@Composable
private fun DaySelector(selectedDays: Set<String>, onDaySelected: (String) -> Unit) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        days.forEach { day ->
            FilterChip(
                selected = selectedDays.contains(day),
                onClick = { onDaySelected(day) },
                label = { Text(text = day, softWrap = false, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = NeutralColor,
                    selectedContainerColor = AccentColor,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun TimePickerField(
    label: String,
    time: LocalTime?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    OutlinedTextField(
        value = time?.format(formatter) ?: "",
        onValueChange = {},
        label = { Text(label)},
        readOnly = true,
        modifier = modifier.clickable(onClick = onClick),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = AccentColor,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            focusedLabelColor = AccentColor,
            unfocusedLabelColor = Color.Black)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(onDismissRequest: () -> Unit, onTimeSelected: (LocalTime) -> Unit) {
    val timePickerState = rememberTimePickerState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = NeutralColor
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                TimePicker(state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color.White,
                        selectorColor = TempleRed,
                        containerColor = Color.White,
                        periodSelectorSelectedContainerColor = TransparentAccentColor,
                        periodSelectorSelectedContentColor = TempleRed,
                        timeSelectorSelectedContainerColor = TransparentAccentColor,
                        timeSelectorSelectedContentColor = TempleRed,
                        timeSelectorUnselectedContainerColor = Color.White
                    ))
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TextButton(onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(contentColor = NewButtonColor, containerColor = Color.Transparent)) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(colors = ButtonDefaults.buttonColors(containerColor = NewButtonColor),
                        onClick = {
                        onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        onDismissRequest()
                    }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}
