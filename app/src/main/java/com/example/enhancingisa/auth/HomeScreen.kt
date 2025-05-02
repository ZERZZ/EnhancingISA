package com.example.enhancingisa.auth

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavController
import java.io.File
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current

    // Track if user is remembered
    var isUserRemembered by remember { mutableStateOf(false) }

    // Check if user has saved credentials
    LaunchedEffect(Unit) {
        isUserRemembered = checkIfUserIsRemembered(context)
    }

    // Flicker effect for ENHANCING ISA title
    val flickerAlpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            flickerAlpha.animateTo(0.7f, animationSpec = tween(50)) // Dims
            flickerAlpha.animateTo(1f, animationSpec = tween(100)) // Brightens
            delay((500..1500).random().toLong()) //
        }
    }

    // Pulsing animation on get started button
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            scaleAnim.animateTo(1.05f, animationSpec = tween(700, easing = LinearEasing)) // Bigger
            scaleAnim.animateTo(1f, animationSpec = tween(700, easing = LinearEasing)) // Smaller
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ENHANCING ISA title
            Text(
                text = "ENHANCING ISA",
                fontSize = 30.sp,
                color = Color(0xFF00FFEA), // Neon light
                modifier = Modifier.graphicsLayer(alpha = flickerAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "AI-Driven Cybersecurity Training",
                fontSize = 16.sp,
                color = Color(0xFFBB86FC) // Futuristic Purple
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Button: If remember go dash if not login
            Button(
                onClick = {
                    if (isUserRemembered) {
                        navController.navigate("dashboard") //
                    } else {
                        navController.navigate("login") //
                    }
                },
                modifier = if (!isUserRemembered) Modifier.graphicsLayer(scaleX = scaleAnim.value, scaleY = scaleAnim.value) else Modifier, // only pulses for login page
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(6.dp)
            ) {
                Text(
                    text = if (isUserRemembered) "Continue to Dashboard" else "Get Started",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

// Function to check credentials
fun checkIfUserIsRemembered(context: Context): Boolean {
    val file = File(context.filesDir, "login_data.txt")
    return file.exists() //
}
