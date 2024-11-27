package com.example.rshmad

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rshmad.ui.screens.*
import com.example.rshmad.viewmodel.AuthViewModel
import com.example.rshmad.ui.theme.RSHMADTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val scope = MainScope()

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        FirebaseApp.initializeApp(this)

        setContent {
            RSHMADTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                scope.launch {
                    delay(2000)
                    if (currentUser != null) {

                        val userId = currentUser.uid
                        val database = FirebaseDatabase.getInstance().reference
                        database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                            val userRole = snapshot.child("role").value.toString()
                            if (userRole == "customer") {
                                navController.navigate("customerHomeScreen") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            } else if (userRole == "driver") {
                                navController.navigate("driverHomeScreen") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                    } else {

                        navController.navigate("userTypeSelection") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }


                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen() }
                    composable("userTypeSelection") { UserTypeSelectionScreen(navController) }
                    composable("login/{userType}") { backStackEntry ->
                        val userType = backStackEntry.arguments?.getString("userType")
                        LoginScreen(navController, authViewModel, userType.orEmpty())
                    }
                    composable("register/{userType}") { backStackEntry ->
                        val userType = backStackEntry.arguments?.getString("userType")
                        RegisterScreen(navController, authViewModel, userType.orEmpty())
                    }
                    composable("customerHomeScreen") { CustomerHomeScreen(navController) }
                    composable("driverHomeScreen") { DriverHomeScreen(navController) }
                    composable("profileScreen") { ProfileScreen(navController) }
                    composable("servicesScreen") { ServicesScreen(navController) }

                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Splash Screen Logo",
            modifier = Modifier.size(150.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
