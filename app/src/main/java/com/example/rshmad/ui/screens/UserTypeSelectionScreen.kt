package com.example.rshmad.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun UserTypeSelectionScreen(navController: NavHostController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val userType = remember { mutableStateOf("unknown") }

    if (userId != null) {
        // Fetch user type from Firebase
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.get().addOnSuccessListener { snapshot ->
            val fetchedUserType = snapshot.child("userType").getValue(String::class.java) ?: "unknown"
            userType.value = fetchedUserType
            // Now you can use userType.value to determine navigation logic if needed
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("login/customer") }) {
            Text(text = "Customer Login")
        }
        Button(onClick = { navController.navigate("register/customer") }) {
            Text(text = "Customer Register")
        }
        Button(onClick = { navController.navigate("login/driver") }) {
            Text(text = "Driver Login")
        }
        Button(onClick = { navController.navigate("register/driver") }) {
            Text(text = "Driver Register")
        }

    }
}
