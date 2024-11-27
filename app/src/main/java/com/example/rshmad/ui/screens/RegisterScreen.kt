package com.example.rshmad.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rshmad.viewmodel.AuthState
import com.example.rshmad.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    userType: String
) {

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val age = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }


    val authState = viewModel.authState.collectAsState().value


    val auth = FirebaseAuth.getInstance()


    fun registerUser() {

        auth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {

                        val user = mapOf(
                            "fullName" to fullName.value,
                            "email" to email.value,
                            "age" to age.value,
                            "phone" to phone.value,
                            "role" to userType
                        )
                        FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(userId)
                            .setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {

                                    if (userType == "customer") {
                                        navController.navigate("customerHomeScreen")
                                    } else if (userType == "driver") {
                                        navController.navigate("driverHomeScreen")
                                    }
                                } else {

                                    Toast.makeText(
                                        navController.context,
                                        "Error saving user data",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {

                    Toast.makeText(
                        navController.context,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            value = fullName.value,
            onValueChange = { fullName.value = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )


        TextField(
            value = age.value,
            onValueChange = { age.value = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )


        TextField(
            value = phone.value,
            onValueChange = { phone.value = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )


        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )


        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )


        Button(onClick = {
            if (fullName.value.isNotEmpty() && age.value.isNotEmpty() && phone.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty()) {
                registerUser() // Call the register function
            } else {
                Toast.makeText(navController.context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Register as $userType")
        }


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
