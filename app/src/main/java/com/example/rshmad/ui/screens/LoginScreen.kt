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
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    userType: String
) {

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }


    val authState = viewModel.authState.collectAsState().value


    val auth = FirebaseAuth.getInstance()


    fun loginUser() {
        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val userId = auth.currentUser?.uid
                    if (userId != null) {

                        FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(userId)
                            .get()
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    val userRole = dbTask.result?.child("role")?.value as? String
                                    if (userRole != null) {

                                        when (userRole) {
                                            "customer" -> navController.navigate("customerHome") {
                                                popUpTo("userTypeSelection") { inclusive = true }
                                            }
                                            "driver" -> navController.navigate("driverHome") {
                                                popUpTo("userTypeSelection") { inclusive = true }
                                            }
                                        }
                                    } else {

                                        Toast.makeText(
                                            navController.context,
                                            "Error: User role not found.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {

                                    Toast.makeText(
                                        navController.context,
                                        "Error fetching user data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {

                    Toast.makeText(
                        navController.context,
                        "Login failed: ${task.exception?.message}",
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

        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )


        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )


        Button(onClick = {
            loginUser()
        }) {
            Text(text = "Login as $userType")
        }


        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text("Error: ${(authState as AuthState.Error).message}")
            is AuthState.Success -> {

            }
            else -> {}
        }
    }
}
