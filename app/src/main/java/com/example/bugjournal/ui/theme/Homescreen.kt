package com.example.bugjournal.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

data class Bug(
    val title: String = "",
    val severity: String = "",
    val tags: List<String> = emptyList(),
    val description: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var bugs by remember { mutableStateOf<List<Bug>>(emptyList()) }
    var selectedSeverity by remember { mutableStateOf("All") }
    var filterTag by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid).collection("bugs")
                .addSnapshotListener { snapshot, e ->
                    if (e == null && snapshot != null) {
                        bugs = snapshot.documents.mapNotNull { it.toObject(Bug::class.java) }
                    }
                }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.background(Color.White) 
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                        navController.navigate("auth") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    )
    {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bug Journal") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {

                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("addBug") },
                    containerColor = Color.Black,     // Black background
                    contentColor = Color.White        // White icon
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Bug"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                TextField(
                    value = filterTag,
                    onValueChange = { filterTag = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { showBottomSheet = true },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    label = { Text("Search by tag or title") },
                    modifier = Modifier
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
                Spacer(modifier = Modifier.height(12.dp))

                // Filter bugs based on severity and tag/title filter
                val filteredBugs = bugs.filter { bug ->
                    val severityMatches = selectedSeverity == "All" || bug.severity.equals(selectedSeverity, ignoreCase = true)
                    val tagMatches = filterTag.isBlank() || bug.tags.any { it.contains(filterTag, ignoreCase = true) }
                    val titleMatches = filterTag.isBlank() || bug.title.contains(filterTag, ignoreCase = true)
                    severityMatches && (tagMatches || titleMatches)
                }

                LazyColumn {
                    items(filteredBugs) { bug ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Title: ${bug.title}", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Severity: ${bug.severity}", fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Tags: ${bug.tags.joinToString(", ")}", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Description: ${bug.description}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                Column(Modifier.padding(16.dp)) {
                    Text("Filter by Severity", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownMenuBox(
                        options = listOf("All", "Low", "Medium", "High"),
                        selected = selectedSeverity,
                        onSelected = { selectedSeverity = it },
                        label = "Severity"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
