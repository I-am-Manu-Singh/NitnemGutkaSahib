package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.LeaderboardViewMode
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyGroup
import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyLeaderboardRepository
import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyMemberProgress
import com.creativeblisslabs.nitnemguktasteek.domain.social.rememberFamilyLeaderboardRepository
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.utils.translate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyLeaderboardDialog(
    uiState: NitnemUiState,
    displayLanguage: DisplayLanguage,
    repository: FamilyLeaderboardRepository? = rememberFamilyLeaderboardRepository(),
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var viewMode by remember { mutableStateOf(LeaderboardViewMode.FamilyMembers) }

    var searchUsername by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResultUser by remember { mutableStateOf<FamilyMemberProgress?>(null) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var isLoadingRealData by remember { mutableStateOf(false) }

    var showJoinCodeDialog by remember { mutableStateOf(false) }
    var inputJoinCode by remember { mutableStateOf("") }
    var joinError by remember { mutableStateOf<String?>(null) }
    var showLeaveConfirmDialog by remember { mutableStateOf(false) }

    var showEditGroupNameDialog by remember { mutableStateOf(false) }
    var newGroupNameInput by remember { mutableStateOf("") }

    val authLauncher = com.creativeblisslabs.nitnemguktasteek.utils.rememberAuthLauncher()
    val authUser = authLauncher.currentUser
    val userDisplayName = authUser?.displayName?.takeIf { it.isNotBlank() }
        ?: authUser?.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.split(" ")?.joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
        ?: "You"

    val billingManager = remember { com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager.instance }
    val billingState by billingManager.billingState.collectAsState()

    val currentMember = remember(uiState.totalXp, uiState.streakDays, uiState.quizHighscore, uiState.totalListeningMinutes, userDisplayName, authUser?.uid, billingState) {
        val userBadges = mutableListOf<String>()
        if (billingState.isLifetimePatron) userBadges.add("lifetime_patron")
        else if (billingState.isPremium) userBadges.add("premium_supporter")
        else if (billingState.isSupporter) userBadges.add("seva_supporter")

        FamilyMemberProgress(
            userId = authUser?.uid ?: "user_me",
            username = authUser?.email?.substringBefore('@') ?: "",
            displayName = userDisplayName,
            xp = uiState.totalXp,
            level = (uiState.totalXp / 500).coerceAtLeast(1),
            currentStreak = uiState.streakDays,
            bestStreak = uiState.streakDays.coerceAtLeast(1),
            totalRecitationMinutes = uiState.totalListeningMinutes,
            completedBanisCount = uiState.completedBanis.size,
            completedBanisToday = uiState.completedBanis.toList(),
            quizScore = uiState.quizHighscore,
            badges = userBadges,
            avatarMascotState = ""
        )
    }

    var currentGroup by remember {
        mutableStateOf(
            FamilyGroup(
                groupId = "family_group_main",
                groupName = "Family & Friends Sangat",
                inviteCode = "NITNEM84",
                groupTotalStreak = uiState.streakDays,
                groupTotalXp = uiState.totalXp,
                members = listOf(currentMember)
            )
        )
    }

    val isAdmin = remember(currentGroup.adminUserId, authUser?.uid) {
        currentGroup.adminUserId.isEmpty() || currentGroup.adminUserId == (authUser?.uid ?: "user_me")
    }

    var allGroups by remember { mutableStateOf<List<FamilyGroup>>(emptyList()) }
    var isLoadingGroups by remember { mutableStateOf(false) }

    var hasLoadedInitialData by remember { mutableStateOf(false) }

    // Load actual real group data from repository & auto-join/sync
    LaunchedEffect(currentMember.userId, authUser?.uid, repository) {
        if (repository != null) {
            isLoadingRealData = true
            try {
                // Auto sync current member so they are registered in family_groups/family_group_main/members
                repository.syncUserProgress("family_group_main", currentMember)

                val realGroup = repository.getFamilyGroup("family_group_main")
                if (realGroup != null && realGroup.members.isNotEmpty()) {
                    // Filter out any stale anonymous 'user_me' or duplicate matching UIDs
                    val filteredMembers = realGroup.members.filterNot { 
                        it.userId == currentMember.userId || 
                        it.userId == "user_me" ||
                        (authUser?.uid != null && it.userId == authUser.uid) ||
                        (authUser == null && it.displayName.equals("You", ignoreCase = true))
                    } + currentMember
                    val updatedMembers = filteredMembers.distinctBy { it.userId }.sortedByDescending { it.xp }
                    currentGroup = realGroup.copy(
                        members = updatedMembers,
                        groupTotalXp = updatedMembers.sumOf { it.xp },
                        groupTotalStreak = updatedMembers.maxOfOrNull { it.currentStreak } ?: uiState.streakDays
                    )
                }

                allGroups = repository.getAllFamilyGroups().ifEmpty { listOf(currentGroup) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingRealData = false
                hasLoadedInitialData = true
            }
        } else {
            hasLoadedInitialData = true
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets(0),
                topBar = {
                    TopAppBar(
                        windowInsets = WindowInsets(0),
                        title = {
                            Column {
                                Text(
                                    text = "Family & Sangat Standings".translate(displayLanguage),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Real-time Leaderboard & Streaks".translate(displayLanguage),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        actions = {
                            if (viewMode == LeaderboardViewMode.FamilyMembers) {
                                IconButton(onClick = { showJoinCodeDialog = true }) {
                                    Icon(Icons.Default.GroupAdd, contentDescription = "Join with Code", tint = Gold)
                                }
                                if (isAdmin) {
                                    IconButton(onClick = { showAddDialog = true }) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Member", tint = Gold)
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            ) { padding ->
                if (!hasLoadedInitialData && isLoadingRealData) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = Gold,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Loading Sangat Standings...".translate(displayLanguage),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
                    ) {
                        // ── TOP TAB SWITCHER: Family Members vs Group Rankings ──
                        item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (viewMode == LeaderboardViewMode.FamilyMembers) Gold else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewMode = LeaderboardViewMode.FamilyMembers }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "Family Members".translate(displayLanguage),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (viewMode == LeaderboardViewMode.FamilyMembers) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (viewMode == LeaderboardViewMode.AllGroups) Gold else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            viewMode = LeaderboardViewMode.AllGroups
                                            if (repository != null) {
                                                scope.launch {
                                                    isLoadingGroups = true
                                                    allGroups = repository.getAllFamilyGroups().ifEmpty { listOf(currentGroup) }
                                                    isLoadingGroups = false
                                                }
                                            }
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = "Group Rankings".translate(displayLanguage),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (viewMode == LeaderboardViewMode.AllGroups) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (viewMode == LeaderboardViewMode.FamilyMembers) {
                        // ── GROUP SUMMARY CARD WITH CUSTOM NAME EDIT ──
                        item {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, Gold.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Gold.copy(alpha = 0.15f), Color.Transparent)
                                            )
                                        )
                                        .padding(20.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable {
                                                        newGroupNameInput = currentGroup.groupName
                                                        showEditGroupNameDialog = true
                                                    }
                                                ) {
                                                    Text(
                                                        text = currentGroup.groupName,
                                                        fontSize = 19.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Spacer(Modifier.width(6.dp))
                                                    Icon(
                                                        Icons.Default.Edit,
                                                        contentDescription = "Edit Group Name",
                                                        tint = Gold,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                if (isAdmin) {
                                                    Text(
                                                        text = "Code: ${currentGroup.inviteCode}",
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Gold
                                                    )
                                                }
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (isAdmin) {
                                                    Button(
                                                        onClick = { showAddDialog = true },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                                        shape = RoundedCornerShape(14.dp),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                                    ) {
                                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("Add".translate(displayLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                    }
                                                }

                                                OutlinedButton(
                                                    onClick = { showLeaveConfirmDialog = true },
                                                    shape = RoundedCornerShape(14.dp),
                                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                                                ) {
                                                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(15.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("Leave".translate(displayLanguage), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        // Stats Highlights Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            LeaderboardStatPill(
                                                icon = Icons.Default.LocalFireDepartment,
                                                title = "Group Streak",
                                                value = "${currentGroup.groupTotalStreak} Days",
                                                color = Color(0xFFFF5722)
                                            )
                                            LeaderboardStatPill(
                                                icon = Icons.Default.Star,
                                                title = "Total XP",
                                                value = "${currentGroup.groupTotalXp} XP",
                                                color = Gold
                                            )
                                            LeaderboardStatPill(
                                                icon = Icons.Default.CheckCircle,
                                                title = "Members",
                                                value = "${currentGroup.members.size}",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── SECTION HEADER ────────────────────────────────────────────────
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🏆 Sangat Member Standings".translate(displayLanguage),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isLoadingRealData) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                }
                            }
                        }

                        // ── LEADERBOARD RANKINGS LIST ──────────────────────────────────────
                        itemsIndexed(currentGroup.members) { index, member ->
                            val rank = index + 1
                            val isUser = member.userId == currentMember.userId || (authUser?.uid != null && member.userId == authUser.uid)

                            val rankBadgeColor = when (rank) {
                                1 -> Color(0xFFFFD700) // Gold
                                2 -> Color(0xFFC0C0C0) // Silver
                                3 -> Color(0xFFCD7F32) // Bronze
                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            }

                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (isUser) Gold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    if (isUser) 1.5.dp else 1.dp,
                                    if (isUser) Gold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rank Badge
                                    Surface(
                                        shape = CircleShape,
                                        color = rankBadgeColor.copy(alpha = if (rank <= 3) 0.25f else 0.10f),
                                        border = BorderStroke(1.dp, rankBadgeColor),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (rank <= 3) "#$rank" else "$rank",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                color = if (rank <= 3) rankBadgeColor else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    // Name, Username & Level
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = member.displayName,
                                                fontSize = 15.sp,
                                                fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.Bold,
                                                color = if (isUser) Gold else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = Gold.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "Lvl ${member.level}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Gold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            if (member.badges.contains("lifetime_patron")) {
                                                Spacer(Modifier.width(4.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(100.dp),
                                                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                                    border = BorderStroke(0.8.dp, Color(0xFFFFD700))
                                                ) {
                                                    Text(
                                                        text = "🪯 Lifetime",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color(0xFFFFD700),
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            } else if (member.badges.contains("premium_supporter")) {
                                                Spacer(Modifier.width(4.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(100.dp),
                                                    color = Color(0xFF64B5F6).copy(alpha = 0.2f),
                                                    border = BorderStroke(0.8.dp, Color(0xFF64B5F6))
                                                ) {
                                                    Text(
                                                        text = "⭐ Premium",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF64B5F6),
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            } else if (member.badges.contains("seva_supporter")) {
                                                Spacer(Modifier.width(4.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(100.dp),
                                                    color = Color(0xFFFF8A80).copy(alpha = 0.2f),
                                                    border = BorderStroke(0.8.dp, Color(0xFFFF8A80))
                                                ) {
                                                    Text(
                                                        text = "❤️ Seva",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFF8A80),
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            }
                                        }

                                        if (member.username.isNotBlank()) {
                                            Text(
                                                text = "@${member.username}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        // Stats Mini Row
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(13.dp))
                                                Spacer(Modifier.width(2.dp))
                                                RollingNumberText(
                                                    value = member.currentStreak,
                                                    suffix = "d",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFFFF5722)
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                                                Spacer(Modifier.width(2.dp))
                                                RollingNumberText(
                                                    value = member.completedBanisCount,
                                                    suffix = " banis",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(13.dp))
                                                Spacer(Modifier.width(2.dp))
                                                RollingNumberText(
                                                    value = member.totalRecitationMinutes,
                                                    suffix = "m",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    // XP Score Pill
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Gold.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
                                    ) {
                                        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                            RollingNumberText(
                                                value = member.xp,
                                                suffix = " XP",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Gold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // ── ALL GROUPS / FAMILY TEAMS LEADERBOARD ─────────────────────────
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🌐 Global Family & Sangat Groups".translate(displayLanguage),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isLoadingGroups) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                }
                            }
                        }

                        itemsIndexed(allGroups) { index, group ->
                            val rank = index + 1
                            val isMyGroup = group.groupId == currentGroup.groupId

                            val rankBadgeColor = when (rank) {
                                1 -> Color(0xFFFFD700)
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            }

                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = if (isMyGroup) Gold.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    if (isMyGroup) 1.5.dp else 1.dp,
                                    if (isMyGroup) Gold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = rankBadgeColor.copy(alpha = if (rank <= 3) 0.25f else 0.10f),
                                        border = BorderStroke(1.dp, rankBadgeColor),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "#$rank",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                color = if (rank <= 3) rankBadgeColor else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = group.groupName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isMyGroup) Gold else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isMyGroup) {
                                                Spacer(Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(100.dp),
                                                    color = Gold.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "Your Family".translate(displayLanguage),
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Gold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                                                Spacer(Modifier.width(2.dp))
                                                Text("${group.members.size} members", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(13.dp))
                                                Spacer(Modifier.width(2.dp))
                                                RollingNumberText(
                                                    value = group.groupTotalStreak,
                                                    suffix = "d streak",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFFFF5722)
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Gold.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
                                    ) {
                                        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                            RollingNumberText(
                                                value = group.groupTotalXp,
                                                suffix = " XP",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Gold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }

    // ── EDIT GROUP NAME DIALOG ─────────────────────────────────────────
    if (showEditGroupNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditGroupNameDialog = false },
            title = {
                Text(
                    text = "Edit Family Group Name".translate(displayLanguage),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = newGroupNameInput,
                    onValueChange = { newGroupNameInput = it },
                    label = { Text("Group Name".translate(displayLanguage)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGroupNameInput.isNotBlank()) {
                            val trimmed = newGroupNameInput.trim()
                            currentGroup = currentGroup.copy(groupName = trimmed)
                            scope.launch {
                                repository?.updateGroupName(currentGroup.groupId, trimmed)
                                allGroups = repository?.getAllFamilyGroups() ?: allGroups
                            }
                            showEditGroupNameDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                ) {
                    Text("Save".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGroupNameDialog = false }) {
                    Text("Cancel".translate(displayLanguage))
                }
            }
        )
    }

    // ── ADD FRIEND / USERNAME MODAL DIALOG ─────────────────────────────
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                searchUsername = ""
                searchResultUser = null
                searchError = null
            },
            title = {
                Text(
                    text = "Add Member to Family".translate(displayLanguage),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter username to add them to your family leaderboard standings:".translate(displayLanguage),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = searchUsername,
                        onValueChange = {
                            searchUsername = it
                            searchError = null
                        },
                        label = { Text("Username".translate(displayLanguage)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isSearching) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Gold)
                        }
                    }

                    searchError?.let { err ->
                        Text(err, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }

                    searchResultUser?.let { found ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(found.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("@${found.username} • Lvl ${found.level} • ${found.xp} XP", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = {
                                        currentGroup = currentGroup.copy(
                                            members = (currentGroup.members + found).distinctBy { it.userId }.sortedByDescending { it.xp }
                                        )
                                        scope.launch {
                                            repository?.addMemberByUsername(currentGroup.groupId, found.username)
                                        }
                                        showAddDialog = false
                                        searchUsername = ""
                                        searchResultUser = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                                ) {
                                    Text("Add".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (searchUsername.isNotBlank()) {
                            isSearching = true
                            searchError = null
                            scope.launch {
                                val found = repository?.findUserByUsername(searchUsername)
                                if (found != null) {
                                    searchResultUser = found
                                } else {
                                    searchError = "User not found. Please verify the exact username.".translate(displayLanguage)
                                }
                                isSearching = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                ) {
                    Text("Search".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel".translate(displayLanguage))
                }
            }
        )
    }

    // ── JOIN GROUP WITH CODE DIALOG ──────────────────────────────────
    if (showJoinCodeDialog) {
        AlertDialog(
            onDismissRequest = {
                showJoinCodeDialog = false
                inputJoinCode = ""
                joinError = null
            },
            title = {
                Text(
                    text = "Join Family Group".translate(displayLanguage),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter 6-digit or custom invite code provided by your family group admin:".translate(displayLanguage),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inputJoinCode,
                        onValueChange = {
                            inputJoinCode = it.uppercase()
                            joinError = null
                        },
                        label = { Text("Invite Code (e.g. NK1080)".translate(displayLanguage)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    joinError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputJoinCode.isNotBlank()) {
                            scope.launch {
                                val clean = inputJoinCode.trim().uppercase()
                                val joined = repository?.joinGroupByCode(clean, currentMember) ?: false
                                if (joined) {
                                    val group = repository?.getFamilyGroupByCode(clean)
                                    if (group != null) {
                                        currentGroup = group
                                    }
                                    showJoinCodeDialog = false
                                    inputJoinCode = ""
                                } else {
                                    joinError = "Group code not found. Please check with your group admin.".translate(displayLanguage)
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                ) {
                    Text("Join".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinCodeDialog = false }) {
                    Text("Cancel".translate(displayLanguage))
                }
            }
        )
    }

    // ── LEAVE GROUP CONFIRMATION DIALOG ───────────────────────────────
    if (showLeaveConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmDialog = false },
            title = {
                Text(
                    text = "Leave Family Group?".translate(displayLanguage),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to leave \"${currentGroup.groupName}\"? You will need the group code to rejoin.".translate(displayLanguage),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository?.leaveFamilyGroup(currentGroup.groupId, currentMember.userId)
                            currentGroup = currentGroup.copy(
                                members = currentGroup.members.filterNot { it.userId == currentMember.userId }
                            )
                            showLeaveConfirmDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Leave".translate(displayLanguage), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmDialog = false }) {
                    Text("Cancel".translate(displayLanguage))
                }
            }
        )
    }
}

@Composable
private fun LeaderboardStatPill(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        RollingNumberText(
            value = value,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
