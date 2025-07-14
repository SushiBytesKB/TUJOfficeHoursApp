package com.example.tujofficehoursapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.tujofficehoursapp.ui.theme.TUJOfficeHoursAppTheme
import com.google.firebase.auth.userProfileChangeRequest

class MainActivity : ComponentActivity() {

    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TUJOfficeHoursAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = Firebase.auth
    val db = Firebase.firestore
    var startDestination by remember { mutableStateOf<String?>(null) }
    var userRole by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = Unit) {
        if (auth.currentUser == null) {
            startDestination = "login"
        } else {
            db.collection("users").document(auth.currentUser!!.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    userRole = role
                    startDestination = if (role == "Student") "student_reservations" else "professor_reserved"
                }
                .addOnFailureListener { startDestination = "login" }
        }
    }

    if (startDestination != null) {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable("login") {
                LoginScreen(
                    onLoginClick = { email, password ->
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                db.collection("users").document(auth.currentUser!!.uid).get()
                                    .addOnSuccessListener { doc ->
                                        val role = doc.getString("role")
                                        userRole = role
                                        val route = if (role == "Student") "student_reservations" else "professor_reserved"
                                        navController.navigate(route) { popUpTo("login") { inclusive = true } }
                                    }
                            }
                            .addOnFailureListener { /* Add a login error message? */ }
                    },
                    onNavigateToSignUp = { navController.navigate("signup") }
                )
            }
            composable("signup") {
                SignUpScreen(
                    // MODIFICATION: The onSignUpClick lambda now accepts a 'name' parameter.
                    onSignUpClick = { name, email, password, role ->
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                val firebaseUser = result.user!!
                                // MODIFICATION: The User object now includes the name.
                                val user = User(uid = firebaseUser.uid, name = name, email = email, role = role)
                                db.collection("users").document(user.uid).set(user)

                                if (role == "Professor") {
                                    val professorProfile = Professor(uid = user.uid, name = name, email = email)
                                    db.collection("professors").document(user.uid).set(professorProfile)
                                }

                                // MODIFICATION: Set the user's display name in Firebase Authentication.
                                val profileUpdates = userProfileChangeRequest {
                                    displayName = name
                                }
                                firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(navController.context, "Sign Up Successful!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        }
                                    }
                            }
                            .addOnFailureListener { /* Handle sign-up failure */ }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable("student_reservations") {
                ReservationsScreen(
                    onNavigateToProfessors = { navController.navigate("student_professors") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("student_professors") {
                ProfessorsScreen(
                    onNavigateToReservations = { navController.navigate("student_reservations") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("professor_reserved") {
                ReservedScreen(
                    onNavigateToOfficeHours = { navController.navigate("professor_office_hours") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("professor_office_hours") {
                MyOfficeHoursScreen(
                    onNavigateToReservations = { navController.navigate("professor_reserved") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }

            composable("settings") {
                SettingsScreen(
                    userRole = userRole ?: "Student",
                    onNavigateToReservations = {
                        if (userRole == "Student") navController.navigate("student_reservations") else navController.navigate("professor_reserved")
                    },
                    onNavigateToProfessors = { navController.navigate("student_professors") },
                    onNavigateToOfficeHours = { navController.navigate("professor_office_hours") },
                    onLogout = {
                        auth.signOut()
                        userRole = null
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                )
            }
        }
    }
}