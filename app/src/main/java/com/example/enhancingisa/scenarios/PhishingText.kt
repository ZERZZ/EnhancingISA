package com.example.enhancingisa.scenarios

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private const val OPENAI_API_KEY =
    "sk-proj-BTOyDAYEr64daBDiFqFIdFZO-eR-6tPH_EDQK-QGTeQKN3L3RG1kVCukFq3pLz" +
            "D_9w4qPg5VrfT3BlbkFJzIg7IjF1m11d7VG95IPq9RYhB7s9DTNgBC_Cf4a_RWyIdtql" +
            "5zyGDD0cFDBszJKysmgyrhYQ4A"

private data class ScenarioResponse(val text: String, val hotspots: List<String>)

// ─── result saving  ───────────────────────────────────────────
fun saveQuizResult(
    context: Context,
    username: String,
    quizType: String,
    score: Int,
    totalPossible: Int,
    duration: String
) {
    val validUsername = username.ifBlank { "unknown_user" }
    val fileName = "quiz_results_${validUsername}.txt"
    val file = File(context.filesDir, fileName)
    val timestamp =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    val line =
        "$quizType | Found $score/$totalPossible clues | Time: $duration | $timestamp\n"
    try { file.appendText(line) } catch (_: Exception) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhishingText(
    navController: NavController,
    loggedInUsername: String
) {
    val context = LocalContext.current

    // ─── Game state ───────────────────────────────────────────
    var round by remember { mutableStateOf(0) }
    val totalRounds = 5
    var totalCorrect by remember { mutableStateOf(0) }
    var totalMissed by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var didLogResults by remember { mutableStateOf(false) }
    var totalPossibleClues by remember { mutableStateOf(0) }

    // ─── Exit confirmation ────────────────────────────────────────
    var confirmExit by remember { mutableStateOf(false) }
    BackHandler(enabled = !gameOver) { confirmExit = true }

    // ─── Timer state updated every second ──────────────────────────
    var elapsedSeconds by remember { mutableStateOf(0) }
    LaunchedEffect(gameOver) {
        // end when quiz ends
        while (!gameOver) {
            delay(1000L)
            elapsedSeconds++
        }
    }
    val mins = elapsedSeconds / 60
    val secs = elapsedSeconds % 60
    val elapsedStr = "%d:%02d".format(mins, secs)

    // ─── this rounds phone and time randomly generated ───────────────────────────
    val phoneNumber by remember(round) {
        mutableStateOf(if (Random.nextBoolean()) randomUKPhone() else randomInternationalPhone())
    }
    val messageTime by remember(round) { mutableStateOf(randomTime()) }

    // ─── Fetch & load scenario ──────────────────────────────────────────────────
    var scenario by remember { mutableStateOf<ScenarioResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // ─── Track clicks on all hotspots ──────────────────────────────────────────────
    val clicked = remember { mutableStateMapOf<String, Boolean>() }
    var flashRed by remember { mutableStateOf(false) }

    LaunchedEffect(round) {
        clicked.clear()
        // foreign phone which means its clickable
        if (!phoneNumber.startsWith("07")) {
            clicked[phoneNumber] = false
            totalPossibleClues++
        }
        if (round < totalRounds) {
            isLoading = true
            val resp = fetchScenarioViaHttp(phoneNumber, messageTime)
            // filter out genuine “Lloyds Bank” & “invoice” as prompt seems to make chat make them hotspots
            val filtered = resp.hotspots.filterNot {
                it.equals("Lloyds Bank", ignoreCase = true)
                        || it.equals("invoice", ignoreCase = true)
            }
            filtered.forEach {
                clicked[it] = false
            }
            totalPossibleClues += filtered.size
            scenario = resp.copy(hotspots = filtered)
            isLoading = false
        } else {
            gameOver = true
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF121212))) {
        // ─── Game Over screen ───────────────────────────────
        if (gameOver) {
            if (!didLogResults) {
                LaunchedEffect(Unit) {
                    saveQuizResult(
                        context, loggedInUsername,
                        "Phishing Text",
                        totalCorrect,
                        totalPossibleClues,
                        elapsedStr
                    )
                }
                didLogResults = true
            }
            // results screen ────────────────────────────────────────────────────────
            Card(
                Modifier
                    .padding(24.dp)
                    .align(Alignment.Center),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Quiz Complete!", color = Color.White, fontSize = 24.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "You found $totalCorrect / $totalPossibleClues phishing clues",
                        color = Color(0xFF03DAC6),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Mis-clicks: $totalMissed", color = Color.Red, fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Time: $elapsedStr", color = Color.LightGray, fontSize = 16.sp)
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
                    ) {
                        Text("Back to Dashboard", color = Color.White)
                    }
                }
            }
            return@Box
        }

        // ─── Flash red overlay ─────────────────────────────────────
        if (flashRed) {
            Box(
                Modifier.matchParentSize().background(Color(0x80FF0000))
            )
            LaunchedEffect(flashRed) {
                delay(300L)
                flashRed = false
            }
        }

        // ─── Catch clicks outside hotspots ───────────────────────────
        Box(Modifier
            .matchParentSize()
            .clickable {
                totalMissed++
                flashRed = true
            })

        // ─── Header & phone hotspot ─────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { confirmExit = true }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0A84FF)) // blue
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                phoneNumber,
                color = if (clicked[phoneNumber] == true) Color.Red else Color.White,
                fontSize = 18.sp,
                fontWeight = if (clicked[phoneNumber] == true) FontWeight.Bold else FontWeight.Normal,
                textDecoration = if (clicked[phoneNumber] == true) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.clickable(enabled = clicked.containsKey(phoneNumber)) {
                    clicked[phoneNumber] = true
                    totalCorrect++
                    // only advance when all current clues are found
                    if (clicked.values.all { it }) round++
                }
            )
        }

        // ─── Text Message · Today ... ───────────────────────────────────
        Text(
            "Text Message · Today $messageTime",
            color = Color.LightGray, fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 96.dp)
        )

        // ─── Message body with hotspots ────────────────────
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFF0A84FF),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            scenario?.let { sc ->
                val annotated = buildAnnotatedString {
                    append(sc.text)
                    sc.hotspots.forEach { hs ->
                        val start = sc.text.indexOf(hs).coerceAtLeast(0)
                        val end = start + hs.length
                        if (start < end) {
                            val isLink = hs.startsWith("http")
                                    || hs.startsWith("www.")
                                    || hs.contains(".co.uk")
                                    || hs.contains("bit.ly")
                                    || hs.endsWith(".pdf")
                            val color = when {
                                clicked[hs] == true -> Color.Red
                                isLink -> Color(0xFF0A84FF)
                                else -> Color.White
                            }
                            val deco = when {
                                clicked[hs] == true -> TextDecoration.LineThrough
                                isLink -> TextDecoration.Underline
                                else -> TextDecoration.None
                            }
                            addStyle(SpanStyle(color = color, textDecoration = deco), start, end)
                            addStringAnnotation("HOTSPOT", hs, start, end)
                        }
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.TopCenter)
                        .padding(top = 120.dp)
                        .background(Color(0xFF2C2C2E), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    ClickableText(
                        text = annotated,
                        style = LocalTextStyle.current.copy(
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            color = Color.White
                        ),
                        onClick = { offset ->
                            annotated.getStringAnnotations("HOTSPOT", offset, offset)
                                .firstOrNull()?.let { span ->
                                    val hs = span.item
                                    if (clicked[hs] == false) {
                                        clicked[hs] = true
                                        totalCorrect++
                                        if (clicked.values.all { it }) round++
                                    }
                                } ?: run {
                                totalMissed++; flashRed = true
                            }
                        }
                    )
                }
            }
        }

        // ─── Live score displayed in bottom left ───────────────────────────────────────
        Text(
            "✅ $totalCorrect   ❌ $totalMissed",
            color = Color.White, fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 120.dp)
        )

        // ─── Live timer bottom right ───────────────────────────────────────────────────
        Text(
            elapsedStr,
            color = Color.LightGray, fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 120.dp)
        )

        // ─── Bottom input bar + “Next” arrow UI and next scenario ──────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFF1E1E1E))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = "", readOnly = true, onValueChange = {},
                placeholder = {
                    Text("Type your response…", color = Color.LightGray, fontSize = 15.sp)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFF2C2C2E),
                    cursorColor = Color(0xFF0A84FF)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            IconButton(onClick = {
                if (round < totalRounds - 1) round++ else gameOver = true
            }, modifier = Modifier.size(60.dp).padding(start = 8.dp)) {
                Icon(Icons.Filled.Send, contentDescription = "Next", tint = Color(0xFF0A84FF))
            }
        }

        // ─── Quit confirmation ─────────────────────────────────────
        if (confirmExit) {
            AlertDialog(
                onDismissRequest = { confirmExit = false },
                title = { Text("Leave this scenario?", color = Color.White) },
                text = { Text("Your progress will be lost.", color = Color.White) },
                confirmButton = {
                    TextButton(onClick = {
                        confirmExit = false
                        navController.popBackStack()
                    }) { Text("Yes", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { confirmExit = false }) { Text("No", color = Color.White) }
                },
                containerColor = Color(0xFF2C2C2E),
                tonalElevation = 8.dp
            )
        }
    }
}

// ─── ai scenario fetcher and prompt etc ────────────────────────────────────────
private suspend fun fetchScenarioViaHttp(phone: String, time: String): ScenarioResponse =
    withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.openai.com/v1/chat/completions")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $OPENAI_API_KEY")
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                doOutput = true
            }
            val prompt = """
        Generate an SMS to $phone at $time,
        include a maximum of two regular word typos (like 'transcation' or 'reciept'),
        a fake attachment name (invoice.pdf),
        a mismatched brand (e.g. 'Llodys Bank','Santandar Bank','Facbook'),
        at least one link, including www., bit.ly, or .pdf.
        Return JSON with {"text": "<full>", "hotspots": ["<exact substrings>"]}.
      """.trimIndent()
            val payload = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("max_tokens", 200)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", prompt)
                    })
                })
            }.toString()
            BufferedOutputStream(conn.outputStream).use { it.write(payload.toByteArray()) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = InputStreamReader(stream).use { it.readText() }
            conn.disconnect()
            if (code !in 200..299) {
                Log.e("PhishingText", "HTTP $code → $text")
                return@withContext ScenarioResponse("Error $code fetching scenario", emptyList())
            }
            val content = JSONObject(text)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
            val obj = JSONObject(content)
            val body = obj.getString("text")
            val arr = obj.getJSONArray("hotspots")
            val hs = List(arr.length()) { i -> arr.getString(i) }
            return@withContext ScenarioResponse(body, hs)
        } catch (e: Exception) {
            Log.e("PhishingText", "Exception fetching scenario", e)
            return@withContext ScenarioResponse("Network error: ${e.message}", emptyList())
        }
    }

// ─── Random helpers ─────────────────────────────────────────────────
private fun randomUKPhone(): String {
    val p1 = Random.nextInt(100, 1000)
    val p2 = Random.nextInt(0, 1_000_000)
    return "07$p1 ${p2.toString().padStart(6, '0')}"
}
private fun randomInternationalPhone(): String {
    val codes = listOf("77","33","49","61","1")
    val cc = codes.random()
    val num = Random.nextLong(1_000_000_000,9_999_999_999)
    return "+$cc $num"
}
private fun randomTime(): String {
    val fmt = SimpleDateFormat("h:mm a", Locale.getDefault())
    return fmt.format(Date())
}
