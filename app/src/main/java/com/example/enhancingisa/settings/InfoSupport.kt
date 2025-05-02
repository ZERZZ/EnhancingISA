package com.example.enhancingisa.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
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
fun InfoSupport(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Info / Support",
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
            contentPadding     = inner,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier           = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(16.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // ─── About the App ───────────────────────────────────────────────────
            item {
                ListItem(
                    modifier         = Modifier.fillMaxWidth(),
                    colors           = itemColors,
                    leadingContent   = {
                        Icon(Icons.Filled.Info, contentDescription = "About app")
                    },
                    headlineContent  = { Text("About this App") },
                    supportingContent = {
                        Text(
                            "This app helps you learn to spot phishing attempts via AI-generated scenarios and quizzes. " +
                                    "Practice identifying suspicious links, typos, and sender addresses in a safe environment.",
                            fontSize = 12.sp
                        )
                    }
                )
            }

            item {
                Divider(color = Color.DarkGray, thickness = 1.dp)
            }

            // ─── Contact Me ────────────────────────────────────────────────────────
            item {
                ListItem(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // launch email app
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:B603275@my.leedscitycollege.ac.uk")
                            }
                            context.startActivity(intent)
                        },
                    colors           = itemColors,
                    leadingContent   = {
                        Icon(Icons.Filled.Email, contentDescription = "Contact email")
                    },
                    headlineContent  = { Text("Contact Me") },
                    supportingContent = {
                        Text("B603275@my.leedscitycollege.ac.uk", fontSize = 12.sp)
                    }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
