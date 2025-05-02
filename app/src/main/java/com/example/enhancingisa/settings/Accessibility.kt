package com.example.enhancingisa.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilitySettings(
    navController: NavController,
    fontSizeSp: Float,
    onFontSizeChanged: (Float) -> Unit,
    dyslexiaMode: Boolean,
    onDyslexiaChanged: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accessibility Settings", color = Color.White, fontSize = 27.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
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

            // Dyslexia mode toggle
            item {
                ListItem(
                    modifier        = Modifier.fillMaxWidth(),
                    colors          = itemColors,
                    leadingContent  = { Icon(Icons.Filled.Accessibility, contentDescription = "Dyslexia") },
                    headlineContent = { Text("Dyslexia Friendly Font") },
                    trailingContent = {
                        Switch(
                            checked = dyslexiaMode,
                            onCheckedChange = onDyslexiaChanged
                        )
                    }
                )
            }

            // Font size & slider
            item {
                ListItem(
                    modifier         = Modifier.fillMaxWidth(),
                    colors           = itemColors,
                    leadingContent   = { Icon(Icons.Filled.FormatSize, contentDescription = "Font size") },
                    headlineContent  = { Text("Font Size") },
                    supportingContent = {
                        Column(Modifier.padding(top = 4.dp, bottom = 8.dp)) {
                            Text("${fontSizeSp.toInt()} sp", fontSize = 12.sp, color = Color.White)
                            Slider(
                                value = fontSizeSp,
                                onValueChange = onFontSizeChanged,
                                valueRange = 12f..30f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
