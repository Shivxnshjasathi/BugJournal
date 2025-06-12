package com.example.bugjournal.ui.theme

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// Bug model

data class Bug(
    val id: String = "",
    val title: String = "",
    val appname: String = "",
    val severity: String = "",
    val environment: String = "",
    val steps: String = "",
    val resolution: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val timestamp: Long = 0L
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName ?: "there"
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var bugs by remember { mutableStateOf<List<Bug>>(emptyList()) }
    var selectedSeverity by remember { mutableStateOf("All") }
    var filterTag by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("users").document(uid).collection("bugs")
                .addSnapshotListener { snapshot, e ->
                    isLoading = false
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
                    text = "Hello, $userName 👋",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
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
    ) {
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
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("addBug") },
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Bug")
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
                        Icon(Icons.Rounded.Search, contentDescription = "Search Icon")
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F0F0)),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                val filteredBugs = bugs.filter { bug ->
                    val severityMatches = selectedSeverity == "All" || bug.severity.equals(selectedSeverity, ignoreCase = true)
                    val tagMatches = filterTag.isBlank() || bug.tags.any { it.contains(filterTag, ignoreCase = true) }
                    val titleMatches = filterTag.isBlank() || bug.title.contains(filterTag, ignoreCase = true)
                    severityMatches && (tagMatches || titleMatches)
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 48.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                } else if (filteredBugs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 48.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "Hi $userName 👋\nLooks like you haven’t reported any bugs yet!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.DarkGray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        items(filteredBugs) { bug ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        if (bug.title.isNotBlank()) {
                                            val encodedTitle = Uri.encode(bug.title)
                                            navController.navigate("bugDetail/${Uri.encode(bug.title)}")

                                        } else {
                                            Log.e("BugNavigation", "Bug title is blank, cannot navigate.")
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = bug.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color.Black,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

//                                    if (bug.appname.isNotBlank()) {
//                                        Spacer(modifier = Modifier.height(4.dp))
//                                        Text(
//                                            "Project: ${bug.appname}",
//                                            style = MaterialTheme.typography.bodySmall,
//                                            color = Color.Gray
//                                        )
//                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
//                                    Spacer(modifier = Modifier.height(8.dp))
//                                    Text(
//                                        "Severity: ${bug.severity}",
//                                        style = MaterialTheme.typography.bodyMedium,
//                                        color = Color.DarkGray
//                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Description:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = if (bug.description.length > 100)
                                            bug.description.take(100) + "..."
                                        else
                                            bug.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )


                                }
                            }
                        }
                    }


                }
                }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = Color.White,
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
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

        if (isDeleting) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
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

    Text("Apply Filter!", style = MaterialTheme.typography.titleMedium, color = Color.Black)
    Spacer(modifier = Modifier.height(16.dp))

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F0F0)),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.DarkGray,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.Black) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

