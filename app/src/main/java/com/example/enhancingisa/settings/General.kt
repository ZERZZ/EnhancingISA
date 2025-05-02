package com.example.enhancingisa.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettings(
    navController: NavController,
    darkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    fontSizeSp: Float,
    onFontSizeChanged: (Float) -> Unit
) {
    // local state for notifs n sound
    var notifications by remember { mutableStateOf(false) }
    var sound by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "General Settings",
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
            contentPadding = inner,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // dark mode toggle
            item {
                ListItem(
                    modifier         = Modifier.fillMaxWidth(),
                    colors           = itemColors,
                    leadingContent  = {
                        Icon(
                            Icons.Filled.DarkMode,
                            contentDescription = "Dark mode icon"
                        )
                    },
                    headlineContent = { Text("Dark Mode") },
                    trailingContent = {
                        Switch(
                            checked = darkMode,
                            onCheckedChange = onDarkModeChanged
                        )
                    }
                )
            }

            // notifs toggle
            item {
                ListItem(
                    modifier         = Modifier.fillMaxWidth(),
                    colors           = itemColors,
                    leadingContent  = {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Notifications icon"
                        )
                    },
                    headlineContent = { Text("Notifications") },
                    trailingContent = {
                        Switch(
                            checked = notifications,
                            onCheckedChange = { notifications = it }
                        )
                    }
                )
            }

            // sound toggle
            item {
                ListItem(
                    modifier         = Modifier.fillMaxWidth(),
                    colors           = itemColors,
                    leadingContent  = {
                        Icon(
                            Icons.Filled.VolumeUp,
                            contentDescription = "Sound icon"
                        )
                    },
                    headlineContent = { Text("Sounds") },
                    trailingContent = {
                        Switch(
                            checked = sound,
                            onCheckedChange = { sound = it }
                        )
                    }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
