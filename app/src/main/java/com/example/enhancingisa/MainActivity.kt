package com.example.enhancingisa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.enhancingisa.auth.HomeScreen
import com.example.enhancingisa.auth.LoginScreen
import com.example.enhancingisa.auth.SignupScreen
import com.example.enhancingisa.quiz.AdaptiveQuiz
import com.example.enhancingisa.scenarios.PhishingEmail
import com.example.enhancingisa.scenarios.PhishingIntro
import com.example.enhancingisa.scenarios.PhishingText
import com.example.enhancingisa.quiz.QuickTest
import com.example.enhancingisa.settings.*
import com.example.enhancingisa.ui.theme.EnhancingIsaTheme
import com.example.enhancingisa.quiz.PhishingQuiz


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Dark mode, dyslexia font and font size state variables
            var darkMode by remember { mutableStateOf(false) }
            var fontSizeSp by remember { mutableStateOf(16f) }    // bassline = 16sp
            var dyslexiaMode by remember { mutableStateOf(false) }

            // Pass them to theme
            EnhancingIsaTheme(
                darkTheme = darkMode,
                fontScale = fontSizeSp / 16f,
                dyslexia  = dyslexiaMode
            ) {
                val navController = rememberNavController()
                var loggedInUsername by remember { mutableStateOf("") }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }

                    composable("login") {
                        LoginScreen(navController) { username ->
                            loggedInUsername = username
                            navController.navigate("dashboard")
                        }
                    }

                    composable("signup") { SignupScreen(navController) }
                    composable("phishingText") { PhishingText(navController, loggedInUsername) }
                    composable("dashboard") { DashboardScreen(navController, loggedInUsername) }
                    composable("phishingEmail") { PhishingEmail(navController) }
                    composable("phishingIntro") { PhishingIntro(navController) }
                    composable("phishingQuiz") { PhishingQuiz(navController) }
                    composable("progress") { Progress(navController, loggedInUsername) }
                    composable("settings") { Settings(navController) }

                    composable("general") {
                        GeneralSettings(
                            navController         = navController,
                            darkMode              = darkMode,
                            onDarkModeChanged     = { darkMode   = it },
                            fontSizeSp            = fontSizeSp,
                            onFontSizeChanged     = { fontSizeSp = it }
                        )
                    }

                    composable("accessibility") {
                        AccessibilitySettings(
                            navController        = navController,
                            fontSizeSp           = fontSizeSp,
                            onFontSizeChanged    = { fontSizeSp    = it },
                            dyslexiaMode         = dyslexiaMode,
                            onDyslexiaChanged    = { dyslexiaMode = it }
                        )
                    }

                    composable("privacy") { PrivacySettings(navController, loggedInUsername) }
                    composable("infoSupport") { InfoSupport(navController) }
                    composable("quickTest") { QuickTest(navController, loggedInUsername) }
                    composable("adaptiveQuiz") { AdaptiveQuiz(navController, loggedInUsername)
                    }
                }
            }
        }
    }
}
