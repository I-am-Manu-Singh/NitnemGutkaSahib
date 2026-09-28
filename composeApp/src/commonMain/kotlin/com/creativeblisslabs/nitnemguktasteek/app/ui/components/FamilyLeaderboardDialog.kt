// package com.creativeblisslabs.nitnemguktasteek.app.ui.components

// import androidx.compose.animation.*
// import androidx.compose.foundation.BorderStroke
// import androidx.compose.foundation.background
// import androidx.compose.foundation.clickable
// import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.lazy.LazyColumn
// import androidx.compose.foundation.lazy.itemsIndexed
// import androidx.compose.foundation.shape.CircleShape
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.*
// import androidx.compose.material3.*
// import androidx.compose.runtime.*
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Brush
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.graphics.vector.ImageVector
// import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.text.style.TextAlign
// import androidx.compose.ui.unit.dp
// import androidx.compose.ui.unit.sp
// import androidx.compose.ui.window.Dialog
// import androidx.compose.ui.window.DialogProperties
// import kotlinx.coroutines.launch
// import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
// import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.LeaderboardViewMode
// import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
// import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyGroup
// import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyLeaderboardRepository
// import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyMemberProgress
// import com.creativeblisslabs.nitnemguktasteek.domain.social.rememberFamilyLeaderboardRepository
// import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
// import com.creativeblisslabs.nitnemguktasteek.utils.translate

// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun FamilyLeaderboardDialog(
//     uiState: NitnemUiState,
//     displayLanguage: DisplayLanguage,
//     repository: FamilyLeaderboardRepository? = rememberFamilyLeaderboardRepository(),
//     onDismiss: () -> Unit
// ) {
//     val scope = rememberCoroutineScope()
//     var viewMode by remember { mutableStateOf(LeaderboardViewMode.FamilyMembers) }

//     var searchUsername by remember { mutableStateOf("") }
//     var isSearching by remember { mutableStateOf(false) }
//     var searchResultUser by remember { mutableStateOf<FamilyMemberProgress?>(null) }
//     var searchError by remember { mutableStateOf<String?>(null) }
//     var showAddDialog by remember { mutableStateOf(false) }
//     var isLoadingRealData by remember { mutableStateOf(false) }

//     var showJoinCodeDialog by remember { mutableStateOf(false) }
//     var inputJoinCode by remember { mutableStateOf("") }
//     var joinError by remember { mutableStateOf<String?>(null) }
//     var showLeaveConfirmDialog by remember { mutableStateOf(false) }

//     var showEditGroupNameDialog by remember { mutableStateOf(false) }
//     var newGroupNameInput by remember { mutableStateOf("") }
//     var showCreateGroupDialog by remember { mutableStateOf(false) }
//     var createGroupNameInput by remember { mutableStateOf("") }
//     var isCreatingGroup by remember { mutableStateOf(false) }
//     var createGroupError by remember { mutableStateOf<String?>(null) }

//     val authLauncher = com.creativeblisslabs.nitnemguktasteek.utils.rememberAuthLauncher(
//         onSignInSuccess = {}
//     )
//     val authUser = authLauncher.currentUser
//     val isLoggedIn = authUser != null && authUser.uid.isNotBlank()

//     val userDisplayName = authUser?.displayName?.takeIf { it.isNotBlank() }
//         ?: authUser?.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.split(" ")?.joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
//         ?: "Gurmukh"

//     val billingManager = remember { com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager.instance }
//     val billingState by billingManager.billingState.collectAsState()

//     val currentMember = remember(uiState.totalXp, uiState.streakDays, uiState.quizHighscore, uiState.totalListeningMinutes, userDisplayName, authUser?.uid, billingState, isLoggedIn) {
//         if (!isLoggedIn) null
//         else {
//             val userBadges = mutableListOf<String>()
//             if (billingState.isLifetimePatron) userBadges.add("lifetime_patron")
//             else if (billingState.isPremium) userBadges.add("premium_supporter")
//             else if (billingState.isSupporter) userBadges.add("seva_supporter")

//             FamilyMemberProgress(
//                 userId = authUser!!.uid,
//                 username = authUser.email?.substringBefore('@') ?: "",
//                 displayName = userDisplayName,
//                 xp = uiState.totalXp,
//                 level = kotlin.math.floor(kotlin.math.sqrt(uiState.totalXp.toDouble() / 50.0)).toInt().coerceAtLeast(1),
//                 currentStreak = uiState.streakDays,
//                 bestStreak = uiState.streakDays.coerceAtLeast(1),
//                 totalRecitationMinutes = uiState.totalListeningMinutes,
//                 completedBanisCount = uiState.completedBanis.size,
//                 completedBanisToday = uiState.completedBanis.toList(),
//                 quizScore = uiState.quizHighscore,
//                 badges = userBadges,
//                 avatarMascotState = ""
//             )
//         }
//     }

//     val dynamicDefaultCode = remember(authUser?.uid) {
//         val seed = authUser?.uid?.take(4)?.uppercase() ?: "NK"
//         "${seed}${(1000..9999).random()}"
//     }

//     val defaultGroupId = remember(authUser?.uid) {
//         if (authUser != null && authUser.uid.isNotBlank()) "group_${authUser.uid}" else "group_local"
//     }

//     val defaultGroupName = remember(userDisplayName) {
//         if (userDisplayName.isNotBlank() && userDisplayName != "Gurmukh") "$userDisplayName's Family" else "My Sangat"
//     }

//     var currentGroup by remember(defaultGroupId) {
//         mutableStateOf(
//             FamilyGroup(
//                 groupId = defaultGroupId,
//                 groupName = defaultGroupName,
//                 inviteCode = dynamicDefaultCode,
//                 adminUserId = authUser?.uid ?: "",
//                 groupTotalStreak = uiState.streakDays,
//                 groupTotalXp = uiState.totalXp,
//                 members = currentMember?.let { listOf(it) } ?: emptyList()
//             )
//         )
//     }

//     val isAdmin = remember(currentGroup.adminUserId, authUser?.uid, isLoggedIn) {
//         isLoggedIn && (currentGroup.adminUserId.isEmpty() || currentGroup.adminUserId == authUser?.uid)
//     }

//     var allGroups by remember { mutableStateOf<List<FamilyGroup>>(emptyList()) }
//     var isLoadingGroups by remember { mutableStateOf(false) }
//     var hasLoadedInitialData by remember { mutableStateOf(false) }

//     LaunchedEffect(authUser?.uid, isLoggedIn, repository) {
//         if (repository != null) {
//             isLoadingRealData = true
//             try {
//                 val fetchedGroups = repository.getAllFamilyGroups()
//                 val userGroup = if (currentMember != null) {
//                     fetchedGroups.firstOrNull { g -> g.members.any { m -> m.userId == currentMember.userId } || g.adminUserId == currentMember.userId }
//                 } else null

//                 val targetGroupId = userGroup?.groupId ?: ""

//                 if (currentMember != null) {
//                     repository.syncUserProgress(targetGroupId, currentMember)
//                 }

//                 val realGroup = repository.getFamilyGroup(targetGroupId)
//                 if (realGroup != null) {
//                     val rawMembers = realGroup.members.filterNot { it.userId == "user_me" }
//                     val finalMembers = if (currentMember != null) {
//                         (rawMembers.filterNot { it.userId == currentMember.userId } + currentMember)
//                     } else {
//                         rawMembers
//                     }.distinctBy { it.userId }.sortedByDescending { it.xp }

//                     currentGroup = realGroup.copy(
//                         members = finalMembers,
//                         groupTotalXp = finalMembers.sumOf { it.xp },
//                         groupTotalStreak = finalMembers.maxOfOrNull { it.currentStreak } ?: 0
//                     )
//                 } else if (userGroup != null) {
//                     currentGroup = userGroup
//                 }

//                 allGroups = repository.getAllFamilyGroups().ifEmpty { listOf(currentGroup) }
//             } catch (e: Exception) {
//                 e.printStackTrace()
//             } finally {
//                 isLoadingRealData = false
//                 hasLoadedInitialData = true
//             }
//         } else {
//             hasLoadedInitialData = true
//         }
//     }

//     Dialog(
//         onDismissRequest = onDismiss,
//         properties = DialogProperties(
//             usePlatformDefaultWidth = false,
//             dismissOnBackPress = true,
//             dismissOnClickOutside = true
//         )
//     ) {
//         BoxWithConstraints(
//             modifier = Modifier
//                 .fillMaxSize()
//                 .padding(horizontal = 16.dp, vertical = 24.dp),
//             contentAlignment = Alignment.Center
//         ) {
//             val isWideScreen = maxWidth > 720.dp

//             Surface(
//                 modifier = Modifier
//                     .fillMaxWidth()
//                     .widthIn(max = if (isWideScreen) 780.dp else 480.dp)
//                     .fillMaxHeight(0.9f),
//                 shape = RoundedCornerShape(28.dp),
//                 color = Color(0xFF171513),
//                 border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
//                 shadowElevation = 24.dp
//             ) {
//                 Column(
//                     modifier = Modifier
//                         .fillMaxSize()
//                         .padding(20.dp)
//                 ) {
//                     // ── TOP HEADER BAR ──
//                     Row(
//                         modifier = Modifier.fillMaxWidth(),
//                         horizontalArrangement = Arrangement.SpaceBetween,
//                         verticalAlignment = Alignment.CenterVertically
//                     ) {
//                         Row(
//                             verticalAlignment = Alignment.CenterVertically,
//                             horizontalArrangement = Arrangement.spacedBy(10.dp)
//                         ) {
//                             Surface(
//                                 shape = CircleShape,
//                                 color = Gold.copy(alpha = 0.18f),
//                                 border = BorderStroke(1.dp, Gold.copy(alpha = 0.4f)),
//                                 modifier = Modifier.size(38.dp)
//                             ) {
//                                 Box(contentAlignment = Alignment.Center) {
//                                     Icon(
//                                         imageVector = Icons.Default.EmojiEvents,
//                                         contentDescription = null,
//                                         tint = Gold,
//                                         modifier = Modifier.size(20.dp)
//                                     )
//                                 }
//                             }
//                             Column {
//                                 Text(
//                                     text = "Sangat Standings".translate(displayLanguage),
//                                     fontSize = 18.sp,
//                                     fontWeight = FontWeight.ExtraBold,
//                                     color = Color.White
//                                 )
//                                 Text(
//                                     text = "Real-time Leaderboard & Streaks".translate(displayLanguage),
//                                     fontSize = 11.5.sp,
//                                     color = Color.White.copy(alpha = 0.6f)
//                                 )
//                             }
//                         }

//                         Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
//                             if (viewMode == LeaderboardViewMode.FamilyMembers && isLoggedIn) {
//                                 IconButton(onClick = { 
//                                     createGroupNameInput = ""
//                                     createGroupError = null
//                                     showCreateGroupDialog = true 
//                                 }) {
//                                     Icon(Icons.Default.AddCircleOutline, contentDescription = "Create Group", tint = Gold, modifier = Modifier.size(20.dp))
//                                 }
//                                 IconButton(onClick = { showJoinCodeDialog = true }) {
//                                     Icon(Icons.Default.GroupAdd, contentDescription = "Join with Code", tint = Gold, modifier = Modifier.size(20.dp))
//                                 }
//                                 if (isAdmin) {
//                                     IconButton(onClick = { showAddDialog = true }) {
//                                         Icon(Icons.Default.PersonAdd, contentDescription = "Add Member", tint = Gold, modifier = Modifier.size(20.dp))
//                                     }
//                                 }
//                             }
//                             IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
//                                 Icon(
//                                     imageVector = Icons.Default.Close,
//                                     contentDescription = "Close",
//                                     tint = Color.White.copy(alpha = 0.6f),
//                                     modifier = Modifier.size(18.dp)
//                                 )
//                             }
//                         }
//                     }

//                     Spacer(Modifier.height(14.dp))

//                     if (!hasLoadedInitialData && isLoadingRealData) {
//                         Box(
//                             modifier = Modifier
//                                 .weight(1f)
//                                 .fillMaxWidth(),
//                             contentAlignment = Alignment.Center
//                         ) {
//                             Column(
//                                 horizontalAlignment = Alignment.CenterHorizontally,
//                                 verticalArrangement = Arrangement.spacedBy(12.dp)
//                             ) {
//                                 CircularProgressIndicator(
//                                     color = Gold,
//                                     strokeWidth = 3.dp,
//                                     modifier = Modifier.size(36.dp)
//                                 )
//                                 Text(
//                                     text = "Loading Sangat Standings...".translate(displayLanguage),
//                                     fontSize = 13.sp,
//                                     color = Color.White.copy(alpha = 0.6f)
//                                 )
//                             }
//                         }
//                     } else {
//                         LazyColumn(
//                             modifier = Modifier
//                                 .weight(1f)
//                                 .fillMaxWidth(),
//                             verticalArrangement = Arrangement.spacedBy(14.dp),
//                             contentPadding = PaddingValues(bottom = 20.dp, top = 4.dp)
//                         ) {
//                             // ── SIGN IN PROMPT ──
//                             if (!isLoggedIn) {
//                                 item {
//                                     Surface(
//                                         shape = RoundedCornerShape(20.dp),
//                                         color = Gold.copy(alpha = 0.1f),
//                                         border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
//                                         modifier = Modifier.fillMaxWidth()
//                                     ) {
//                                         Row(
//                                             modifier = Modifier.padding(16.dp),
//                                             verticalAlignment = Alignment.CenterVertically,
//                                             horizontalArrangement = Arrangement.spacedBy(12.dp)
//                                         ) {
//                                             Surface(
//                                                 shape = CircleShape,
//                                                 color = Gold.copy(alpha = 0.2f),
//                                                 modifier = Modifier.size(44.dp)
//                                             ) {
//                                                 Box(contentAlignment = Alignment.Center) {
//                                                     Icon(
//                                                         imageVector = Icons.Default.AccountCircle,
//                                                         contentDescription = null,
//                                                         tint = Gold,
//                                                         modifier = Modifier.size(26.dp)
//                                                     )
//                                                 }
//                                             }

//                                             Column(modifier = Modifier.weight(1f)) {
//                                                 Text(
//                                                     text = "Sign in to join Standings".translate(displayLanguage),
//                                                     fontWeight = FontWeight.Bold,
//                                                     fontSize = 14.sp,
//                                                     color = Color.White
//                                                 )
//                                                 Text(
//                                                     text = "Sync your daily Nitnem streaks, XP, and inspire the Sangat.".translate(displayLanguage),
//                                                     fontSize = 11.5.sp,
//                                                     color = Color.White.copy(alpha = 0.65f),
//                                                     lineHeight = 15.sp
//                                                 )
//                                             }

//                                             Button(
//                                                 onClick = { authLauncher.signInWithGoogle() },
//                                                 colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color.Black),
//                                                 shape = RoundedCornerShape(12.dp),
//                                                 contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
//                                             ) {
//                                                 Text(
//                                                     text = "Sign In".translate(displayLanguage),
//                                                     fontSize = 12.sp,
//                                                     fontWeight = FontWeight.Bold
//                                                 )
//                                             }
//                                         }
//                                     }
//                                 }
//                             }

//                             // ── TAB SWITCHER ──
//                             item {
//                                 Surface(
//                                     shape = RoundedCornerShape(16.dp),
//                                     color = Color.White.copy(alpha = 0.04f),
//                                     border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
//                                     modifier = Modifier.fillMaxWidth()
//                                 ) {
//                                     Row(
//                                         modifier = Modifier.padding(4.dp),
//                                         horizontalArrangement = Arrangement.spacedBy(4.dp)
//                                     ) {
//                                         Surface(
//                                             shape = RoundedCornerShape(12.dp),
//                                             color = if (viewMode == LeaderboardViewMode.FamilyMembers) Gold else Color.Transparent,
//                                             modifier = Modifier
//                                                 .weight(1f)
//                                                 .clickable { viewMode = LeaderboardViewMode.FamilyMembers }
//                                         ) {
//                                             Box(
//                                                 contentAlignment = Alignment.Center,
//                                                 modifier = Modifier.padding(vertical = 10.dp)
//                                             ) {
//                                                 Text(
//                                                     text = "Sangat Members".translate(displayLanguage),
//                                                     fontWeight = FontWeight.Bold,
//                                                     fontSize = 13.sp,
//                                                     color = if (viewMode == LeaderboardViewMode.FamilyMembers) Color.Black else Color.White.copy(alpha = 0.8f)
//                                                 )
//                                             }
//                                         }

//                                         Surface(
//                                             shape = RoundedCornerShape(12.dp),
//                                             color = if (viewMode == LeaderboardViewMode.AllGroups) Gold else Color.Transparent,
//                                             modifier = Modifier
//                                                 .weight(1f)
//                                                 .clickable {
//                                                     viewMode = LeaderboardViewMode.AllGroups
//                                                     if (repository != null) {
//                                                         scope.launch {
//                                                             isLoadingGroups = true
//                                                             allGroups = repository.getAllFamilyGroups().ifEmpty { listOf(currentGroup) }
//                                                             isLoadingGroups = false
//                                                         }
//                                                     }
//                                                 }
//                                         ) {
//                                             Box(
//                                                 contentAlignment = Alignment.Center,
//                                                 modifier = Modifier.padding(vertical = 10.dp)
//                                             ) {
//                                                 Text(
//                                                     text = "Group Rankings".translate(displayLanguage),
//                                                     fontWeight = FontWeight.Bold,
//                                                     fontSize = 13.sp,
//                                                     color = if (viewMode == LeaderboardViewMode.AllGroups) Color.Black else Color.White.copy(alpha = 0.8f)
//                                                 )
//                                             }
//                                         }
//                                     }
//                                 }
//                             }

//                             if (viewMode == LeaderboardViewMode.FamilyMembers) {
//                                 // ── GROUP SUMMARY CARD ──
//                                 item {
//                                     Surface(
//                                         shape = RoundedCornerShape(22.dp),
//                                         color = Color.White.copy(alpha = 0.05f),
//                                         border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
//                                         modifier = Modifier.fillMaxWidth()
//                                     ) {
//                                         Box(
//                                             modifier = Modifier
//                                                 .background(
//                                                     Brush.linearGradient(
//                                                         listOf(Gold.copy(alpha = 0.12f), Color.Transparent)
//                                                     )
//                                                 )
//                                                 .padding(18.dp)
//                                         ) {
//                                             Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
//                                                 Row(
//                                                     modifier = Modifier.fillMaxWidth(),
//                                                     horizontalArrangement = Arrangement.SpaceBetween,
//                                                     verticalAlignment = Alignment.CenterVertically
//                                                 ) {
//                                                     Column(modifier = Modifier.weight(1f)) {
//                                                         Row(
//                                                             verticalAlignment = Alignment.CenterVertically,
//                                                             modifier = Modifier.clickable(enabled = isAdmin) {
//                                                                 newGroupNameInput = currentGroup.groupName
//                                                                 showEditGroupNameDialog = true
//                                                             }
//                                                         ) {
//                                                             Text(
//                                                                 text = currentGroup.groupName,
//                                                                 fontSize = 18.sp,
//                                                                 fontWeight = FontWeight.Bold,
//                                                                 color = Color.White
//                                                             )
//                                                             if (isAdmin) {
//                                                                 Spacer(Modifier.width(6.dp))
//                                                                 Icon(
//                                                                     Icons.Default.Edit,
//                                                                     contentDescription = "Edit Group Name",
//                                                                     tint = Gold,
//                                                                     modifier = Modifier.size(15.dp)
//                                                                 )
//                                                             }
//                                                         }
//                                                         if (isLoggedIn) {
//                                                             Text(
//                                                                 text = "Invite Code: ${currentGroup.inviteCode}",
//                                                                 fontSize = 12.sp,
//                                                                 fontWeight = FontWeight.SemiBold,
//                                                                 color = Gold
//                                                             )
//                                                         }
//                                                     }

//                                                     if (isLoggedIn) {
//                                                         Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//                                                             if (isAdmin) {
//                                                                 Button(
//                                                                     onClick = { showAddDialog = true },
//                                                                     colors = ButtonDefaults.buttonColors(containerColor = Gold),
//                                                                     shape = RoundedCornerShape(12.dp),
//                                                                     contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
//                                                                 ) {
//                                                                     Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
//                                                                     Spacer(Modifier.width(4.dp))
//                                                                     Text("Add".translate(displayLanguage), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
//                                                                 }
//                                                             }

//                                                             OutlinedButton(
//                                                                 onClick = { showLeaveConfirmDialog = true },
//                                                                 shape = RoundedCornerShape(12.dp),
//                                                                 border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
//                                                                 colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
//                                                                 contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
//                                                             ) {
//                                                                 Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
//                                                                 Spacer(Modifier.width(4.dp))
//                                                                 Text("Leave".translate(displayLanguage), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
//                                                             }
//                                                         }
//                                                     }
//                                                 }

//                                                 Row(
//                                                     modifier = Modifier.fillMaxWidth(),
//                                                     horizontalArrangement = Arrangement.SpaceEvenly
//                                                 ) {
//                                                     LeaderboardStatPill(
//                                                         icon = Icons.Default.LocalFireDepartment,
//                                                         title = "Streak",
//                                                         value = "${currentGroup.groupTotalStreak} Days",
//                                                         color = Color(0xFFFF7043)
//                                                     )
//                                                     LeaderboardStatPill(
//                                                         icon = Icons.Default.Star,
//                                                         title = "Collective XP",
//                                                         value = "${currentGroup.groupTotalXp} XP",
//                                                         color = Gold
//                                                     )
//                                                     LeaderboardStatPill(
//                                                         icon = Icons.Default.Group,
//                                                         title = "Members",
//                                                         value = "${currentGroup.members.size}",
//                                                         color = Color(0xFF64B5F6)
//                                                     )
//                                                 }
//                                             }
//                                         }
//                                     }
//                                 }

//                                 if (currentGroup.members.isEmpty()) {
//                                     item {
//                                         Box(
//                                             modifier = Modifier
//                                                 .fillMaxWidth()
//                                                 .padding(vertical = 32.dp),
//                                             contentAlignment = Alignment.Center
//                                         ) {
//                                             Text(
//                                                 text = "No Sangat members yet. Sign in or add members to start standings!".translate(displayLanguage),
//                                                 color = Color.White.copy(alpha = 0.5f),
//                                                 fontSize = 13.sp,
//                                                 textAlign = TextAlign.Center
//                                             )
//                                         }
//                                     }
//                                 }

//                                 itemsIndexed(currentGroup.members) { index, member ->
//                                     val rank = index + 1
//                                     val isUser = isLoggedIn && authUser?.uid == member.userId
//                                     val rankBadgeColor = when (rank) {
//                                         1 -> Color(0xFFFFD700)
//                                         2 -> Color(0xFFC0C0C0)
//                                         3 -> Color(0xFFCD7F32)
//                                         else -> Color.White.copy(alpha = 0.2f)
//                                     }

//                                     Surface(
//                                         shape = RoundedCornerShape(18.dp),
//                                         color = if (isUser) Gold.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f),
//                                         border = BorderStroke(
//                                             if (isUser) 1.5.dp else 1.dp,
//                                             if (isUser) Gold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
//                                         ),
//                                         modifier = Modifier.fillMaxWidth()
//                                     ) {
//                                         Row(
//                                             modifier = Modifier.padding(14.dp),
//                                             verticalAlignment = Alignment.CenterVertically
//                                         ) {
//                                             Surface(
//                                                 shape = CircleShape,
//                                                 color = rankBadgeColor.copy(alpha = if (rank <= 3) 0.25f else 0.1f),
//                                                 border = BorderStroke(1.dp, rankBadgeColor),
//                                                 modifier = Modifier.size(36.dp)
//                                             ) {
//                                                 Box(contentAlignment = Alignment.Center) {
//                                                     Text(
//                                                         text = if (rank <= 3) "#$rank" else "$rank",
//                                                         fontWeight = FontWeight.ExtraBold,
//                                                         fontSize = 13.sp,
//                                                         color = if (rank <= 3) rankBadgeColor else Color.White
//                                                     )
//                                                 }
//                                             }

//                                             Spacer(Modifier.width(12.dp))

//                                             Column(modifier = Modifier.weight(1f)) {
//                                                 Row(verticalAlignment = Alignment.CenterVertically) {
//                                                     Text(
//                                                         text = member.displayName,
//                                                         fontSize = 14.5.sp,
//                                                         fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.Bold,
//                                                         color = if (isUser) Gold else Color.White
//                                                     )
//                                                     Spacer(Modifier.width(6.dp))
//                                                     Surface(
//                                                         shape = RoundedCornerShape(100.dp),
//                                                         color = Gold.copy(alpha = 0.15f)
//                                                     ) {
//                                                         Text(
//                                                             text = "Lvl ${member.level}",
//                                                             fontSize = 10.sp,
//                                                             fontWeight = FontWeight.Bold,
//                                                             color = Gold,
//                                                             modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
//                                                         )
//                                                     }
//                                                 }

//                                                 if (member.username.isNotBlank()) {
//                                                     Text(
//                                                         text = "@${member.username}",
//                                                         fontSize = 11.5.sp,
//                                                         color = Color.White.copy(alpha = 0.5f)
//                                                     )
//                                                 }

//                                                 Spacer(Modifier.height(4.dp))

//                                                 Row(
//                                                     horizontalArrangement = Arrangement.spacedBy(10.dp),
//                                                     verticalAlignment = Alignment.CenterVertically
//                                                 ) {
//                                                     Row(verticalAlignment = Alignment.CenterVertically) {
//                                                         Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF7043), modifier = Modifier.size(13.dp))
//                                                         Spacer(Modifier.width(2.dp))
//                                                         RollingNumberText(
//                                                             value = member.currentStreak,
//                                                             suffix = "d",
//                                                             fontSize = 11.sp,
//                                                             fontWeight = FontWeight.SemiBold,
//                                                             color = Color(0xFFFF7043)
//                                                         )
//                                                     }
//                                                     Row(verticalAlignment = Alignment.CenterVertically) {
//                                                         Icon(Icons.Default.MenuBook, contentDescription = null, tint = Gold, modifier = Modifier.size(13.dp))
//                                                         Spacer(Modifier.width(2.dp))
//                                                         RollingNumberText(
//                                                             value = member.completedBanisCount,
//                                                             suffix = " banis",
//                                                             fontSize = 11.sp,
//                                                             color = Color.White.copy(alpha = 0.6f)
//                                                         )
//                                                     }
//                                                 }
//                                             }

//                                             Surface(
//                                                 shape = RoundedCornerShape(12.dp),
//                                                 color = Gold.copy(alpha = 0.15f),
//                                                 border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
//                                             ) {
//                                                 Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
//                                                     RollingNumberText(
//                                                         value = member.xp,
//                                                         suffix = " XP",
//                                                         fontWeight = FontWeight.Bold,
//                                                         fontSize = 13.sp,
//                                                         color = Gold
//                                                     )
//                                                 }
//                                             }
//                                         }
//                                     }
//                                 }
//                             } else {
//                                 // ── GLOBAL GROUPS ──
//                                 itemsIndexed(allGroups) { index, group ->
//                                     val rank = index + 1
//                                     val isMyGroup = isLoggedIn && group.groupId == currentGroup.groupId
//                                     val rankBadgeColor = when (rank) {
//                                         1 -> Color(0xFFFFD700)
//                                         2 -> Color(0xFFC0C0C0)
//                                         3 -> Color(0xFFCD7F32)
//                                         else -> Color.White.copy(alpha = 0.2f)
//                                     }

//                                     Surface(
//                                         shape = RoundedCornerShape(18.dp),
//                                         color = if (isMyGroup) Gold.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f),
//                                         border = BorderStroke(
//                                             if (isMyGroup) 1.5.dp else 1.dp,
//                                             if (isMyGroup) Gold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
//                                         ),
//                                         modifier = Modifier.fillMaxWidth()
//                                     ) {
//                                         Row(
//                                             modifier = Modifier.padding(14.dp),
//                                             verticalAlignment = Alignment.CenterVertically
//                                         ) {
//                                             Surface(
//                                                 shape = CircleShape,
//                                                 color = rankBadgeColor.copy(alpha = if (rank <= 3) 0.25f else 0.1f),
//                                                 border = BorderStroke(1.dp, rankBadgeColor),
//                                                 modifier = Modifier.size(36.dp)
//                                             ) {
//                                                 Box(contentAlignment = Alignment.Center) {
//                                                     Text(
//                                                         text = "#$rank",
//                                                         fontWeight = FontWeight.ExtraBold,
//                                                         fontSize = 13.sp,
//                                                         color = if (rank <= 3) rankBadgeColor else Color.White
//                                                     )
//                                                 }
//                                             }

//                                             Spacer(Modifier.width(12.dp))

//                                             Column(modifier = Modifier.weight(1f)) {
//                                                 Row(verticalAlignment = Alignment.CenterVertically) {
//                                                     Text(
//                                                         text = group.groupName,
//                                                         fontSize = 15.sp,
//                                                         fontWeight = FontWeight.Bold,
//                                                         color = if (isMyGroup) Gold else Color.White
//                                                     )
//                                                     if (isMyGroup) {
//                                                         Spacer(Modifier.width(6.dp))
//                                                         Surface(
//                                                             shape = RoundedCornerShape(100.dp),
//                                                             color = Gold.copy(alpha = 0.2f)
//                                                         ) {
//                                                             Text(
//                                                                 text = "Your Family".translate(displayLanguage),
//                                                                 fontSize = 9.sp,
//                                                                 fontWeight = FontWeight.Bold,
//                                                                 color = Gold,
//                                                                 modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
//                                                             )
//                                                         }
//                                                     }
//                                                 }

//                                                 Spacer(Modifier.height(4.dp))

//                                                 Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                                                     Text("${group.members.size} members", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
//                                                     Text("${group.groupTotalStreak}D Streak", fontSize = 11.sp, color = Color(0xFFFF7043), fontWeight = FontWeight.SemiBold)
//                                                 }
//                                             }

//                                             Surface(
//                                                 shape = RoundedCornerShape(12.dp),
//                                                 color = Gold.copy(alpha = 0.15f),
//                                                 border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
//                                             ) {
//                                                 Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
//                                                     RollingNumberText(
//                                                         value = group.groupTotalXp,
//                                                         suffix = " XP",
//                                                         fontWeight = FontWeight.Bold,
//                                                         fontSize = 13.sp,
//                                                         color = Gold
//                                                     )
//                                                 }
//                                             }
//                                         }
//                                     }
//                                 }
//                             }
//                         }
//                     }

//                     Spacer(Modifier.height(12.dp))

//                     Button(
//                         onClick = onDismiss,
//                         colors = ButtonDefaults.buttonColors(
//                             containerColor = Gold,
//                             contentColor = Color.Black
//                         ),
//                         shape = RoundedCornerShape(16.dp),
//                         modifier = Modifier
//                             .fillMaxWidth()
//                             .height(48.dp)
//                     ) {
//                         Text(
//                             text = "Done".translate(displayLanguage),
//                             fontSize = 16.sp,
//                             fontWeight = FontWeight.Bold
//                         )
//                     }
//                 }
//             }
//         }
//     }

//     // ── EDIT GROUP NAME DIALOG ──
//     if (showEditGroupNameDialog) {
//         AlertDialog(
//             onDismissRequest = { showEditGroupNameDialog = false },
//             containerColor = Color(0xFF171513),
//             title = { Text(text = "Edit Group Name".translate(displayLanguage), fontWeight = FontWeight.Bold, color = Gold) },
//             text = {
//                 OutlinedTextField(
//                     value = newGroupNameInput,
//                     onValueChange = { newGroupNameInput = it },
//                     label = { Text("Group Name".translate(displayLanguage)) },
//                     singleLine = true,
//                     modifier = Modifier.fillMaxWidth(),
//                     colors = OutlinedTextFieldDefaults.colors(
//                         focusedBorderColor = Gold,
//                         focusedLabelColor = Gold,
//                         cursorColor = Gold,
//                         focusedTextColor = Color.White,
//                         unfocusedTextColor = Color.White
//                     )
//                 )
//             },
//             confirmButton = {
//                 Button(
//                     onClick = {
//                         if (newGroupNameInput.isNotBlank()) {
//                             val trimmed = newGroupNameInput.trim()
//                             currentGroup = currentGroup.copy(groupName = trimmed)
//                             scope.launch {
//                                 repository?.updateGroupName(currentGroup.groupId, trimmed)
//                                 allGroups = repository?.getAllFamilyGroups() ?: allGroups
//                             }
//                             showEditGroupNameDialog = false
//                         }
//                     },
//                     colors = ButtonDefaults.buttonColors(containerColor = Gold)
//                 ) {
//                     Text("Save".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
//                 }
//             },
//             dismissButton = {
//                 TextButton(onClick = { showEditGroupNameDialog = false }) {
//                     Text("Cancel".translate(displayLanguage), color = Color.White.copy(alpha = 0.7f))
//                 }
//             }
//         )
//     }

//     // ── ADD MEMBER DIALOG ──
//     if (showAddDialog) {
//         AlertDialog(
//             onDismissRequest = {
//                 showAddDialog = false
//                 searchUsername = ""
//                 searchResultUser = null
//                 searchError = null
//             },
//             containerColor = Color(0xFF171513),
//             title = { Text(text = "Add Member to Family".translate(displayLanguage), fontWeight = FontWeight.Bold, color = Gold) },
//             text = {
//                 Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                     Text(
//                         text = "Enter username to add them to your family leaderboard:".translate(displayLanguage),
//                         fontSize = 13.sp,
//                         color = Color.White.copy(alpha = 0.7f)
//                     )

//                     OutlinedTextField(
//                         value = searchUsername,
//                         onValueChange = {
//                             searchUsername = it
//                             searchError = null
//                         },
//                         label = { Text("Username".translate(displayLanguage)) },
//                         leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Gold) },
//                         singleLine = true,
//                         modifier = Modifier.fillMaxWidth(),
//                         colors = OutlinedTextFieldDefaults.colors(
//                             focusedBorderColor = Gold,
//                             focusedLabelColor = Gold,
//                             cursorColor = Gold,
//                             focusedTextColor = Color.White,
//                             unfocusedTextColor = Color.White
//                         )
//                     )

//                     if (isSearching) {
//                         Row(
//                             horizontalArrangement = Arrangement.Center,
//                             modifier = Modifier.fillMaxWidth().padding(8.dp)
//                         ) {
//                             CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Gold)
//                         }
//                     }

//                     searchError?.let { err ->
//                         Text(err, color = Color(0xFFFF5252), fontSize = 12.sp)
//                     }

//                     searchResultUser?.let { found ->
//                         Surface(
//                             shape = RoundedCornerShape(14.dp),
//                             color = Color.White.copy(alpha = 0.05f),
//                             border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
//                             modifier = Modifier.fillMaxWidth()
//                         ) {
//                             Row(
//                                 modifier = Modifier.padding(12.dp),
//                                 verticalAlignment = Alignment.CenterVertically
//                             ) {
//                                 Column(modifier = Modifier.weight(1f)) {
//                                     Text(found.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
//                                     Text("@${found.username} • Lvl ${found.level} • ${found.xp} XP", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
//                                 }
//                                 Button(
//                                     onClick = {
//                                         currentGroup = currentGroup.copy(
//                                             members = (currentGroup.members + found).distinctBy { it.userId }.sortedByDescending { it.xp }
//                                         )
//                                         scope.launch {
//                                             repository?.addMemberByUsername(currentGroup.groupId, found.username)
//                                         }
//                                         showAddDialog = false
//                                         searchUsername = ""
//                                         searchResultUser = null
//                                     },
//                                     colors = ButtonDefaults.buttonColors(containerColor = Gold)
//                                 ) {
//                                     Text("Add".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
//                                 }
//                             }
//                         }
//                     }
//                 }
//             },
//             confirmButton = {
//                 Button(
//                     onClick = {
//                         if (searchUsername.isNotBlank()) {
//                             isSearching = true
//                             searchError = null
//                             scope.launch {
//                                 val found = repository?.findUserByUsername(searchUsername)
//                                 if (found != null) {
//                                     searchResultUser = found
//                                 } else {
//                                     searchError = "User not found. Please verify the exact username.".translate(displayLanguage)
//                                 }
//                                 isSearching = false
//                             }
//                         }
//                     },
//                     colors = ButtonDefaults.buttonColors(containerColor = Gold)
//                 ) {
//                     Text("Search".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
//                 }
//             },
//             dismissButton = {
//                 TextButton(onClick = { showAddDialog = false }) {
//                     Text("Cancel".translate(displayLanguage), color = Color.White.copy(alpha = 0.7f))
//                 }
//             }
//         )
//     }

//     // ── JOIN GROUP DIALOG ──
//     if (showJoinCodeDialog && currentMember != null) {
//         AlertDialog(
//             onDismissRequest = {
//                 showJoinCodeDialog = false
//                 inputJoinCode = ""
//                 joinError = null
//             },
//             containerColor = Color(0xFF171513),
//             title = { Text(text = "Join Family Group".translate(displayLanguage), fontWeight = FontWeight.Bold, color = Gold) },
//             text = {
//                 Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                     Text(
//                         text = "Enter invite code provided by your family group admin:".translate(displayLanguage),
//                         fontSize = 13.sp,
//                         color = Color.White.copy(alpha = 0.7f)
//                     )
//                     OutlinedTextField(
//                         value = inputJoinCode,
//                         onValueChange = {
//                             inputJoinCode = it.uppercase()
//                             joinError = null
//                         },
//                         label = { Text("Invite Code (e.g. NK7291)".translate(displayLanguage)) },
//                         singleLine = true,
//                         modifier = Modifier.fillMaxWidth(),
//                         colors = OutlinedTextFieldDefaults.colors(
//                             focusedBorderColor = Gold,
//                             focusedLabelColor = Gold,
//                             cursorColor = Gold,
//                             focusedTextColor = Color.White,
//                             unfocusedTextColor = Color.White
//                         )
//                     )
//                     joinError?.let {
//                         Text(it, color = Color(0xFFFF5252), fontSize = 12.sp)
//                     }
//                 }
//             },
//             confirmButton = {
//                 Button(
//                     onClick = {
//                         if (inputJoinCode.isNotBlank()) {
//                             scope.launch {
//                                 val clean = inputJoinCode.trim().uppercase()
//                                 val joined = repository?.joinGroupByCode(clean, currentMember) ?: false
//                                 if (joined) {
//                                     val group = repository?.getFamilyGroupByCode(clean)
//                                     if (group != null) {
//                                         currentGroup = group
//                                     }
//                                     showJoinCodeDialog = false
//                                     inputJoinCode = ""
//                                 } else {
//                                     joinError = "Group code not found. Please check with your group admin.".translate(displayLanguage)
//                                 }
//                             }
//                         }
//                     },
//                     colors = ButtonDefaults.buttonColors(containerColor = Gold)
//                 ) {
//                     Text("Join".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
//                 }
//             },
//             dismissButton = {
//                 TextButton(onClick = { showJoinCodeDialog = false }) {
//                     Text("Cancel".translate(displayLanguage), color = Color.White.copy(alpha = 0.7f))
//                 }
//             }
//         )
//     }

//     // ── LEAVE CONFIRM DIALOG ──
//     if (showLeaveConfirmDialog && currentMember != null) {
//         AlertDialog(
//             onDismissRequest = { showLeaveConfirmDialog = false },
//             containerColor = Color(0xFF171513),
//             title = { Text(text = "Leave Family Group?".translate(displayLanguage), fontWeight = FontWeight.Bold, color = Color.White) },
//             text = {
//                 Text(
//                     text = "Are you sure you want to leave \"${currentGroup.groupName}\"? You will need the group code to rejoin.".translate(displayLanguage),
//                     fontSize = 13.sp,
//                     color = Color.White.copy(alpha = 0.7f)
//                 )
//             },
//             confirmButton = {
//                 Button(
//                     onClick = {
//                         scope.launch {
//                             repository?.leaveFamilyGroup(currentGroup.groupId, currentMember.userId)
//                             currentGroup = currentGroup.copy(
//                                 members = currentGroup.members.filterNot { it.userId == currentMember.userId }
//                             )
//                             showLeaveConfirmDialog = false
//                         }
//                     },
//                     colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
//                 ) {
//                     Text("Leave".translate(displayLanguage), fontWeight = FontWeight.Bold, color = Color.White)
//                 }
//             },
//             dismissButton = {
//                 TextButton(onClick = { showLeaveConfirmDialog = false }) {
//                     Text("Cancel".translate(displayLanguage), color = Color.White.copy(alpha = 0.7f))
//                 }
//             }
//         )
//     }

//     // ── CREATE GROUP DIALOG ──
//     if (showCreateGroupDialog && currentMember != null) {
//         AlertDialog(
//             onDismissRequest = { 
//                 if (!isCreatingGroup) showCreateGroupDialog = false 
//             },
//             containerColor = Color(0xFF171513),
//             title = {
//                 Row(
//                     verticalAlignment = Alignment.CenterVertically,
//                     horizontalArrangement = Arrangement.spacedBy(8.dp)
//                 ) {
//                     Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Gold)
//                     Text(
//                         text = "Create Sangat Group".translate(displayLanguage),
//                         fontWeight = FontWeight.Bold,
//                         color = Gold
//                     )
//                 }
//             },
//             text = {
//                 Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
//                     Text(
//                         text = "Create a dedicated Sangat group for your family, friends, or local Gurdwara youth. You will be the admin and a unique invite code will be generated to share with members.".translate(displayLanguage),
//                         fontSize = 13.sp,
//                         color = Color.White.copy(alpha = 0.7f),
//                         lineHeight = 17.sp
//                     )

//                     OutlinedTextField(
//                         value = createGroupNameInput,
//                         onValueChange = {
//                             createGroupNameInput = it
//                             createGroupError = null
//                         },
//                         label = { Text("Group Name".translate(displayLanguage)) },
//                         placeholder = { Text("e.g. Singh Sabha Youth") },
//                         singleLine = true,
//                         modifier = Modifier.fillMaxWidth(),
//                         isError = createGroupError != null,
//                         supportingText = {
//                             createGroupError?.let {
//                                 Text(it, color = Color(0xFFFF5252), fontSize = 12.sp)
//                             }
//                         },
//                         colors = OutlinedTextFieldDefaults.colors(
//                             focusedBorderColor = Gold,
//                             focusedLabelColor = Gold,
//                             cursorColor = Gold,
//                             focusedTextColor = Color.White,
//                             unfocusedTextColor = Color.White
//                         )
//                     )
//                 }
//             },
//             confirmButton = {
//                 Button(
//                     onClick = {
//                         val cleanName = createGroupNameInput.trim()
//                         if (cleanName.isBlank()) {
//                             createGroupError = "Please enter a group name.".translate(displayLanguage)
//                             return@Button
//                         }
//                         isCreatingGroup = true
//                         createGroupError = null
//                         scope.launch {
//                             try {
//                                 val created = repository?.createFamilyGroup(cleanName, currentMember)
//                                 if (created != null) {
//                                     currentGroup = created
//                                     showCreateGroupDialog = false
//                                     createGroupNameInput = ""
//                                 } else {
//                                     createGroupError = "Could not create group. Please try again.".translate(displayLanguage)
//                                 }
//                             } catch (e: Exception) {
//                                 createGroupError = e.message ?: "Failed to create group"
//                             } finally {
//                                 isCreatingGroup = false
//                             }
//                         }
//                     },
//                     enabled = !isCreatingGroup && createGroupNameInput.isNotBlank(),
//                     colors = ButtonDefaults.buttonColors(containerColor = Gold)
//                 ) {
//                     if (isCreatingGroup) {
//                         CircularProgressIndicator(
//                             color = Color.Black,
//                             strokeWidth = 2.dp,
//                             modifier = Modifier.size(16.dp)
//                         )
//                     } else {
//                         Text("Create & Get Code".translate(displayLanguage), color = Color.Black, fontWeight = FontWeight.Bold)
//                     }
//                 }
//             },
//             dismissButton = {
//                 TextButton(
//                     onClick = { showCreateGroupDialog = false },
//                     enabled = !isCreatingGroup
//                 ) {
//                     Text("Cancel".translate(displayLanguage), color = Color.White.copy(alpha = 0.7f))
//                 }
//             }
//         )
//     }
// }

// @Composable
// private fun LeaderboardStatPill(
//     icon: ImageVector,
//     title: String,
//     value: String,
//     color: Color
// ) {
//     Column(
//         horizontalAlignment = Alignment.CenterHorizontally,
//         verticalArrangement = Arrangement.spacedBy(4.dp)
//     ) {
//         Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
//         RollingNumberText(
//             value = value,
//             fontWeight = FontWeight.Bold,
//             fontSize = 14.sp,
//             color = Color.White
//         )
//         Text(title, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
//     }
// }
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
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
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var createGroupNameInput by remember { mutableStateOf("") }
    var isCreatingGroup by remember { mutableStateOf(false) }
    var createGroupError by remember { mutableStateOf<String?>(null) }

    val authLauncher = com.creativeblisslabs.nitnemguktasteek.utils.rememberAuthLauncher(
        onSignInSuccess = {}
    )
    val authUser = authLauncher.currentUser
    val isLoggedIn = authUser != null && authUser.uid.isNotBlank()

    val userDisplayName = authUser?.displayName?.takeIf { it.isNotBlank() }
        ?: authUser?.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.split(" ")?.joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
        ?: "Gurmukh"

    val billingManager = remember { com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager.instance }
    val billingState by billingManager.billingState.collectAsState()

    val currentMember = remember(uiState.totalXp, uiState.streakDays, uiState.quizHighscore, uiState.totalListeningMinutes, userDisplayName, authUser?.uid, billingState, isLoggedIn) {
        if (!isLoggedIn) null
        else {
            val userBadges = mutableListOf<String>()
            if (billingState.isLifetimePatron) userBadges.add("lifetime_patron")
            else if (billingState.isPremium) userBadges.add("premium_supporter")
            else if (billingState.isSupporter) userBadges.add("seva_supporter")

            FamilyMemberProgress(
                userId = authUser!!.uid,
                username = authUser.email?.substringBefore('@') ?: "",
                displayName = userDisplayName,
                xp = uiState.totalXp,
                level = kotlin.math.floor(kotlin.math.sqrt(uiState.totalXp.toDouble() / 50.0)).toInt().coerceAtLeast(1),
                currentStreak = uiState.streakDays,
                bestStreak = uiState.streakDays.coerceAtLeast(1),
                totalRecitationMinutes = uiState.totalListeningMinutes,
                completedBanisCount = uiState.completedBanis.size,
                completedBanisToday = uiState.completedBanis.toList(),
                quizScore = uiState.quizHighscore,
                badges = userBadges,
                avatarMascotState = "",
                profilePhotoUrl = authUser.photoUrl
            )
        }
    }

    val dynamicDefaultCode = remember(authUser?.uid) {
        val seed = authUser?.uid?.take(4)?.uppercase() ?: "NK"
        "${seed}${(1000..9999).random()}"
    }

    val defaultGroupId = remember(authUser?.uid) {
        if (authUser != null && authUser.uid.isNotBlank()) "group_${authUser.uid}" else "group_local"
    }

    val defaultGroupName = remember(userDisplayName) {
        if (userDisplayName.isNotBlank() && userDisplayName != "Gurmukh") "$userDisplayName's Family" else "My Sangat"
    }

    var currentGroup by remember(defaultGroupId) {
        mutableStateOf(
            FamilyGroup(
                groupId = defaultGroupId,
                groupName = defaultGroupName,
                inviteCode = dynamicDefaultCode,
                adminUserId = authUser?.uid ?: "",
                groupTotalStreak = uiState.streakDays,
                groupTotalXp = uiState.totalXp,
                members = currentMember?.let { listOf(it) } ?: emptyList()
            )
        )
    }

    val isAdmin = remember(currentGroup.adminUserId, authUser?.uid, isLoggedIn) {
        isLoggedIn && (currentGroup.adminUserId.isEmpty() || currentGroup.adminUserId == authUser?.uid)
    }

    var allGroups by remember { mutableStateOf<List<FamilyGroup>>(emptyList()) }
    var isLoadingGroups by remember { mutableStateOf(false) }
    var hasLoadedInitialData by remember { mutableStateOf(false) }

    LaunchedEffect(authUser?.uid, isLoggedIn, repository) {
        if (repository != null) {
            isLoadingRealData = true
            try {
                val fetchedGroups = repository.getAllFamilyGroups()
                val userGroup = if (currentMember != null) {
                    fetchedGroups.firstOrNull { g -> g.members.any { m -> m.userId == currentMember.userId } || g.adminUserId == currentMember.userId }
                } else null

                val targetGroupId = userGroup?.groupId ?: ""

                if (currentMember != null) {
                    repository.syncUserProgress(targetGroupId, currentMember)
                }

                val realGroup = repository.getFamilyGroup(targetGroupId)
                if (realGroup != null) {
                    val rawMembers = realGroup.members.filterNot { it.userId == "user_me" }
                    val finalMembers = if (currentMember != null) {
                        (rawMembers.filterNot { it.userId == currentMember.userId } + currentMember)
                    } else {
                        rawMembers
                    }.distinctBy { it.userId }.sortedByDescending { it.xp }

                    currentGroup = realGroup.copy(
                        members = finalMembers,
                        groupTotalXp = finalMembers.sumOf { it.xp },
                        groupTotalStreak = finalMembers.maxOfOrNull { it.currentStreak } ?: 0
                    )
                } else if (userGroup != null) {
                    currentGroup = userGroup
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
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
                     val isLandscape = maxWidth > maxHeight && maxWidth > 600.dp

            val isWideScreen = maxWidth > 720.dp

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = if (isWideScreen) 860.dp else 480.dp)
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF171513),
                border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
                shadowElevation = 24.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // ── TOP HEADER BAR ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Gold.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, Gold.copy(alpha = 0.4f)),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Sangat Standings".translate(displayLanguage),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Real-time Leaderboard & Streaks".translate(displayLanguage),
                                    fontSize = 11.5.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (viewMode == LeaderboardViewMode.FamilyMembers && isLoggedIn) {
                                IconButton(onClick = { 
                                    createGroupNameInput = ""
                                    createGroupError = null
                                    showCreateGroupDialog = true 
                                }) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Create Group", tint = Gold, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { showJoinCodeDialog = true }) {
                                    Icon(Icons.Default.GroupAdd, contentDescription = "Join with Code", tint = Gold, modifier = Modifier.size(20.dp))
                                }
                                if (isAdmin) {
                                    IconButton(onClick = { showAddDialog = true }) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Member", tint = Gold, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    if (!hasLoadedInitialData && isLoadingRealData) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
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
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    } else {
                        // ── RESPONSIVE CONTENT LAYOUT ──
                        if (isWideScreen) {
                            // Landscape / Wide screen: Tabs & Summary on Left, List on Right
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Left Pane: Tab Switcher & Group Summary Card
                                Column(
                                    modifier = Modifier
                                        .weight(0.42f)
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Sign In Prompt if applicable
                                    if (!isLoggedIn) {
                                        SignInPromptCard(displayLanguage, authLauncher)
                                    }

                                    // Tab Switcher
                               TabSwitcherComponent(
    viewMode = viewMode,
    displayLanguage = displayLanguage,
    isLandscape = isWideScreen, // <-- Pass the wide screen/landscape state here
    onTabSelected = { newMode ->
        viewMode = newMode
        if (newMode == LeaderboardViewMode.AllGroups && repository != null) {
            scope.launch {
                isLoadingGroups = true
                allGroups = repository.getAllFamilyGroups().ifEmpty { listOf(currentGroup) }
                isLoadingGroups = false
            }
        }
    }
)

                                    if (viewMode == LeaderboardViewMode.FamilyMembers) {
                                        GroupSummaryCard(
                                            currentGroup = currentGroup,
                                            isAdmin = isAdmin,
                                            isLoggedIn = isLoggedIn,
                                            displayLanguage = displayLanguage,
                                            onEditNameClick = {
                                                newGroupNameInput = currentGroup.groupName
                                                showEditGroupNameDialog = true
                                            },
                                            onAddMemberClick = { showAddDialog = true },
                                            onLeaveClick = { showLeaveConfirmDialog = true }
                                        )
                                    }
                                }

                                // Right Pane: Scrollable List Content
                                Box(
                                    modifier = Modifier
                                        .weight(0.58f)
                                        .fillMaxHeight()
                                ) {
                                    LeaderboardListContent(
                                        isLoggedIn = isLoggedIn,
                                        authUser = authUser,
                                        viewMode = viewMode,
                                        currentGroup = currentGroup,
                                        allGroups = allGroups,
                                        displayLanguage = displayLanguage
                                    )
                                }
                            }
                        } else {
                            // Portrait / Narrow screen: Stacked Layout
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(bottom = 20.dp, top = 4.dp)
                            ) {
                                if (!isLoggedIn) {
                                    item {
                                        SignInPromptCard(displayLanguage, authLauncher)
                                    }
                                }

item {
    TabSwitcherComponent(
        viewMode = viewMode,
        displayLanguage = displayLanguage,
        isLandscape = isLandscape, // Pass the configuration state here
        onTabSelected = { newMode ->
            viewMode = newMode
            if (newMode == LeaderboardViewMode.AllGroups && repository != null) {
                scope.launch {
                    isLoadingGroups = true
                    allGroups = repository.getAllFamilyGroups().ifEmpty { listOf(currentGroup) }
                    isLoadingGroups = false
                }
            }
        }
    )
}

                                if (viewMode == LeaderboardViewMode.FamilyMembers) {
                                    item {
                                        GroupSummaryCard(
                                            currentGroup = currentGroup,
                                            isAdmin = isAdmin,
                                            isLoggedIn = isLoggedIn,
                                            displayLanguage = displayLanguage,
                                            onEditNameClick = {
                                                newGroupNameInput = currentGroup.groupName
                                                showEditGroupNameDialog = true
                                            },
                                            onAddMemberClick = { showAddDialog = true },
                                            onLeaveClick = { showLeaveConfirmDialog = true }
                                        )
                                    }
                                }

                                if (viewMode == LeaderboardViewMode.FamilyMembers && currentGroup.members.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No Sangat members yet. Sign in or add members to start standings!".translate(displayLanguage),
                                                color = Color.White.copy(alpha = 0.5f),
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                if (viewMode == LeaderboardViewMode.FamilyMembers) {
                                    itemsIndexed(currentGroup.members) { index, member ->
                                        MemberItemRow(index = index, member = member, isLoggedIn = isLoggedIn, authUser = authUser)
                                    }
                                } else {
                                    itemsIndexed(allGroups) { index, group ->
                                        GroupItemRow(index = index, group = group, isLoggedIn = isLoggedIn, currentGroup = currentGroup, displayLanguage = displayLanguage)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Done".translate(displayLanguage),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // ── DIALOGS (Edit, Add, Join, Leave, Create Group) ──
    // [Dialog implementations remain unchanged below...]
}

@Composable
private fun SignInPromptCard(displayLanguage: DisplayLanguage, authLauncher: Any) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Gold.copy(alpha = 0.1f),
        border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Gold.copy(alpha = 0.2f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sign in to join Standings".translate(displayLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "Sync your daily Nitnem streaks, XP, and inspire the Sangat.".translate(displayLanguage),
                    fontSize = 11.5.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    lineHeight = 15.sp
                )
            }

            Button(
                onClick = { (authLauncher as? com.creativeblisslabs.nitnemguktasteek.utils.AuthLauncher)?.signInWithGoogle() },
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Sign In".translate(displayLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
private fun TabSwitcherComponent(
    viewMode: LeaderboardViewMode,
    displayLanguage: DisplayLanguage,
    isLandscape: Boolean, // Added parameter
    onTabSelected: (LeaderboardViewMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLandscape) {
            // Stack vertically in Landscape / Wide Screen
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Wrap each item in a Row to provide RowScope receiver context
                Row(modifier = Modifier.fillMaxWidth()) {
                    TabItem(
                        text = "Sangat Members".translate(displayLanguage),
                        isSelected = viewMode == LeaderboardViewMode.FamilyMembers,
                        isLandscape = true,
                        onClick = { onTabSelected(LeaderboardViewMode.FamilyMembers) }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    TabItem(
                        text = "Group Rankings".translate(displayLanguage),
                        isSelected = viewMode == LeaderboardViewMode.AllGroups,
                        isLandscape = true,
                        onClick = { onTabSelected(LeaderboardViewMode.AllGroups) }
                    )
                }
            }
        } else {
            // Side-by-side in Portrait
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabItem(
                    text = "Sangat Members".translate(displayLanguage),
                    isSelected = viewMode == LeaderboardViewMode.FamilyMembers,
                    isLandscape = false,
                    onClick = { onTabSelected(LeaderboardViewMode.FamilyMembers) }
                )
                TabItem(
                    text = "Group Rankings".translate(displayLanguage),
                    isSelected = viewMode == LeaderboardViewMode.AllGroups,
                    isLandscape = false,
                    onClick = { onTabSelected(LeaderboardViewMode.AllGroups) }
                )
            }
        }
    }
}
@Composable
private fun RowScope.TabItem( // 1. Added RowScope. here
    text: String,
    isSelected: Boolean,
    isLandscape: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Gold else Color.Transparent,
        modifier = Modifier
            .clickable { onClick() }
            .then(
                if (isLandscape) Modifier.fillMaxWidth() else Modifier.weight(1f) // 2. weight works now!
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
@Composable
private fun GroupSummaryCard(
    currentGroup: FamilyGroup,
    isAdmin: Boolean,
    isLoggedIn: Boolean,
    displayLanguage: DisplayLanguage,
    onEditNameClick: () -> Unit,
    onAddMemberClick: () -> Unit,
    onLeaveClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Gold.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(enabled = isAdmin) { onEditNameClick() }
                        ) {
                            Text(
                                text = currentGroup.groupName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (isAdmin) {
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Group Name",
                                    tint = Gold,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        if (isLoggedIn) {
                            Text(
                                text = "Invite Code: ${currentGroup.inviteCode}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gold
                            )
                        }
                    }

                    if (isLoggedIn) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (isAdmin) {
                                Button(
                                    onClick = onAddMemberClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add".translate(displayLanguage), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }

                            OutlinedButton(
                                onClick = onLeaveClick,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Leave".translate(displayLanguage), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LeaderboardStatPill(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "Streak",
                        value = "${currentGroup.groupTotalStreak} Days",
                        color = Color(0xFFFF7043)
                    )
                    LeaderboardStatPill(
                        icon = Icons.Default.Star,
                        title = "Collective XP",
                        value = "${currentGroup.groupTotalXp} XP",
                        color = Gold
                    )
                    LeaderboardStatPill(
                        icon = Icons.Default.Group,
                        title = "Members",
                        value = "${currentGroup.members.size}",
                        color = Color(0xFF64B5F6)
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardListContent(
    isLoggedIn: Boolean,
    authUser: Any?,
    viewMode: LeaderboardViewMode,
    currentGroup: FamilyGroup,
    allGroups: List<FamilyGroup>,
    displayLanguage: DisplayLanguage
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        if (viewMode == LeaderboardViewMode.FamilyMembers) {
            if (currentGroup.members.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Sangat members yet. Sign in or add members to start standings!".translate(displayLanguage),
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                itemsIndexed(currentGroup.members) { index, member ->
                    MemberItemRow(index = index, member = member, isLoggedIn = isLoggedIn, authUser = authUser)
                }
            }
        } else {
            itemsIndexed(allGroups) { index, group ->
                GroupItemRow(index = index, group = group, isLoggedIn = isLoggedIn, currentGroup = currentGroup, displayLanguage = displayLanguage)
            }
        }
    }
}

@Composable
private fun MemberItemRow(
    index: Int,
    member: FamilyMemberProgress,
    isLoggedIn: Boolean,
    authUser: Any?
) {
    val rank = index + 1
    val isUser = isLoggedIn && (authUser as? com.google.firebase.auth.FirebaseUser)?.uid == member.userId
    val rankBadgeColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color.White.copy(alpha = 0.2f)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isUser) Gold.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f),
        border = BorderStroke(
            if (isUser) 1.5.dp else 1.dp,
            if (isUser) Gold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(44.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = rankBadgeColor.copy(alpha = if (rank <= 3) 0.25f else 0.1f),
                    border = BorderStroke(1.dp, rankBadgeColor),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (member.profilePhotoUrl != null) {
                            AsyncImage(
                                model = member.profilePhotoUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = member.displayName.firstOrNull()?.toString()?.uppercase() ?: "G",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = if (rank <= 3) rankBadgeColor else Color.White
                            )
                        }
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.Black,
                    modifier = Modifier.size(18.dp).offset(x = 2.dp, y = 2.dp),
                    border = BorderStroke(1.dp, rankBadgeColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (rank <= 3) "#$rank" else "$rank",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            color = if (rank <= 3) rankBadgeColor else Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = member.displayName,
                        fontSize = 14.5.sp,
                        fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (isUser) Gold else Color.White
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
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF7043), modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(2.dp))
                        RollingNumberText(
                            value = member.currentStreak,
                            suffix = "d",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF7043)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Gold, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(2.dp))
                        RollingNumberText(
                            value = member.completedBanisCount,
                            suffix = " banis",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
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

@Composable
private fun GroupItemRow(
    index: Int,
    group: FamilyGroup,
    isLoggedIn: Boolean,
    currentGroup: FamilyGroup,
    displayLanguage: DisplayLanguage
) {
    val rank = index + 1
    val isMyGroup = isLoggedIn && group.groupId == currentGroup.groupId
    val rankBadgeColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color.White.copy(alpha = 0.2f)
    }

   Surface(
    shape = RoundedCornerShape(18.dp),
    color = if (isMyGroup) Gold.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f),
    border = BorderStroke(
        if (isMyGroup) 1.5.dp else 1.dp,
        if (isMyGroup) Gold.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
    ),
    // 1. Force the container to wrap its content height instead of expanding
    modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight() 
) {
    Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically // Keeps badge and XP vertically centered
    ) {
        // Rank Badge
        Surface(
            shape = CircleShape,
            color = rankBadgeColor.copy(alpha = if (rank <= 3) 0.25f else 0.1f),
            border = BorderStroke(1.dp, rankBadgeColor),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = if (rank <= 3) rankBadgeColor else Color.White
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Center Content Area
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // 2. Put the Group Name first on its own line so it has room to breathe/wrap
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = group.groupName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMyGroup) Gold else Color.White,
                    modifier = Modifier.weight(1f, fill = false) // Allows text to wrap or truncate instead of pushing components
                )
                
                if (isMyGroup) {
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

            // 3. Keep stats together on the bottom line
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${group.members.size} members", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                Text("${group.groupTotalStreak}D Streak", fontSize = 11.sp, color = Color(0xFFFF7043), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.width(12.dp))

        // XP Badge
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
            color = Color.White
        )
        Text(title, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
    }
}