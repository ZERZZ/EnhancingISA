package com.example.enhancingisa.quiz

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class QuizQuestion(
    val question: String,
    val answers: List<String>,
    val correctAnswer: String
)

@Composable
fun QuickTest(navController: NavController, loggedInUsername: String) {
    val context = LocalContext.current
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // array of questions n answers
    val allQuestions = listOf(
        QuizQuestion(
            "What should you do if you suspect an email is phishing?",
            listOf(
                "Click the link",
                "Reply immediately",
                "Report it to IT/security",
                "Forward it to a colleague for review"
            ),
            "Report it to IT/security"
        ),
        QuizQuestion(
            "What is vishing?",
            listOf(
                "An advanced visual attack",
                "A phishing attack done over the phone",
                "An email scam targeting VIPs",
                "A security measure used by banks"
            ),
            "A phishing attack done over the phone"
        ),
        QuizQuestion(
            "Which is the safest way to verify a link before clicking?",
            listOf(
                "Open it in an incognito window",
                "Hover over it to preview the real URL",
                "Click it and check for security warnings",
                "If the email looks official, it's safe"
            ),
            "Hover over it to preview the real URL"
        ),
        QuizQuestion(
            "What is a fake login page commonly used for?",
            listOf(
                "To test website loading speeds",
                "To steal credentials",
                "To provide alternative login options",
                "To protect against hacking attempts"
            ),
            "To steal credentials"
        ),
        QuizQuestion(
            "Which domain is likely a phishing website?",
            listOf(
                "secure-paypal-login.com",
                "paypal.com",
                "support.microsoft.com",
                "amazon.co.uk"
            ),
            "secure-paypal-login.com"
        ),
        QuizQuestion(
            "A phishing website asks you to enter your details, but everything looks normal. What should you check first?",
            listOf(
                "The overall website design",
                "The actual URL in the browser’s address bar",
                "Whether the page loads quickly",
                "The logo and branding"
            ),
            "The actual URL in the browser’s address bar"
        ),
        QuizQuestion(
            "How do attackers typically gather personal information for phishing attacks?",
            listOf(
                "Social engineering tactics",
                "AI scanning personal documents",
                "Buying it from Google legally",
                "Breaking into secured databases"
            ),
            "Social engineering tactics"
        ),
        QuizQuestion(
            "What should you do with suspicious attachments in an email?",
            listOf(
                "Open them in a virtual machine",
                "Report and delete them immediately",
                "Download but don’t open them",
                "Rename them and scan later"
            ),
            "Report and delete them immediately"
        ),
        QuizQuestion(
            "How can you tell if a website is secure?",
            listOf(
                "HTTPS & padlock in the URL bar",
                "A professional-looking website",
                "A verified checkmark next to the domain",
                "If the page loads without an error"
            ),
            "HTTPS & padlock in the URL bar"
        ),
        QuizQuestion(
            "You receive an email from your 'bank' asking to confirm your details. What do you do?",
            listOf(
                "Contact your bank directly through their official website",
                "Reply to the email and ask for proof",
                "Click the link and log in to check",
                "Ignore it, but keep the email in case"
            ),
            "Contact your bank directly through their official website"
        )
    )

    // takes 10 of them and shuffles them randomly
    val quizQuestions = remember { allQuestions.shuffled().take(10) }

    // for feedback at end of quiz
    val feedbackMessage = when (score) {
        10 -> "Perfect score! Excellent job!"
        in 6..9 -> "Great work! You're doing well."
        in 3..5 -> "Good effort! Keep improving."
        else -> "Needs improvement. Stay cautious!"
    }

    if (isFinished) {
        LaunchedEffect(Unit) {
            saveQuizResult(context, loggedInUsername, "Quick Test", score) //saves to be displayed in user progress
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { //results screen
                Text("Quiz Complete!", color = Color.White, fontSize = 24.sp)
                Text("Your Score: $score / 10", color = Color.White, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(feedbackMessage, color = Color.Cyan, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("phishingQuiz") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Return to Quiz Menu", color = Color.White)
                }
            }
        }
    } else {
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { navController.popBackStack() },  //back button
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
                }

                Text("Question ${currentQuestionIndex + 1}/10", color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(quizQuestions[currentQuestionIndex].question, color = Color.White, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(16.dp))

                quizQuestions[currentQuestionIndex].answers.forEach { answer ->
                    Button(
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = answer
                                if (answer == quizQuestions[currentQuestionIndex].correctAnswer) {
                                    score++
                                }
                                if (currentQuestionIndex < quizQuestions.size - 1) {
                                    currentQuestionIndex++
                                    selectedAnswer = null
                                } else {
                                    isFinished = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3A3A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = answer, fontSize = 16.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// function to save results
fun saveQuizResult(context: Context, username: String, quizType: String, score: Int) {
    val validUsername = username.ifBlank { "unknown_user" }
    val fileName = "quiz_results_${validUsername}.txt"
    val file = File(context.filesDir, fileName)
    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    val result = "$quizType | Score: $score/10 | $timestamp\n"
    try {
        file.appendText(result)
    } catch (_: Exception) {}
}
