    package com.creativeblisslabs.nitnemguktasteek.app.ui.components

    import androidx.compose.animation.core.*
    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.background
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.horizontalScroll
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.*
    import androidx.compose.material.icons.outlined.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Brush
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.vector.ImageVector
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.text.style.TextOverflow
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.compose.ui.window.Dialog
    import androidx.compose.ui.window.DialogProperties
    import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
    import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
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
                        val name = baniId.replace("-", " ")
                            .split(" ")
                            .joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
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
                    color = SurfaceColor,
                    border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
                    shadowElevation = 18.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
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
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Spiritual Growth Tracker".translate(displayLanguage),
                                        fontSize = 11.5.sp,
                                        color = TextSecondary
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
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                    if (isWideScreen) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LevelHeroCard(currentLevel, uiState, currentLevelBaseXp, xpSpan, progressInLevel, displayLanguage)
                FilterColumnCentered(
                    selectedFilter = selectedFilter,
                    displayLanguage = displayLanguage
                ) { selectedFilter = it }
            }

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
        // PORTRAIT / MOBILE LAYOUT
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        item {
            LevelHeroCard(currentLevel, uiState, currentLevelBaseXp, xpSpan, progressInLevel, displayLanguage)
            Spacer(Modifier.height(10.dp))
            FilterRowCentered(
                selectedFilter = selectedFilter,
                displayLanguage = displayLanguage
            ) { selectedFilter = it }
            Spacer(Modifier.height(10.dp))
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Gold, modifier = Modifier.size(28.dp), strokeWidth = 2.5.dp)
                }
            }
        } else if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = TextSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier.size(32.dp)
                                )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = "No XP activity recorded yet".translate(displayLanguage),
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                            } else {
                                items(filteredList) { item ->
                                    HistoryCardItem(item)
                                }
                            }
                        }

                        // Done Button stays pinned right at the bottom
                        DoneButton(displayLanguage, onDismiss)
                        }
                    }
                }
            }
        }
    }
    // 1. Filter Item Model with Icon Mapping
    data class FilterItem(
        val key: String,
        val icon: ImageVector
    )

    val DefaultFilters = listOf(
        FilterItem("All", Icons.Outlined.GridView),
        FilterItem("Bani", Icons.Outlined.AutoStories),
        FilterItem("Streak", Icons.Outlined.LocalFireDepartment),
        FilterItem("Quiz", Icons.Outlined.Quiz),
        FilterItem("Practice", Icons.Outlined.FitnessCenter)
    )
    @Composable
    fun FilterRowCentered(
        selectedFilter: String,
        displayLanguage: DisplayLanguage,
        modifier: Modifier = Modifier,
        filters: List<FilterItem> = DefaultFilters,
        onSelect: (String) -> Unit
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            filters.forEach { item ->
                val isSelected = selectedFilter == item.key
                val contentColor = if (isSelected) Color.Black else TextSecondary
                val backgroundColor = if (isSelected) Gold else TextPrimary.copy(alpha = 0.08f)

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = backgroundColor,
                    onClick = { onSelect(item.key) },
                    tonalElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = contentColor
                        )
                        Text(
                            text = item.key.translate(displayLanguage),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // 3. Compact Vertical Filter Column (Drawer / Sidebar)
    @Composable
    fun FilterColumnCentered(
        selectedFilter: String,
        displayLanguage: DisplayLanguage,
        modifier: Modifier = Modifier,
        filters: List<FilterItem> = DefaultFilters,
        onSelect: (String) -> Unit
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filters.forEach { item ->
                val isSelected = selectedFilter == item.key
                val contentColor = if (isSelected) Color.Black else TextSecondary
                val backgroundColor = if (isSelected) Gold else TextPrimary.copy(alpha = 0.06f)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = backgroundColor,
                    onClick = { onSelect(item.key) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item.key.translate(displayLanguage),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = contentColor
                        )
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
            color = TextPrimary.copy(alpha = 0.05f),
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
                                text = "Level $currentLevel".translate(displayLanguage),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Gold
                            )
                            Text(
                                text = "${uiState.totalXp} Total XP Earned".translate(displayLanguage),
                                fontSize = 12.sp,
                                color = TextSecondary
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
                        trackColor = TextPrimary.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }           
@Composable
private fun ActivityListContent(
    isLoading: Boolean,
    filteredList: List<XpHistoryItem>,
    displayLanguage: DisplayLanguage
) {
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
                    tint = TextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "No XP activity recorded yet".translate(displayLanguage),
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList) { item ->
                HistoryCardItem(item)
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
    @Composable
fun HistoryCardItem(item: XpHistoryItem) {
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
        color = TextPrimary.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, TextPrimary.copy(alpha = 0.07f)),
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
                    color = TextPrimary
                )
                Text(
                    text = item.formattedDate.ifBlank { item.category },
                    fontSize = 11.sp,
                    color = TextSecondary
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