package com.example.enhancingisa.scenarios

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private const val OPENAI_API_KEY = "INCLUDE_YOUR_API_KEY_HERE"

private data class EmailScenario(
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val body: String,
    val hotspots: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhishingEmail(navController: NavController) {
    // ─── Game state ────────────────────────────────────────────
    var round        by remember { mutableStateOf(0) }
    val totalRounds  = 5
    var totalCorrect by remember { mutableStateOf(0) }
    var totalMissed  by remember { mutableStateOf(0) }
    var gameOver     by remember { mutableStateOf(false) }

    // ─── Back-button intercept ──────────────────────────────────
    var confirmExit by remember { mutableStateOf(false) }
    BackHandler(enabled = !gameOver) { confirmExit = true }

    // ─── Timestamp ─────────────────────────────────────────────
    val timestamp = remember(round) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    // ─── Scenario & loading ────────────────────────────────────
    var scenario  by remember { mutableStateOf<EmailScenario?>(null) }
    var isLoading by remember { mutableStateOf(true)    }

    // ─── Track clicks on every hotspot  ─────────────────────────
    val clicked = remember { mutableStateMapOf<String, Boolean>() }
    var flashRed by remember { mutableStateOf(false) }

    LaunchedEffect(round) {
        if (round < totalRounds) {
            isLoading = true
            val sc = fetchEmailScenario(timestamp)
            scenario = sc
            clicked.clear()
            clicked[sc.senderEmail] = false
            sc.hotspots.forEach { clicked[it] = false }
            isLoading = false
        } else {
            gameOver = true
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF121212))) {
        // ──── Game over screen ──────────────────────────────────────
        if (gameOver) {
            Column(
                Modifier.align(Alignment.Center).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Game Over", color = Color.White, fontSize = 24.sp)
                Spacer(Modifier.height(8.dp))
                Text("✅ $totalCorrect", color = Color.Cyan, fontSize = 18.sp)
                Text("❌ $totalMissed", color = Color.Red, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
                ) { Text("Back to Dashboard", color = Color.White) }
            }
            return@Box
        }

        // ─── Flash red overlay on mis-click ─────────────────────────
        if (flashRed) {
            Box(Modifier.matchParentSize().background(Color(0x80FF0000)))
            LaunchedEffect(flashRed) {
                delay(300)
                flashRed = false
            }
        }

        // ─── Catch clicks outside hotspots ──────────────────────────────────────────
        Box(Modifier.matchParentSize().clickable {
            totalMissed++
            flashRed = true
        })

        // ──── “< Inbox” back button ───────────────────────────────
        Text(
            "< Inbox",
            color = Color(0xFF0A84FF),
            fontSize = 18.sp,
            modifier = Modifier
                .padding(16.dp)
                .clickable { confirmExit = true }
        )

        // ─── Email card ─────────────────────────────────────────
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFF0A84FF),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            scenario?.let { sc ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 56.dp)
                            .background(Color(0xFF2C2C2E), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        // Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.DarkGray)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(sc.senderName, color = Color.White, fontSize = 16.sp)
                                Text(
                                    sc.senderEmail,
                                    color = if (clicked[sc.senderEmail] == true) Color.Red else Color.White,
                                    textDecoration = if (clicked[sc.senderEmail] == true)
                                        TextDecoration.LineThrough else TextDecoration.None,
                                    modifier = Modifier.clickable {
                                        if (clicked[sc.senderEmail] == false) {
                                            clicked[sc.senderEmail] = true
                                            totalCorrect++
                                            if (clicked.values.all { it }) round++
                                        }
                                    }
                                )
                            }
                            Text(timestamp, color = Color.LightGray, fontSize = 14.sp)
                        }

                        Divider(Modifier.fillMaxWidth().padding(vertical = 12.dp), color = Color.Gray)

                        // Subject
                        val subjAnnotated = buildAnnotatedString {
                            append(sc.subject)
                            sc.hotspots.forEach { hs ->
                                val i   = sc.subject.indexOf(hs).coerceAtLeast(0)
                                val end = i + hs.length
                                if (end <= length) {
                                    val isLink = hs.startsWith("www.") || hs.startsWith("http")
                                    val color = when {
                                        clicked[hs] == true -> Color.Red
                                        isLink               -> Color(0xFF0A84FF)
                                        else                 -> Color.White
                                    }
                                    val deco = when {
                                        clicked[hs] == true -> TextDecoration.LineThrough
                                        isLink               -> TextDecoration.Underline
                                        else                 -> TextDecoration.None
                                    }
                                    addStyle(SpanStyle(color = color, textDecoration = deco), start = i, end = end)
                                    addStringAnnotation("HOT", hs, start = i, end = end)
                                }
                            }
                        }
                        ClickableText(
                            text = subjAnnotated,
                            style = LocalTextStyle.current.copy(color = Color.White, fontSize = 18.sp),
                            modifier = Modifier.padding(bottom = 8.dp),
                            onClick = { off ->
                                subjAnnotated.getStringAnnotations("HOT", off, off)
                                    .firstOrNull()?.let { span ->
                                        val hs = span.item
                                        if (clicked[hs] == false) {
                                            clicked[hs] = true
                                            totalCorrect++
                                            if (clicked.values.all { it }) round++
                                        }
                                    } ?: run {
                                    totalMissed++
                                    flashRed = true
                                }
                            }
                        )

                        // Email body
                        val bodyAnnotated = buildAnnotatedString {
                            append(sc.body)
                            sc.hotspots.forEach { hs ->
                                val i0  = sc.body.indexOf(hs).coerceAtLeast(0)
                                val end = i0 + hs.length
                                if (end <= length) {
                                    val isLink = hs.startsWith("www.") || hs.startsWith("http")
                                    val color = when {
                                        clicked[hs] == true -> Color.Red
                                        isLink               -> Color(0xFF0A84FF)
                                        else                 -> Color.White
                                    }
                                    val deco = when {
                                        clicked[hs] == true -> TextDecoration.LineThrough
                                        isLink               -> TextDecoration.Underline
                                        else                 -> TextDecoration.None
                                    }
                                    addStyle(SpanStyle(color = color, textDecoration = deco), start = i0, end = end)
                                    addStringAnnotation("HOT", hs, start = i0, end = end)
                                }
                            }
                        }
                        ClickableText(
                            text = bodyAnnotated,
                            style = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp, lineHeight = 22.sp),
                            onClick = { off ->
                                bodyAnnotated.getStringAnnotations("HOT", off, off)
                                    .firstOrNull()?.let { span ->
                                        val hs = span.item
                                        if (clicked[hs] == false) {
                                            clicked[hs] = true
                                            totalCorrect++
                                            if (clicked.values.all { it }) round++
                                        }
                                    } ?: run {
                                    totalMissed++
                                    flashRed = true
                                }
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        // Reply / Forward UI
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Reply, contentDescription = "Reply", tint = Color(0xFF0A84FF))
                                Spacer(Modifier.width(4.dp))
                                Text("Reply", color = Color(0xFF0A84FF))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Forward, contentDescription = "Forward", tint = Color(0xFF0A84FF))
                                Spacer(Modifier.width(4.dp))
                                Text("Forward", color = Color(0xFF0A84FF))
                            }
                        }
                    }

                    // ─── Live score counters ────────────────────────────────────────
                    Spacer(Modifier.height(8.dp))
                    Text("✅ $totalCorrect   ❌ $totalMissed", color = Color.White, fontSize = 16.sp)
                }
            }
        }

        // ─── Exit confirmation ───────────────────────────────────
        if (confirmExit) {
            AlertDialog(
                onDismissRequest = { confirmExit = false },
                title           = { Text("Leave this scenario?", color = Color.White) },
                text            = { Text("Your progress will be lost.", color = Color.White) },
                confirmButton   = {
                    TextButton(onClick = {
                        confirmExit = false
                        navController.popBackStack()
                    }) { Text("Yes", color = Color.White) }
                },
                dismissButton   = {
                    TextButton(onClick = { confirmExit = false }) { Text("No", color = Color.White) }
                },
                containerColor  = Color(0xFF2C2C2E),
                tonalElevation  = 8.dp
            )
        }
    }
}

// ─── ai function and related prompt etc ───────────────────────────────────────
private suspend fun fetchEmailScenario(time: String): EmailScenario =
    withContext(Dispatchers.IO) {

        val url  = URL("https://api.openai.com/v1/chat/completions")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer $OPENAI_API_KEY")
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            doOutput = true
        }

        val prompt = """
      Generate an email received at $time.
      Provide:
       • senderName (legit sounding),
       • senderEmail (≤25 chars, one typo),
       • subject (one typo),
       • body (two typos, one www. or bit.ly link).
      Return JSON:
      {
        "senderName":"…",
        "senderEmail":"…",
        "subject":"…",
        "body":"…",
        "hotspots":["<typo1>","<typo2>","<link>"]
      }
    """.trimIndent()

        val payload = JSONObject().apply {
            put("model","gpt-3.5-turbo")
            put("max_tokens",250)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role","system")
                    put("content",prompt)
                })
            })
        }.toString()

        BufferedOutputStream(conn.outputStream).use { it.write(payload.toByteArray()) }
        val code = conn.responseCode
        val text = InputStreamReader(
            if (code in 200..299) conn.inputStream else conn.errorStream
        ).use { it.readText() }
        conn.disconnect()

        if (code !in 200..299) {
            Log.e("PhishingEmail","HTTP $code → $text")
            return@withContext EmailScenario("Error","","","", emptyList())
        }

        val content = JSONObject(text)
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
        val obj = JSONObject(content)

        EmailScenario(
            senderName  = obj.getString("senderName"),
            senderEmail = obj.getString("senderEmail"),
            subject     = obj.getString("subject"),
            body        = obj.getString("body"),
            hotspots    = List(obj.getJSONArray("hotspots").length()) { i ->
                obj.getJSONArray("hotspots").getString(i)
            }
        )
    }
