@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bugjournal.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddBugScreen(navController: NavController) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()

    var title by remember { mutableStateOf("") }
    var bug by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("Low") }
    var description by remember { mutableStateOf("") }
    var environment by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var resolution by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    val severityOptions = listOf("Low", "Medium", "High")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Bug") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
     TextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

       TextField(value = bug, onValueChange = { bug = it }, label = { Text("Bug") }, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    value = severity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Severity") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor().clip(RoundedCornerShape(12.dp)) // Rounded corners
                        .background(Color(0xFFF0F0F0)),
                            colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    severityOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                severity = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
           TextField(value = environment, onValueChange = { environment = it }, label = { Text("Environment") }, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
          TextField(value = steps, onValueChange = { steps = it }, label = { Text("Steps to Reproduce") }, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = resolution, onValueChange = { resolution = it }, label = { Text("Resolution") },modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma-separated)") }, modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Black,
                    errorBorderColor = Color.Red,
                    disabledBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    disabledTextColor = Color.DarkGray,
                    focusedLeadingIconColor = Color.Black,
                    unfocusedLeadingIconColor = Color.Gray,
                    errorLeadingIconColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (user != null) {
                        val bugData = hashMapOf(
                            "title" to title,
                            "bug" to bug,
                            "severity" to severity,
                            "description" to description,
                            "environment" to environment,
                            "steps" to steps,
                            "resolution" to resolution,
                            "tags" to tags.split(",").map { it.trim() },
                            "timestamp" to System.currentTimeMillis()
                        )

                        firestore.collection("users")
                            .document(user.uid)
                            .collection("bugs")
                            .add(bugData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Bug added", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to add bug", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Gray
                )
            ) {
                Text("Add Bug")
            }
        }
    }
}
