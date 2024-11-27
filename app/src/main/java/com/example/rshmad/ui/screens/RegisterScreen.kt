package com.example.rshmad.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController
import com.example.rshmad.viewmodel.AuthState
import com.example.rshmad.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RegisterScreen(
    navController: NavHostController, // Navigation controller, if required
    viewModel: AuthViewModel,
    userType: String // User role passed from the navigation argument
) {
    // State for email and password inputs
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Collect the authentication state as a state object
    val authState = viewModel.authState.collectAsState().value

    // FirebaseAuth instance for registration
    val auth = FirebaseAuth.getInstance()

    // Register logic
    fun registerUser() {
        // Perform Firebase authentication and store role
        auth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Save role information to Firebase database
                        val user = mapOf("role" to userType)
                        FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(userId)
                            .setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    // Navigate based on user role
                                    if (userType == "customer") {
                                        navController.navigate("customerHomeScreen")
                                    } else if (userType == "driver") {
                                        navController.navigate("driverHomeScreen")
                                    }
                                } else {
                                    // Handle database saving error
                                    Toast.makeText(
                                        navController.context,
                                        "Error saving user role",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    // Handle registration error
                    Toast.makeText(
                        navController.context,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email input field
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        // Password input field
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )

        // Register button
        Button(onClick = {
            registerUser() // Call the register function
        }) {
            Text(text = "Register as $userType")
        }

        // UI state based on authentication state
        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text("Error: ${(authState as AuthState.Error).message}")
            is AuthState.Success -> {
                Text("Registration Successful")
            }
            else -> {}
        }
    }
}