package com.example.bugjournal.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugDetailScreen(
    bugTitle: String,
    navController: NavController
) {
    val user = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    var bug by remember { mutableStateOf<Bug?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(bugTitle) {
        user?.uid?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("bugs")
                .whereEqualTo("title", bugTitle)
                .limit(1)
                .get()
                .addOnSuccessListener { docs ->
                    val doc = docs.documents.firstOrNull()
                    bug = doc?.toObject(Bug::class.java)?.copy(id = doc.id)
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        bug?.let {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Bug Detail") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    DetailItem("Title", it.title)
                    DetailItem("App", it.appname)
                    DetailItem("Severity", it.severity)
                    DetailItem("Environment", it.environment)
                    DetailItem("Tags", it.tags.joinToString(", "))
                    DetailItem("Steps", it.steps)
                    DetailItem("Resolution", it.resolution)

                    // Limit description to 200 characters
                    DetailItem(
                        "Description",
                        if (it.description.length > 200)
                            it.description.take(200) + "..."
                        else
                            it.description
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 🔴 Delete Button
                    Button(
                        onClick = {
                            isDeleting = true
                            user?.uid?.let { uid ->
                                db.collection("users")
                                    .document(uid)
                                    .collection("bugs")
                                    .document(it.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        isDeleting = false
                                        navController.popBackStack() // Go back after delete
                                    }
                                    .addOnFailureListener {
                                        isDeleting = false
                                    }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Delete Bug", color = Color.White)
                        }
                    }
                }
            }
        } ?: run {
            Text("Bug not found", modifier = Modifier.padding(16.dp))
        }
    }
}


@Composable
fun DetailItem(label: String, value: String) {
    if (value.isNotBlank()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
        }
    }
}
