//package com.example.tujofficehoursapp
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.tujofficehoursapp.ui.theme.TujFont
//import com.example.tujofficehoursapp.ui.theme.TempleRed
//
//@Composable
//fun LoginScreen(
//    onLoginClick: (String, String) -> Unit,
//    onNavigateToSignUp: () -> Unit
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.temple_logo),
//            contentDescription = "Temple Logo",
//            modifier = Modifier.width(150.dp).padding(bottom = 24.dp)
//        )
//        Text(
//            text = "TUJ Office Hours\n\nLogin",
//            fontSize = 34.sp,
//            fontWeight = FontWeight.Bold,
//            fontFamily = TujFont,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(32.dp))
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = TempleRed,
//                focusedLabelColor = TempleRed,
//                cursorColor = TempleRed
//            )
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = TempleRed,
//                focusedLabelColor = TempleRed,
//                cursorColor = TempleRed
//            )
//        )
//        Spacer(modifier = Modifier.height(24.dp))
//        Button(
//            onClick = { onLoginClick(email, password) },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(containerColor = TempleRed)
//        ) {
//            Text("Login", modifier = Modifier.padding(8.dp))
//        }
//        Spacer(modifier = Modifier.height(24.dp))
//        Text(
//            text = "Don't have an account? Sign Up here",
//            color = TempleRed,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.clickable { onNavigateToSignUp() }
//        )
//    }
//}
//
//@Composable
//fun SignUpScreen(
//    onSignUpClick: (String, String, String, String) -> Unit,
//    onNavigateToLogin: () -> Unit
//) {
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var selectedRole by remember { mutableStateOf("Student") }
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.temple_logo),
//            contentDescription = "Temple Logo",
//            modifier = Modifier.width(150.dp).padding(bottom = 24.dp)
//        )
//        Text(
//            text = "TUJ Office Hours\n\nSign Up",
//            fontSize = 34.sp,
//            fontWeight = FontWeight.Bold,
//            fontFamily = TujFont,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            RadioButton(
//                selected = selectedRole == "Student",
//                onClick = { selectedRole = "Student" },
//                colors = RadioButtonDefaults.colors(selectedColor = TempleRed)
//            )
//            Text("Student", modifier = Modifier.padding(start = 4.dp, end = 16.dp))
//            RadioButton(
//                selected = selectedRole == "Professor",
//                onClick = { selectedRole = "Professor" },
//                colors = RadioButtonDefaults.colors(selectedColor = TempleRed)
//            )
//            Text("Professor", modifier = Modifier.padding(start = 4.dp))
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = { Text("Full Name") },
//            modifier = Modifier.fillMaxWidth(),
//            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TempleRed, focusedLabelColor = TempleRed, cursorColor = TempleRed)
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TempleRed, focusedLabelColor = TempleRed, cursorColor = TempleRed)
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TempleRed, focusedLabelColor = TempleRed, cursorColor = TempleRed)
//        )
//        Spacer(modifier = Modifier.height(24.dp))
//        Button(
//            onClick = { onSignUpClick(name, email, password, selectedRole) },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(containerColor = TempleRed)
//        ) {
//            Text("Sign Up", modifier = Modifier.padding(8.dp))
//        }
//        Spacer(modifier = Modifier.height(24.dp))
//        Text(
//            text = "Already have an account? Login here",
//            color = TempleRed,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.clickable { onNavigateToLogin() }
//        )
//    }
//}

package com.example.tujofficehoursapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tujofficehoursapp.ui.theme.TempleRed
import com.example.tujofficehoursapp.ui.theme.TujFont

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    error: String?
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.temple_logo),
            contentDescription = "Temple Logo",
            modifier = Modifier
                .width(150.dp)
                .padding(bottom = 24.dp)
        )
        Text(
            text = "TUJ Office Hours\n\nLogin",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = TujFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TempleRed,
                focusedLabelColor = TempleRed,
                cursorColor = TempleRed
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TempleRed,
                focusedLabelColor = TempleRed,
                cursorColor = TempleRed
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = TempleRed)
        ) {
            Text("Login", modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Don't have an account? Sign Up here",
            color = TempleRed,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigateToSignUp() }
        )
    }
}

@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Student") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.temple_logo),
            contentDescription = "Temple Logo",
            modifier = Modifier
                .width(150.dp)
                .padding(bottom = 24.dp)
        )
        Text(
            text = "TUJ Office Hours\n\nSign Up",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = TujFont,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedRole == "Student",
                onClick = { selectedRole = "Student" },
                colors = RadioButtonDefaults.colors(selectedColor = TempleRed)
            )
            Text("Student", modifier = Modifier.padding(start = 4.dp, end = 16.dp))
            RadioButton(
                selected = selectedRole == "Professor",
                onClick = { selectedRole = "Professor" },
                colors = RadioButtonDefaults.colors(selectedColor = TempleRed)
            )
            Text("Professor", modifier = Modifier.padding(start = 4.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TempleRed, focusedLabelColor = TempleRed, cursorColor = TempleRed)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TempleRed, focusedLabelColor = TempleRed, cursorColor = TempleRed)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TempleRed, focusedLabelColor = TempleRed, cursorColor = TempleRed)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSignUpClick(name, email, password, selectedRole) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = TempleRed)
        ) {
            Text("Sign Up", modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Already have an account? Login here",
            color = TempleRed,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}