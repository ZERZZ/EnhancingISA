package com.example.enhancingisa.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PhishingQuiz(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // Back button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick test button
            Button(
                onClick = { navController.navigate("quickTest") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.FlashOn,
                        contentDescription = "Quick Test",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Quick Test", fontSize = 16.sp, color = Color.White)
                }
            }

            // Adaptive test not completed so button takes u to dash
            Button(
                onClick = { navController.navigate("dashboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.AutoGraph,
                        contentDescription = "Adaptive Test",
                        tint = Color.White,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Adaptive Test", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
