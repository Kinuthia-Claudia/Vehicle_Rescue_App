package com.example.rshmad.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.rshmad.model.ServiceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


@Composable
fun DriverHomeScreen(
    navController: NavHostController,
    auth: FirebaseAuth = FirebaseAuth.getInstance(),
    database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {

    var serviceRequests by remember { mutableStateOf<List<ServiceRequest>>(emptyList()) }
    var completedRequestsCount by remember { mutableStateOf(0) }
    var pendingRequestsCount by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<ServiceRequest?>(null) }


    LaunchedEffect(Unit) {
        val serviceRequestsRef = database.reference.child("serviceRequests")

        // Fetch pending service requests
        serviceRequestsRef.orderByChild("status").equalTo("Pending").get().addOnSuccessListener { snapshot ->
            serviceRequests = snapshot.children.map { child ->
                val serviceType = child.child("serviceType").value.toString()
                val customerName = child.child("customerName").value.toString()
                val customerPhone = child.child("customerPhone").value.toString()
                ServiceRequest(serviceType, customerName, customerPhone)
            }
            pendingRequestsCount = serviceRequests.size
        }


        serviceRequestsRef.orderByChild("status").equalTo("Completed").get().addOnSuccessListener { snapshot ->
            completedRequestsCount = snapshot.children.count()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Driver Dashboard",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatCard(
                title = "Pending Requests",
                count = pendingRequestsCount,
                icon = R.drawable.baseline_pending_24,
                backgroundColor = MaterialTheme.colorScheme.secondary
            )
            StatCard(
                title = "Completed Requests",
                count = completedRequestsCount,
                icon = R.drawable.baseline_check_circle_24,
                backgroundColor = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Pending Service Requests",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (pendingRequestsCount > 0) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(serviceRequests) { request ->
                    ServiceRequestRow(
                        serviceRequest = request,
                        onAccept = {
                            // Accept the request
                            selectedRequest = request
                            showDialog = true
                        },
                        onDecline = {
                            // Decline the request
                            updateServiceRequestStatus(request, "Declined", database)
                        }
                    )
                }
            }
        } else {

            Text(
                text = "No pending requests at the moment.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Previous Service Requests",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )


        Button(
            onClick = {

                navController.navigate("previousRequests")
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("View Previous Requests")
        }

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login/customer") { // Navigate to login screen
                    popUpTo("customerHomeScreen") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Logout")
        }
    }


    if (showDialog && selectedRequest != null) {
        ServiceRequestDialog(
            serviceRequest = selectedRequest!!,
            onDismiss = { showDialog = false },
            onConfirm = {
                // Update the status to accepted
                updateServiceRequestStatus(selectedRequest!!, "Accepted", database)
                showDialog = false
            }
        )
    }
}


fun updateServiceRequestStatus(request: ServiceRequest, status: String, database: FirebaseDatabase) {
    val serviceRequestRef = database.reference.child("serviceRequests").child(request.customerName)
    serviceRequestRef.child("status").setValue(status)
}


@Composable
fun StatCard(
    title: String,
    count: Int,
    icon: Int,
    backgroundColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

            .background(backgroundColor, MaterialTheme.shapes.medium)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$count", style = MaterialTheme.typography.headlineSmall, color = Color.White)
        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
}



@Composable
fun ServiceRequestRow(
    serviceRequest: ServiceRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Service: ${serviceRequest.serviceType}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Customer: ${serviceRequest.customerName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Phone: ${serviceRequest.customerPhone}", style = MaterialTheme.typography.bodyMedium)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Button(onClick = onAccept) {
                Text("Accept")
            }
            Button(onClick = onDecline) {
                Text("Decline")
            }
        }
    }
}


@Composable
fun ServiceRequestDialog(
    serviceRequest: ServiceRequest,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Confirm Service Request")
        },
        text = {
            Text(text = "You have selected to accept the ${serviceRequest.serviceType} request for ${serviceRequest.customerName}.")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DriverHomeScreenPreview() {
    DriverHomeScreen(navController = NavHostController(LocalContext.current))
}
