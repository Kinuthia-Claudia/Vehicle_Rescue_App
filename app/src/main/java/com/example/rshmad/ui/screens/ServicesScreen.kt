package com.example.rshmad.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rshmad.R

@Composable
fun ServicesScreen(
    navController: NavHostController
) {

    var selectedService by remember { mutableStateOf<String?>(null) }


    var showDialog by remember { mutableStateOf(false) }


    val services = listOf(
        ServiceItem("Tow Truck", R.drawable.baseline_tow_24),
        ServiceItem("Oil Change", R.drawable.baseline_car_crash_24),
        ServiceItem("Mechanic Dispatch", R.drawable.baseline_build_24),
        ServiceItem("Tire Replacement", R.drawable.baseline_tool_24),
        ServiceItem("Battery Replacement", R.drawable.baseline_electric_24),
        ServiceItem("Car Wash", R.drawable.baseline_hand_24)
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Select a Service", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                ServiceCard(service = service, onServiceSelected = {
                    selectedService = service.name
                    showDialog = true // Show the confirmation dialog
                })
            }
        }
    }

    if (showDialog) {
        ServiceConfirmationDialog(
            service = selectedService ?: "Unknown Service",
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                Toast.makeText(navController.context, "Someone is on their way!", Toast.LENGTH_SHORT).show()


                navController.popBackStack("customerHomeScreen", inclusive = true)
            }
        )
    }
}

@Composable
fun ServiceCard(service: ServiceItem, onServiceSelected: (String) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(8.dp),
        onClick = { onServiceSelected(service.name) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = service.iconRes),
                contentDescription = service.name,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = service.name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ServiceConfirmationDialog(
    service: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Service Confirmation")
        },
        text = {
            Text(text = "You selected $service. Someone is on their way!")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class ServiceItem(val name: String, val iconRes: Int)

@Preview(showBackground = true)
@Composable
fun ServicesScreenPreview() {
    ServicesScreen(navController = NavHostController(LocalContext.current))
}
