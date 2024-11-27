package com.example.rshmad.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun UserTypeSelectionScreen(navController: NavHostController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val userType = remember { mutableStateOf("unknown") }
    val isLoading = remember { mutableStateOf(true) }

    if (userId != null) {
        // Fetch user type from Firebase
        LaunchedEffect(userId) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            userRef.get().addOnSuccessListener { snapshot ->
                val fetchedUserType = snapshot.child("userType").getValue(String::class.java) ?: "unknown"
                userType.value = fetchedUserType
                isLoading.value = false
            }
        }
    } else {
        isLoading.value = false
    }


    if (isLoading.value) {
        CircularProgressIndicator()
    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to RoadSide Hero",
                modifier = Modifier.padding(bottom = 32.dp)
            )


            if (userType.value == "customer") {
                // Redirect to Customer Screens
                Button(
                    onClick = { navController.navigate("customerHomeScreen") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(text = "Customer Home")
                }
            } else if (userType.value == "driver") {

                Button(
                    onClick = { navController.navigate("driverHomeScreen") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(text = "Driver Home")
                }
            } else {

                Button(
                    onClick = { navController.navigate("login/customer") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(text = "Customer Login")
                }
                Button(
                    onClick = { navController.navigate("register/customer") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(text = "Customer Register")
                }
                Button(
                    onClick = { navController.navigate("login/driver") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(text = "Driver Login")
                }
                Button(
                    onClick = { navController.navigate("register/driver") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(text = "Driver Register")
                }
            }
        }
    }
}

