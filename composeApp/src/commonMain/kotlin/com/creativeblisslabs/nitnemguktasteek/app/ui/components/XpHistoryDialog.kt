package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Gold
import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyLeaderboardRepository
import com.creativeblisslabs.nitnemguktasteek.domain.social.XpHistoryItem
import com.creativeblisslabs.nitnemguktasteek.domain.social.rememberFamilyLeaderboardRepository
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.utils.translate

@Composable
fun XpHistoryDialog(
    uiState: NitnemUiState,
    displayLanguage: DisplayLanguage,
    repository: FamilyLeaderboardRepository? = rememberFamilyLeaderboardRepository(),
    onDismiss: () -> Unit
) {
    val authLauncher = com.creativeblisslabs.nitnemguktasteek.utils.rememberAuthLauncher()
    val authUser = authLauncher.currentUser
    val userId = authUser?.uid ?: "user_local"

    var selectedFilter by remember { mutableStateOf("All") }
    var historyList by remember { mutableStateOf<List<XpHistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch from Firestore or build derived summary
    LaunchedEffect(userId, repository) {
        isLoading = true
        try {
            val remoteList = if (repository != null && authUser?.uid != null) {
                repository.getXpHistory(authUser.uid)
            } else emptyList()

            if (remoteList.isNotEmpty()) {
                historyList = remoteList
            } else {
                val list = mutableListOf<XpHistoryItem>()
                
                uiState.completedBanis.forEachIndexed { i, baniId ->
                    val name = baniId.replace("-", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
                    list.add(
                        XpHistoryItem(
                            id = "bani_$i",
                            userId = userId,
                            title = "$name Completed",
                            category = "Bani",
                            xpAmount = 100,
                            formattedDate = "Today"
                        )
                    )
                }

                if (uiState.streakDays > 0) {
                    list.add(
                        XpHistoryItem(
                            id = "streak_daily",
                            userId = userId,
                            title = "${uiState.streakDays} Day Nitnem Streak",
                            category = "Streak",
                            xpAmount = (uiState.streakDays * 50).coerceAtLeast(150),
                            formattedDate = "Current Streak"
                        )
                    )
                }

                if (uiState.quizHighscore > 0) {
                    list.add(
                        XpHistoryItem(
                            id = "quiz_high",
                            userId = userId,
                            title = "Gurbani Quiz Mastery",
                            category = "Quiz",
                            xpAmount = uiState.quizHighscore * 20,
                            formattedDate = "Quiz Highscore"
                        )
                    )
                }

                if (uiState.totalListeningMinutes > 0) {
                    list.add(
                        XpHistoryItem(
                            id = "listen_mins",
                            userId = userId,
                            title = "${uiState.totalListeningMinutes}m Gurbani Recitation",
                            category = "Practice",
                            xpAmount = (uiState.totalListeningMinutes * 2).coerceAtLeast(20),
                            formattedDate = "Lifetime Practice"
                        )
                    )
                }

                historyList = list
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val currentLevel = kotlin.math.floor(kotlin.math.sqrt(uiState.totalXp.toDouble() / 50.0)).toInt().coerceAtLeast(1)
    val currentLevelBaseXp = ((currentLevel - 1) * (currentLevel - 1)) * 50
    val nextLevelXp = (currentLevel * currentLevel) * 50
    val xpSpan = (nextLevelXp - currentLevelBaseXp).coerceAtLeast(1)
    val progressInLevel = ((uiState.totalXp - currentLevelBaseXp).toFloat() / xpSpan.toFloat()).coerceIn(0f, 1f)

    val filteredList = remember(historyList, selectedFilter) {
        if (selectedFilter == "All") historyList
        else historyList.filter { it.category.equals(selectedFilter, ignoreCase = true) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            val isWideScreen = maxWidth > 680.dp

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = if (isWideScreen) 760.dp else 460.dp)
                    .fillMaxHeight(if (isWideScreen) 0.85f else 0.88f),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF171513),
                border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
                shadowElevation = 18.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // ── TOP BAR WITH CLOSE BUTTON ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Gold.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, Gold.copy(alpha = 0.4f)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "XP History & Progress".translate(displayLanguage),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Spiritual Growth Tracker".translate(displayLanguage),
                                    fontSize = 11.5.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    if (isWideScreen) {
                        // ── WIDE / LANDSCAPE LAYOUT (Two Columns with Vertical Tabs Stack) ──
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left Column: Hero Card & Vertical Filters Stack
                            Column(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LevelHeroCard(currentLevel, uiState, currentLevelBaseXp, xpSpan, progressInLevel, displayLanguage)
                                FilterColumn(selectedFilter, displayLanguage) { selectedFilter = it }
                            }

                            // Right Column: List & Actions
                            Column(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ActivityListContent(isLoading, filteredList, displayLanguage)
                                }
                                Spacer(Modifier.height(10.dp))
                                DoneButton(displayLanguage, onDismiss)
                            }
                        }
                    } else {
                        // ── PORTRAIT / COMPACT LAYOUT (Horizontal Tabs Row) ──
                        LevelHeroCard(currentLevel, uiState, currentLevelBaseXp, xpSpan, progressInLevel, displayLanguage)

                        Spacer(Modifier.height(12.dp))

                        FilterRow(selectedFilter, displayLanguage) { selectedFilter = it }

                        Spacer(Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            ActivityListContent(isLoading, filteredList, displayLanguage)
                        }

                        Spacer(Modifier.height(14.dp))

                        DoneButton(displayLanguage, onDismiss)
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelHeroCard(
    currentLevel: Int,
    uiState: NitnemUiState,
    currentLevelBaseXp: Int,
    xpSpan: Int,
    progressInLevel: Float,
    displayLanguage: DisplayLanguage
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Gold.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Level $currentLevel Gurmukh".translate(displayLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold
                        )
                        Text(
                            text = "${uiState.totalXp} Total XP Earned".translate(displayLanguage),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Gold.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Gold)
                    ) {
                        Text(
                            text = "${uiState.totalXp - currentLevelBaseXp} / $xpSpan XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { progressInLevel.coerceIn(0.05f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Gold,
                    trackColor = Color.White.copy(alpha = 0.12f)
                )
            }
        }
    }
}

@Composable
private fun FilterRow(selectedFilter: String, displayLanguage: DisplayLanguage, onFilterSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val filters = listOf("All", "Bani", "Streak", "Quiz", "Practice")
        filters.forEach { filter ->
            val isSelected = selectedFilter == filter
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Gold else Color.White.copy(alpha = 0.06f),
                border = BorderStroke(0.8.dp, if (isSelected) Gold else Color.White.copy(alpha = 0.12f)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onFilterSelected(filter) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Text(
                        text = filter.translate(displayLanguage),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterColumn(selectedFilter: String, displayLanguage: DisplayLanguage, onFilterSelected: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val filters = listOf("All", "Bani", "Streak", "Quiz", "Practice")
        filters.forEach { filter ->
            val isSelected = selectedFilter == filter
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) Gold else Color.White.copy(alpha = 0.06f),
                border = BorderStroke(0.8.dp, if (isSelected) Gold else Color.White.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFilterSelected(filter) }
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter.translate(displayLanguage),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityListContent(isLoading: Boolean, filteredList: List<XpHistoryItem>, displayLanguage: DisplayLanguage) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Gold, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
        }
    } else if (filteredList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "No XP activity recorded yet".translate(displayLanguage),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList) { item ->
                val categoryIcon = when (item.category.lowercase()) {
                    "streak" -> Icons.Default.LocalFireDepartment
                    "quiz" -> Icons.Default.School
                    "practice" -> Icons.Default.GraphicEq
                    else -> Icons.Default.MenuBook
                }
                val iconColor = when (item.category.lowercase()) {
                    "streak" -> Color(0xFFFF7043)
                    "quiz" -> Color(0xFF64B5F6)
                    "practice" -> Color(0xFF81C784)
                    else -> Gold
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.04f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = iconColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = item.formattedDate.ifBlank { item.category },
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Gold.copy(alpha = 0.18f),
                            border = BorderStroke(0.8.dp, Gold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "+${item.xpAmount} XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Gold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoneButton(displayLanguage: DisplayLanguage, onDismiss: () -> Unit) {
    Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
    ) {
        Text(
            text = "Done".translate(displayLanguage),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}