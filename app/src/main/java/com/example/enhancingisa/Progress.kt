package com.example.enhancingisa

import android.content.Context
import java.io.File
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Progress(navController: NavController, loggedInUsername: String) {
    val context = LocalContext.current

    val quizResults = getQuizResultsForUser(context, loggedInUsername)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.navigate("dashboard") },
                    modifier = Modifier
                        .size(90.dp)
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Title
            Text(
                "User Progress",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // No results message
            if (quizResults.isEmpty()) {
                Text(
                    "No quiz history available.",
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            } else {
                // Newest results at top
                quizResults.asReversed().forEach { result ->
                    val parts = result.split("|").map { it.trim() }
                    if (parts.size >= 3) {
                        val quizType   = parts[0]
                        val scoreText  = parts[1].removePrefix("Score:").substringBefore("/").trim()
                        val score      = scoreText.toIntOrNull() ?: 0
                        val timestamp  = parts[2]

                        val scoreColor = when (score) {
                            in 0..3  -> Color.Red
                            in 4..6  -> Color(0xFFFFA500)
                            else     -> Color.Green
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                            shape  = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Quiz: $quizType", color = Color.White, fontSize = 18.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Score: $score/10", color = scoreColor, fontSize = 16.sp)
                                Spacer(Modifier.height(4.dp))
                                Text("Date: $timestamp", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getQuizResultsForUser(context: Context, username: String): List<String> {
    val validUsername = username.ifBlank { "unknown_user" }
    val fileName = "quiz_results_${validUsername}.txt"
    val file = File(context.filesDir, fileName)

    if (!file.exists()) return emptyList()
    return file.readLines().filter { it.isNotBlank() }
}
