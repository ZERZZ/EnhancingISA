package com.example.enhancingisa.auth

import android.content.Context
import java.io.File
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: (String) -> Unit) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Read stored credentials
    LaunchedEffect(Unit) {
        val savedUsername = readStoredUsername(context)
        if (!savedUsername.isNullOrEmpty()) {
            onLoginSuccess(savedUsername)
        }
    }

    // Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Login",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFBB86FC),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Password Visibility",
                            tint = Color.White
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFFBB86FC),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message (if login fails)
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Login button
            Button(
                onClick = {
                    if (validateLogin(context, username, password)) {
                        errorMessage = ""

                        saveStoredUsername(context, username)

                        onLoginSuccess(username)
                    } else {
                        errorMessage = "Incorrect credentials"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Login", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Signup Button
            TextButton(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    fontSize = 16.sp,
                    color = Color(0xFFBB86FC)
                )
            }
        }
    }
}

// Function validate login against hardcoded credentials
fun validateLogin(context: Context, username: String, password: String): Boolean {
    val credentials = mapOf(
        "admin" to "password123",
        "user1" to "password123",
        "user2" to "password123",
        "user3" to "password123"
    )
    return credentials[username] == password
}


// Function to store logged-in username
fun saveStoredUsername(context: Context, username: String) {
    val file = File(context.filesDir, "login_data.txt")
    file.writeText(username)
}

// Function to retrieve stored username
fun readStoredUsername(context: Context): String? {
    val file = File(context.filesDir, "login_data.txt")
    return if (file.exists()) file.readText().trim() else null
}
