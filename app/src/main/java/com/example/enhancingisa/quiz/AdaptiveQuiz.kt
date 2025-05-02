package com.example.enhancingisa.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdaptiveQuiz(navController: NavController, loggedInUsername: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Back Button
        IconButton(
            onClick = { navController.navigate("phishingQuiz") },
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to Phishing Quiz",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        // Title
        Text(
            text = "Adaptive Quiz Screen",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}