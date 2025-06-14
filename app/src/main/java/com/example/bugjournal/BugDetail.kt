package com.example.bugjournal.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import dev.jeziellago.compose.markdowntext.MarkdownText // 👈 Import this

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

    var suggestion by remember { mutableStateOf<String?>(null) }
    var isGeminiLoading by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
                    suggestion = doc?.getString("geminiSuggestion")
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

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .padding(16.dp)
                        ) {
                            item {
                                Text("Gemini Suggestion", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            item {
                                if (isGeminiLoading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                } else {
                                    MarkdownText( // 👈 Markdown rendering here
                                        markdown = suggestion ?: "No suggestion available.",
                                        modifier = Modifier.fillMaxWidth(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.DarkGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            item {
                                Button(
                                    onClick = {
                                        user?.uid?.let { uid ->
                                            bug?.let { bugData ->
                                                db.collection("users")
                                                    .document(uid)
                                                    .collection("bugs")
                                                    .document(bugData.id)
                                                    .update("geminiSuggestion", suggestion)
                                                    .addOnSuccessListener {
                                                        showBottomSheet = false
                                                        Toast.makeText(context, "Saved to Firestore", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = suggestion != null
                                ) {
                                    Text("Save Suggestion", color = Color.White)
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item { DetailItem("Title", it.title) }
                        item { DetailItem("App", it.appname) }
                        item { DetailItem("Severity", it.severity) }
                        item { DetailItem("Environment", it.environment) }
                        item { DetailItem("Tags", it.tags.joinToString(", ")) }
                        item { DetailItem("Steps", it.steps) }
                        item { DetailItem("Resolution", it.resolution) }
                        item {
                            DetailItem(
                                "Description",
                                if (it.description.length > 200)
                                    it.description.take(200) + "..."
                                else
                                    it.description
                            )
                        }
                        if (suggestion != null) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("AI Suggestion", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                                MarkdownText(
                                    markdown = suggestion ?: "",
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                            }
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }

                    if (suggestion == null) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    isGeminiLoading = true
                                    suggestion = getGeminiSuggestion(it)
                                    isGeminiLoading = false
                                    sheetState.show()
                                    showBottomSheet = true
                                }
                            },
                            containerColor = Color.Black,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            if (isGeminiLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text("AI", color = Color.White)
                            }
                        }
                    }

                    FloatingActionButton(
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
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener {
                                        isDeleting = false
                                    }
                            }
                        },
                        containerColor = Color.Red,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
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

suspend fun getGeminiSuggestion(bug: Bug): String = withContext(Dispatchers.IO) {
    return@withContext try {
        val model = GenerativeModel(
            modelName = "gemini-2.0-flash-001",
            apiKey = "AIzaSyBwNywxcIhAk8t-xC_9yDLRCBuA9ZOPm88"
        )
        val prompt = """
            Bug Report:
            Title: ${bug.title}
            App: ${bug.appname}
            Severity: ${bug.severity}
            Environment: ${bug.environment}
            Steps: ${bug.steps}
            Description: ${bug.description}
            Tags: ${bug.tags.joinToString(", ")}
            Resolution: ${bug.resolution}

            Please:
            - Summarize the bug.
            - Provide a concise description of the issue what would have happed.
            - Suggest potential fixes or improvements in the bug.
            - Include any relevant debugging steps or context to fix the reported bug.
            - Suggest any improvements in the way of noting the bug.
            - Mention missing debugging steps or context that can help fix it.
        """.trimIndent()

        val response = model.generateContent(prompt)
        response.text ?: "Empty response from Gemini."
    } catch (e: Exception) {
        "Gemini error: ${e.localizedMessage}"
    }
}
