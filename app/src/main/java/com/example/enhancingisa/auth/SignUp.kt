package com.example.enhancingisa.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignupScreen(navController: NavController) {
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
                text = "Sign Up",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Private beta testing tag
            Text(
                text = "This app is currently in private beta testing.\n\n" +
                        "Regarding questions, contact:\nB603275@my.leedscitycollege.ac.uk",
                fontSize = 16.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Back to login button
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)), // Purple
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Back to login screen", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}