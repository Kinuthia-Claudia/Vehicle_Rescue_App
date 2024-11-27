package com.example.rshmad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rshmad.ui.screens.*
import com.example.rshmad.viewmodel.AuthViewModel
import com.example.rshmad.ui.theme.RSHMADTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            RSHMADTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                // Check if user is logged in
                if (currentUser != null) {
                    // User is logged in, fetch their role from Firebase Database
                    val userId = currentUser.uid
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                        val userRole = snapshot.child("role").value.toString()

                        // Navigate based on role
                        if (userRole == "customer") {
                            navController.navigate("customerHomeScreen")
                        } else if (userRole == "driver") {
                            navController.navigate("driverHomeScreen")
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "userTypeSelection") {
                    composable("userTypeSelection") { UserTypeSelectionScreen(navController) }
                    composable("login/{userType}") { backStackEntry ->
                        val userType = backStackEntry.arguments?.getString("userType")
                        LoginScreen(navController, authViewModel, userType.orEmpty())
                    }
                    composable("register/{userType}") { backStackEntry ->
                        val userType = backStackEntry.arguments?.getString("userType")
                        RegisterScreen(navController, authViewModel, userType.orEmpty())
                    }
                    composable("customerHomeScreen") {
                        CustomerHomeScreen(navController) // Pass navController to the CustomerHomeScreen
                    }
                    composable("driverHomeScreen") {
                        DriverHomeScreen(navController) // Pass navController to the DriverHomeScreen
                    }
                }
            }
        }
    }
}
