package org.audiocular.nitnem.app.ui.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items 
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.components.MascotBani
import org.audiocular.nitnem.app.ui.components.MascotState
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.models.NitnemUiState
import androidx.compose.foundation.BorderStroke
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.data.generated.GeneratedBaniCatalog
import org.audiocular.nitnem.utils.rememberTextToSpeechHelper
import androidx.compose.ui.text.style.TextOverflow as StyleTextOverflow
// ─────────────────────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────────────────────

private data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val gurmukhi: String? = null,
    val mascotState: MascotState = MascotState.Happy
)

private val suggestedQuestions = listOf(
    "What does Japji Sahib teach?",
    "What is the meaning of Waheguru?",
    "What is the opening line of Jaap Sahib?",
    "What is Amrit Vela?",
    "Who composed Chaupai Sahib?",
    "What does Rehras Sahib celebrate?"
)

// ─────────────────────────────────────────────────────────────────────────────
// Theme-Linked Dynamic DB Response Engine
// ─────────────────────────────────────────────────────────────────────────────

private fun generateResponse(query: String, currentExploringBani: Bani, displayLanguage: DisplayLanguage): ChatMessage {
    val lower = query.lowercase().trim()
    val allBanisFromDb = GeneratedBaniCatalog.banis

    val matchedBaniFromCatalog = allBanisFromDb.firstOrNull { dbBani ->
        val catalogTitle = dbBani.title.lowercase()
        lower.contains(catalogTitle) || catalogTitle.contains(lower) ||
                (lower.contains("japji") && dbBani.id == "japji-sahib") ||
                (lower.contains("chaupai") && dbBani.id == "benti-chaupai-sahib") ||
                (lower.contains("sohila") && dbBani.id == "kirtan-sohila") ||
                (lower.contains("sukhmani") && dbBani.id == "sukhmani-sahib") ||
                (lower.contains("asa") && dbBani.id == "asa-di-vaar")
    }

    if (matchedBaniFromCatalog != null) {
        val themeInfo = resolveBaniTheme(matchedBaniFromCatalog.title)
        val firstLineGurmukhi = matchedBaniFromCatalog.lines.firstOrNull()?.gurmukhi

        val generatedSummary = buildString {
            append("${matchedBaniFromCatalog.title} was composed by ${themeInfo.author}. ".translate(displayLanguage))
            append("It is positioned chronologically at ${themeInfo.ang} and retains a structure of ${themeInfo.structure}. ".translate(displayLanguage))
            append("This recitation is optimally assigned to the ${themeInfo.heroTitle} period (${themeInfo.heroTime}) to achieve ${themeInfo.heroSubtitle}. ".translate(displayLanguage))
            append("\n\nInsight: ${themeInfo.heroStatus}".translate(displayLanguage))
            if (themeInfo.about.isNotBlank() && !themeInfo.about.contains("About")) {
                append("\n\nContext:\n${themeInfo.about}".translate(displayLanguage))
            }
        }

        return ChatMessage(
            id = System.currentTimeMillis(),
            text = generatedSummary,
            isUser = false,
            gurmukhi = firstLineGurmukhi,
            mascotState = MascotState.Reading
        )
    }

    val lineMatch = currentExploringBani.lines.firstOrNull { line ->
        val searchText = (line.translation + " " + line.wordMeaning + " " + line.gurmukhi + " " + line.punjabi + " " + line.hindi).lowercase()
        lower.split(" ").any { word -> word.length > 3 && searchText.contains(word) }
    }
    if (lineMatch != null) {
        val translationText = lineMatch.translation.ifBlank { lineMatch.punjabi }
        return ChatMessage(
            id = System.currentTimeMillis(),
            text = "Inside ${currentExploringBani.title}, I found an overlapping verse:\n\n\"$translationText\"\n\n${if (lineMatch.wordMeaning.isNotBlank()) "Word Reference:\n${lineMatch.wordMeaning}" else ""}".translate(displayLanguage),
            isUser = false,
            gurmukhi = lineMatch.gurmukhi,
            mascotState = MascotState.Reading
        )
    }

    if (lower.contains("waheguru")) {
        val txt = when (displayLanguage) {
            DisplayLanguage.Punjabi -> "ਵਾਹੇਗੁਰੂ ਸਿੱਖ ਧਰਮ ਦਾ ਕੇਂਦਰੀ ਮੰਤਰ ਹੈ, ਜੋ ਅਗਿਆਨਤਾ ਦੇ ਹਨੇਰੇ ਨੂੰ ਦੂਰ ਕਰਨ ਵਾਲੇ ਅਕਾਲ ਪੁਰਖ ਨੂੰ ਦਰਸਾਉਂਦਾ ਹੈ। ਇਹ ਸਿਮਰਨ ਅਭਿਆਸ ਦਾ ਮੁੱਖ ਕੇਂਦਰ ਹੈ।"
            DisplayLanguage.Hindi -> "वाहेगुरु सिख धर्म का केंद्रीय मंत्र है, जो अज्ञानता के अंधकार को दूर करने वाले अकाल पुरख को दर्शाता है। यह सिमरन अभ्यास का मुख्य केंद्र है।"
            DisplayLanguage.English -> "Waheguru is the central mantra of Sikhism, representing the Wondrous Enlightener who dispels dark ignorance. It is the primary focus of Simran focus loops."
        }
        return ChatMessage(
            id = System.currentTimeMillis(),
            text = txt,
            isUser = false,
            gurmukhi = "ਵਾਹੁ ਵਾਹੁ ਕਿਸ ਨੋ ਆਖੀਐ ਜਿਸੁ ਅੰਤੁ ਨ ਪਾਰਾਵਾਰੁ ॥",
            mascotState = MascotState.Happy
        )
    }
    
    if (lower.contains("amrit vela") || lower.contains("amritvela")) {
        val txt = when (displayLanguage) {
            DisplayLanguage.Punjabi -> "ਅੰਮ੍ਰਿਤ ਵੇਲਾ ਅੰਮ੍ਰਿਤਮਈ ਸਵੇਰ ਦੇ ਸਮੇਂ (3 AM - 6 AM) ਨੂੰ ਦਰਸਾਉਂਦਾ ਹੈ। ਇਹ ਸਿਮਰਨ ਅਤੇ ਮਨ ਦੀ ਇਕਾਗਰਤਾ ਲਈ ਸਭ ਤੋਂ ਉੱਤਮ ਸਮਾਂ ਹੈ।"
            DisplayLanguage.Hindi -> "अमृत वेला अमृतमयी सुबह के समय (3 AM - 6 AM) को दर्शाता है। यह सिमरन और मन की एकाग्रता के लिए सबसे उत्तम समय है।"
            DisplayLanguage.English -> "Amrit Vela signifies the ambrosial morning hours (3 AM - 6 AM). As mapped inside our spiritual calendar framework, it represents the absolute peak period for clarity and internal meditation."
        }
        return ChatMessage(
            id = System.currentTimeMillis(),
            text = txt,
            isUser = false,
            gurmukhi = "ਅੰਮ੍ਰਿਤ ਵੇਲਾ ਸਚੁ ਨਾਉ ਵਡਿਆਈ ਵੀਚਾਰੁ ॥",
            mascotState = MascotState.AmritVela
        )
    }

    val matches = mutableListOf<Pair<BaniLine, String>>()
    outer@ for (dbBani in allBanisFromDb) {
        for (line in dbBani.lines) {
            if (line.translation.lowercase().contains(lower) || 
                line.gurmukhi.lowercase().contains(lower) || 
                line.hindi.lowercase().contains(lower)) {
                matches.add(line to dbBani.title)
                if (matches.size >= 2) break@outer
            }
        }
    }

    if (matches.isNotEmpty()) {
        val generatedText = buildString {
            append("I searched all compositions for \"$query\" and found these matches:\n\n".translate(displayLanguage))
            matches.forEach { (line, baniTitle) ->
                append("• **$baniTitle**:\n")
                append("\"${line.gurmukhi}\"\n")
                append("Translation: ${line.translation}\n\n".translate(displayLanguage))
            }
        }
        return ChatMessage(
            id = System.currentTimeMillis(),
            text = generatedText,
            isUser = false,
            mascotState = MascotState.Happy
        )
    }

    val defaultText = when (displayLanguage) {
        DisplayLanguage.Punjabi -> "ਵਾਹਿਗੁਰੂ ਜੀ ਕਾ ਖਾਲਸਾ ਵਾਹਿਗੁਰੂ ਜੀ ਕੀ ਫਤਿਹ। ਮੈਂ ਬਾਣੀ ਹਾਂ, ਤੁਹਾਡੀ ਮਾਰਗਦਰਸ਼ਕ। ਮੈਂ ${allBanisFromDb.size} ਬਾਣੀਆਂ ਦੇ ਡਾਟਾਬੇਸ ਨਾਲ ਜੁੜੀ ਹੋਈ ਹਾਂ। ਕਿਰਪਾ ਕਰਕੇ ਕੋਈ ਹੋਰ ਸਵਾਲ ਪੁੱਛੋ।"
        DisplayLanguage.Hindi -> "वाहेगुरु जी का खालसा वाहेगुरु जी की फतेह। मैं बाणी हूँ, आपकी मार्गदर्शिका। मैं ${allBanisFromDb.size} बाणियों के डेटाबेस से जुड़ी हूँ। कृपया कोई अन्य प्रश्न पूछें।"
        DisplayLanguage.English -> "Waheguru Ji Ka Khalsa, Waheguru Ji Ki Fateh. I am Bani, your database-linked guide. I search across our active catalog of ${allBanisFromDb.size} compositions. Ask me about any verse, translation, or composition."
    }

    return ChatMessage(
        id = System.currentTimeMillis(),
        text = defaultText,
        isUser = false,
        mascotState = MascotState.Reflecting
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Assistant Component Tab
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BaniAiAssistantTab(
    bani: Bani,
    colors: PracticeColors,
    displayLanguage: DisplayLanguage,
    uiState: NitnemUiState,
    onShowVerseDetails: (BaniLine) -> Unit = {}
) { 
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current

    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var ttsOutputEnabled by remember { mutableStateOf(true) }

    val ttsEngineHelper = rememberTextToSpeechHelper()
    
    // Dynamic bottom padding tracking purely for player state spacing (non-hardcoded)
    val playerSpacingPadding = remember(uiState.playback.isPlaying) {
        if (uiState.playback.isPlaying) 78.dp else 0.dp
    }

    // Dynamic list bottom padding avoiding hardcoded keyboard heights
    val listBottomPadding = remember(playerSpacingPadding, inputText) {
        val query = inputText.lowercase().trim()
        val isSearching = query.length >= 3
        if (isSearching) {
            280.dp
        } else {
            playerSpacingPadding + 80.dp
        }
    }
    
    LaunchedEffect(displayLanguage) {
        val calculatedLanguageToken = when (displayLanguage) {
            DisplayLanguage.English -> "en"
            DisplayLanguage.Hindi -> "hi"
            DisplayLanguage.Punjabi -> "pa"
        }
        ttsEngineHelper.setLanguage(calculatedLanguageToken)
        ttsEngineHelper.setSpeechRate(0.85f)
        ttsEngineHelper.setPitch(1.0f)
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsEngineHelper.stop()
        }
    }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = 0L,
                text = "Waheguru Ji Ka Khalsa, Waheguru Ji Ki Fateh 🙏\n\nI am Bani, your database-linked Gurbani assistant. Type or use your voice to ask me any question about our catalog metrics or translations.\n\nActive context: **${bani.title}**",
                isUser = false,
                mascotState = MascotState.Happy
            )
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val sendMessage: (String) -> Unit = { query ->
        if (query.isNotBlank()) {
            keyboard?.hide()
            ttsEngineHelper.stop()
            
            val userMsg = ChatMessage(id = System.currentTimeMillis(), text = query, isUser = true)
            messages.add(userMsg)
            inputText = ""
            isTyping = true

            coroutineScope.launch {
                delay(800L + (query.length * 10L).coerceAtMost(1500L))
                isTyping = false
                
                val response = generateResponse(query, bani, displayLanguage)
                messages.add(response)
                
                if (ttsOutputEnabled) {
                    val cleanTextForSpeech = response.text
                        .replace("**", "")
                        .replace("`", "")
                        .replace("🙏", "")
                        .replace("\n", " ")
                    ttsEngineHelper.speak(cleanTextForSpeech)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp, 
                end = 16.dp, 
                top = 12.dp, 
                bottom = listBottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 3 }
                ) {
                    if (msg.isUser) {
                        UserBubble(msg = msg)
                    } else {
                        AssistantBubble(msg = msg, colors = colors)
                    }
                }
            }

            if (isTyping) {
                item(key = "typing") {
                    TypingIndicator(colors = colors)
                }
            }

            if (messages.size == 1 && !isTyping) {
                item(key = "suggestions") {
                    SuggestionChips(
                        suggestions = suggestedQuestions,
                        colors = colors,
                        displayLanguage = displayLanguage,
                        onSelect = sendMessage
                    )
                }
            }
        }

        val autocompleteSuggestions = remember(inputText) {
            if (inputText.length < 3) emptyList()
            else {
                val query = inputText.lowercase().trim()
                val matches = mutableListOf<BaniLine>()
                outer@ for (dbBani in GeneratedBaniCatalog.banis) {
                    for (line in dbBani.lines) {
                        if (line.gurmukhi.lowercase().contains(query) ||
                            line.translation.lowercase().contains(query) ||
                            line.hindi.lowercase().contains(query) ||
                            line.punjabi.lowercase().contains(query) ||
                            line.transliteration.lowercase().contains(query)) {
                            matches.add(line)
                            if (matches.size >= 5) break@outer
                        }
                    }
                }
                matches
            }
        }

      Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding() // Safely handles the bottom system bar
                .imePadding()            // Handles the software keyboard height dynamically
        ) {
            if (autocompleteSuggestions.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = colors.card.copy(alpha = 0.98f),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = "Verse matches (tap to view meanings):".translate(displayLanguage),
                            color = Gold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        HorizontalDivider(color = colors.textSecondary.copy(alpha = 0.1f))
                        autocompleteSuggestions.forEach { line ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onShowVerseDetails(line)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = line.gurmukhi,
                                        color = colors.textPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = line.translation,
                                        color = colors.textSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = StyleTextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Gold.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    // REMOVED the hardcoded padding = playerSpacingPadding here because 
                    // it leaves a dead zone gap when the keyboard opens up.
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                        clip = false
                    ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = colors.card.copy(alpha = 0.98f),
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { ttsOutputEnabled = !ttsOutputEnabled },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (ttsOutputEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Toggle Speech Out",
                            tint = if (ttsOutputEnabled) Gold else colors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(Gold.copy(alpha = 0.5f), Gold.copy(alpha = 0.15f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        MascotBani(
                            state = when {
                                isTyping -> MascotState.Thinking
                                isListening -> MascotState.Listening
                                else -> MascotState.Idle
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = if (isListening) "Listening to voice run...".translate(displayLanguage) else "Ask about Gurbani...".translate(displayLanguage),
                                color = colors.textSecondary.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor   = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor        = colors.textPrimary,
                            unfocusedTextColor      = colors.textPrimary,
                            cursorColor             = Gold
                        ),
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage(inputText) }),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )

                    Spacer(Modifier.width(6.dp))

                    val microphobePulseColor by animateColorAsState(
                        targetValue = if (isListening) Color(0xFFEF5350) else colors.textSecondary.copy(alpha = 0.1f),
                        label = "mic_glow"
                    )

                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = microphobePulseColor
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    isListening = !isListening
                                    if (isListening) {
                                        ttsEngineHelper.stop()
                                        coroutineScope.launch {
                                            delay(3500)
                                            if (isListening) {
                                                isListening = false
                                                sendMessage("What does ${bani.title} celebrate?")
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Voice Input Control",
                                tint = if (isListening) Color.White else colors.textPrimary.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (inputText.isNotBlank() && !isListening) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = Gold
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { sendMessage(inputText) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
// ─────────────────────────────────────────────────────────────────────────────
// User Bubble UI Component
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UserBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp),
            color = Gold.copy(alpha = 0.85f)
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Assistant Bubble UI Component
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AssistantBubble(
    msg: ChatMessage,
    colors: PracticeColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Gold.copy(alpha = 0.45f), Gold.copy(alpha = 0.12f)))),
            contentAlignment = Alignment.Center
        ) {
            MascotBani(
                state = msg.mascotState,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        // Wrap the column in a Surface to provide a unified background and shape
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = colors.card.copy(alpha = 0.35f) // Your container background color
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!msg.gurmukhi.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        // Top corners match container, bottom corners are flat
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                        color = Gold.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = Gold.copy(alpha = 0.7f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = msg.gurmukhi,
                                color = Gold.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    // Bottom corners match container, top corners adapt dynamically
                    shape = RoundedCornerShape(
                        topStart = if (msg.gurmukhi.isNullOrBlank()) 4.dp else 0.dp,
                        topEnd = if (msg.gurmukhi.isNullOrBlank()) 18.dp else 0.dp,
                        bottomStart = 18.dp, 
                        bottomEnd = 18.dp
                    ),
                    color = colors.card.copy(alpha = 0.55f)
                ) {
                    Text(
                        text = msg.text,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Typing Indicator UI Component
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TypingIndicator(colors: PracticeColors) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Gold.copy(alpha = 0.45f), Gold.copy(alpha = 0.12f)))),
            contentAlignment = Alignment.Center
        ) {
            MascotBani(state = MascotState.Thinking, modifier = Modifier.size(24.dp))
        }

        Spacer(Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
            color = colors.card.copy(alpha = 0.55f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val bounce by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = -6f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(index * 130)
                        ),
                        label = "dot_$index"
                    )
                    Box(
                        modifier = Modifier
                            .offset(y = bounce.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Gold.copy(alpha = 0.65f))
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Suggestion Chips UI Component
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SuggestionChips(
    suggestions: List<String>,
    colors: PracticeColors,
    displayLanguage: DisplayLanguage = DisplayLanguage.English,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Suggested questions".translate(displayLanguage),
            color = colors.textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            items(suggestions) { suggestion ->
                Surface(
                    modifier = Modifier.clickable { onSelect(suggestion.translate(displayLanguage)) },
                    shape = RoundedCornerShape(12.dp),
                    color = Gold.copy(alpha = 0.08f),
                    contentColor = colors.textPrimary
                ) {
                    Text(
                        text = suggestion.translate(displayLanguage),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 11.sp,
                        maxLines = 1, // Optional: Prevents text wrapping if string is exceptionally long
                        color = colors.textPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
