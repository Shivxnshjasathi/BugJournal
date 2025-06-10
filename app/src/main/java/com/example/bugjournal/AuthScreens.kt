package com.example.bugjournal
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.airbnb.lottie.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bugjournal.ui.theme.AppTypography
import com.example.bugjournal.ui.theme.Poppins
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun AuthScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }  // Track loading state
    val auth = Firebase.auth

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {




        Text(
            text = "Taking notes has never ",
            style = AppTypography.titleLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins
            ),
            modifier = Modifier.padding(bottom = 5.dp)
        )

        Text(
            text = "been easier!",
            style = AppTypography.titleLarge.copy(
                color = Color.Black,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )


        Text(
            text = if (isLogin) "Welcome Back!" else "Create an Account",
            style = AppTypography.bodyLarge.copy(
                color = Color.Black,
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        TextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),   // Light gray background

            shape = RoundedCornerShape(12.dp),  // Rounded border shape
            isError = email.isNotEmpty() && !isValidEmail(email),
            singleLine = true,
            enabled = !isLoading,
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

        TextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password Icon") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                .background(Color(0xFFF0F0F0)),
            isError = password.isNotEmpty() && !isValidPassword(password),
            singleLine = true,
            enabled = !isLoading,
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    email.isBlank() -> errorMessage = "Email cannot be empty"
                    !isValidEmail(email) -> errorMessage = "Please enter a valid email"
                    password.isBlank() -> errorMessage = "Password cannot be empty"
                    !isValidPassword(password) -> errorMessage = "Password must be at least 6 characters"
                    else -> {
                        errorMessage = ""
                        isLoading = true // Start loading
                        if (isLogin) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    isLoading = false
                                    navController.navigate("home")
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    errorMessage = it.message ?: "Login failed"
                                }
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    isLoading = false
                                    navController.navigate("home")
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    errorMessage = it.message ?: "Signup failed"
                                }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Gray
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = if (isLogin) "Login" else "Sign Up",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }


        TextButton(
            onClick = {
                isLogin = !isLogin
                errorMessage = ""
            },
            modifier = Modifier.padding(top = 12.dp),
            enabled = !isLoading
        ) {
            Text(
                text = if (isLogin)
                    "Don't have an account? Sign up"
                else
                    "Already have an account? Login",
                style = AppTypography.bodyLarge.copy(
                    color = Color.Black,
                ),
            )
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp),
                style = AppTypography.labelSmall
            )
        }
    }
}
