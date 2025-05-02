package com.example.enhancingisa.settings

import android.content.Context
import java.io.File
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettings(
    navController: NavController,
    username: String
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Privacy Settings",
                        color = Color.White,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.size(90.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { inner ->
        val itemColors = ListItemDefaults.colors(
            containerColor   = Color(0xFF1E1E1E),
            headlineColor    = Color.White,
            supportingColor  = Color(0xFFB0B0B0),
            leadingIconColor = Color(0xFFBB86FC)
        )

        LazyColumn(
            contentPadding     = inner,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier           = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // View Collected Data
            item {
                ListItem(
                    modifier         = Modifier.fillMaxWidth(),
                    colors           = itemColors,
                    leadingContent   = {
                        Icon(Icons.Filled.Visibility, contentDescription = "Collected data info")
                    },
                    headlineContent  = { Text("View Collected Data") },
                    supportingContent = {
                        Text(
                            "The app stores a small amount of local data to personalise your experience and track your training progress.\n\n" +
                                    "Your username and login status are saved on your device to remember you between sessions.\n\n" +
                                    "Quiz scores and brief feedback messages are saved privately so you can review your progress.\n\n" +
                                    "No personal or sensitive data is collected. All information remains on your device and can be deleted at any time using the \"Delete My Data\" option.",
                            fontSize = 12.sp,
                            color    = Color(0xFFB0B0B0)
                        )
                    }
                )
            }

            // Delete My Data
            item {
                ListItem(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteDialog = true },
                    colors           = itemColors,
                    leadingContent   = {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete data")
                    },
                    headlineContent  = { Text("Delete My Data") },
                    supportingContent = {
                        Text("Remove all your personal information", fontSize = 12.sp)
                    }
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }

        // Confirmation box
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title   = { Text("Delete All Data?") },
                text    = { Text("Are you sure you want to delete all your data?") },
                confirmButton = {
                    Button(
                        onClick = {
                            File(context.filesDir, "quiz_results_$username.txt").delete()
                            context.getSharedPreferences("settings_$username", Context.MODE_PRIVATE)
                                .edit().clear().apply()
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape  = RoundedCornerShape(8.dp)
                    ) {
                        Text("Yes", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape  = RoundedCornerShape(8.dp)
                    ) {
                        Text("No", color = Color.White)
                    }
                }
            )
        }
    }
}
