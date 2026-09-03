package org.audiocular.nitnem.app.ui.practice

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
import kotlinx.coroutines.launch
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.theme.Gold
import org.audiocular.nitnem.app.ui.components.RollingNumberText
import org.audiocular.nitnem.domain.social.FamilyGroup
import org.audiocular.nitnem.domain.social.FamilyLeaderboardRepository
import org.audiocular.nitnem.domain.social.FamilyMemberProgress
import org.audiocular.nitnem.domain.social.rememberFamilyLeaderboardRepository
import org.audiocular.nitnem.models.NitnemUiState
import org.audiocular.nitnem.utils.translate

enum class LeaderboardViewMode {
    FamilyMembers,
    AllGroups
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyLeaderboardTab(
    uiState: NitnemUiState,
    displayLanguage: DisplayLanguage,
    repository: FamilyLeaderboardRepository? = rememberFamilyLeaderboardRepository()
) {
    val scope = rememberCoroutineScope()
    var viewMode by remember { mutableStateOf(LeaderboardViewMode.FamilyMembers) }

    var searchUsername by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResultUser by remember { mutableStateOf<FamilyMemberProgress?>(null) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    var showEditGroupNameDialog by remember { mutableStateOf(false) }
    var newGroupNameInput by remember { mutableStateOf("") }

    val authLauncher = org.audiocular.nitnem.utils.rememberAuthLauncher()
    val authUser = authLauncher.currentUser
    val userDisplayName = authUser?.displayName?.takeIf { it.isNotBlank() }
        ?: authUser?.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.split(" ")?.joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
        ?: "You"

    val currentMember = remember(uiState.totalXp, uiState.streakDays, uiState.quizHighscore, uiState.totalListeningMinutes, userDisplayName, authUser?.uid) {
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

    var allGroups by remember { mutableStateOf<List<FamilyGroup>>(emptyList()) }
    var isLoadingGroups by remember { mutableStateOf(false) }

    // Load actual group from repository & auto-join/sync
    LaunchedEffect(currentMember.userId, repository) {
        if (repository != null) {
            try {
                // Auto sync current member so they are registered in family_groups/family_group_main/members
                repository.syncUserProgress("family_group_main", currentMember)

                val realGroup = repository.getFamilyGroup("family_group_main")
                if (realGroup != null && realGroup.members.isNotEmpty()) {
                    val updated = (realGroup.members.filterNot { it.userId == currentMember.userId } + currentMember)
                        .sortedByDescending { it.xp }
                    currentGroup = realGroup.copy(
                        members = updated,
                        groupTotalXp = updated.sumOf { it.xp },
                        groupTotalStreak = updated.maxOfOrNull { it.currentStreak } ?: uiState.streakDays
                    )
                }

                allGroups = repository.getAllFamilyGroups().ifEmpty { listOf(currentGroup) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp, top = 8.dp)
    ) {
        // ── TOP TAB SWITCHER: Family Sangat vs Group Rankings ────────────────
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
            // ── TOP HEADER CARD: Group Summary & Custom Name Edit ──────────────────
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
                                    Text(
                                        text = "Code: ${currentGroup.inviteCode}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gold
                                    )
                                }

                                Button(
                                    onClick = { showAddDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Add".translate(displayLanguage), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }

                            // Stats Highlights Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LeaderboardStatPill(
                                    icon = Icons.Default.LocalFireDepartment,
                                    title = "Group Streak".translate(displayLanguage),
                                    value = "${currentGroup.groupTotalStreak} Days",
                                    color = Color(0xFFFF5722)
                                )
                                LeaderboardStatPill(
                                    icon = Icons.Default.Star,
                                    title = "Total XP".translate(displayLanguage),
                                    value = "${currentGroup.groupTotalXp} XP",
                                    color = Gold
                                )
                                LeaderboardStatPill(
                                    icon = Icons.Default.CheckCircle,
                                    title = "Members".translate(displayLanguage),
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
                        text = "🏆 Sangat Member Rankings".translate(displayLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sorted by XP",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                            }

                            if (member.username.isNotBlank()) {
                                Text(
                                    text = "@${member.username}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.height(4.dp))

                            // Stats Mini Row: Streak, Banis read, Recitation
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
                                val found = repository?.findUserByUsername(searchUsername) ?: run {
                                    // Local mock resolver if offline
                                    FamilyMemberProgress(
                                        userId = "user_${System.currentTimeMillis()}",
                                        username = searchUsername.lowercase().trim(),
                                        displayName = searchUsername.replaceFirstChar { it.uppercase() },
                                        xp = 1800,
                                        level = 3,
                                        currentStreak = 4,
                                        bestStreak = 6,
                                        totalRecitationMinutes = 110,
                                        completedBanisCount = 14,
                                        quizScore = 75
                                    )
                                }
                                searchResultUser = found
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
