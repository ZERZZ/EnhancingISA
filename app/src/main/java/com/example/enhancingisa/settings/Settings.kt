package com.example.enhancingisa.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121212))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(  // Logout button
                    onClick = {
                        val file = File(context.filesDir, "login_data.txt")
                        if (file.exists()) file.delete()
                        navController.navigate("login")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Log Out", fontSize = 18.sp, color = Color.White)
                }
            }
        },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->
        val itemColors = ListItemDefaults.colors(
            containerColor   = Color(0xFF1E1E1E),
            headlineColor    = Color.White,
            supportingColor  = Color(0xFFB0B0B0),
            leadingIconColor = Color(0xFFBB86FC)
        )

        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // General
            item {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("general") },
                    colors = itemColors,
                    leadingContent = {
                        Icon(Icons.Filled.Settings, contentDescription = "General icon")
                    },
                    headlineContent = { Text("General Settings") },
                    supportingContent = {
                        Text("Dark Mode, Notifications and Sounds", fontSize = 12.sp)
                    }
                )
            }
            item { Divider(color = Color.DarkGray) }

            // Accessibility
            item {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("accessibility") },
                    colors = itemColors,
                    leadingContent = {
                        Icon(Icons.Filled.Accessibility, contentDescription = "Accessibility icon")
                    },
                    headlineContent = { Text("Accessibility Settings") },
                    supportingContent = {
                        Text("Font Size, Dyslexia Mode", fontSize = 12.sp)
                    }
                )
            }
            item { Divider(color = Color.DarkGray) }

            // Privacy
            item {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("privacy") },
                    colors = itemColors,
                    leadingContent = {
                        Icon(Icons.Filled.Lock, contentDescription = "Privacy icon")
                    },
                    headlineContent = { Text("Privacy Settings") },
                    supportingContent = {
                        Text("View collected data, delete my data", fontSize = 12.sp)
                    }
                )
            }
            item { Divider(color = Color.DarkGray) }

            // Info / Support
            item {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("infoSupport") },
                    colors = itemColors,
                    leadingContent = {
                        Icon(Icons.Filled.Info, contentDescription = "Info icon")
                    },
                    headlineContent = { Text("Info / Support") },
                    supportingContent = {
                        Text("App info, Contact us", fontSize = 12.sp)
                    }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
