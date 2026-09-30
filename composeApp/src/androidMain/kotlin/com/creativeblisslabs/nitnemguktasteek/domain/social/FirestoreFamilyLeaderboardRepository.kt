package com.creativeblisslabs.nitnemguktasteek.domain.social

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Firestore Database Schema for Nitnem Gutka Steek:
 *
 * 1. Collection: "users" (Document ID: userId)
 *    Fields:
 *      - userId: String
 *      - displayName: String
 *      - currentStreak: Long
 *      - bestStreak: Long
 *      - totalRecitationMinutes: Long
 *      - completedBanisToday: List<String>
 *      - quizScore: Long
 *      - avatarMascotState: String
 *      - lastActiveTimestamp: Long
 *
 * 2. Collection: "family_groups" (Document ID: groupId)
 *    Fields:
 *      - groupId: String
 *      - groupName: String
 *      - groupTotalStreak: Long
 *      - memberIds: List<String>
 *      - createdAt: Long
 *    Subcollection: "members" (Document ID: userId)
 *      - (Contains FamilyMemberProgress fields)
 *
 * 3. Collection: "daily_hukamnama" (Document ID: YYYY-MM-DD)
 *    Fields:
 *      - date: String
 *      - ang: Long
 *      - gurmukhi: String
 *      - punjabiSteek: String
 *      - englishTranslation: String
 *      - timestamp: Long
 */
class FirestoreFamilyLeaderboardRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FamilyLeaderboardRepository {

    override suspend fun getFamilyGroup(groupId: String): FamilyGroup? = withContext(Dispatchers.IO) {
        try {
            val groupDoc = firestore.collection("family_groups").document(groupId).get().await()
            if (!groupDoc.exists()) return@withContext null

            val groupName = groupDoc.getString("groupName") ?: "Family"
            val totalStreak = groupDoc.getLong("groupTotalStreak")?.toInt() ?: 0
            val adminId = groupDoc.getString("adminUserId") ?: ""

            // Fetch members from subcollection
            val membersSnapshot = firestore.collection("family_groups")
                .document(groupId)
                .collection("members")
                .get()
                .await()

            val members = membersSnapshot.documents.mapNotNull { doc ->
                val uid = doc.getString("userId") ?: doc.id
                // Do not display anonymous placeholder 'user_me' if authenticated members exist
                if (uid == "user_me" && membersSnapshot.documents.size > 1) return@mapNotNull null

                FamilyMemberProgress(
                    userId = uid,
                    username = doc.getString("username") ?: "",
                    displayName = doc.getString("displayName") ?: "Gurmukh",
                    xp = doc.getLong("xp")?.toInt() ?: (doc.getLong("currentStreak")?.toInt() ?: 0) * 100,
                    level = doc.getLong("level")?.toInt() ?: 1,
                    currentStreak = doc.getLong("currentStreak")?.toInt() ?: 0,
                    bestStreak = doc.getLong("bestStreak")?.toInt() ?: 0,
                    weeklyStreak = doc.getLong("weeklyStreak")?.toInt() ?: 0,
                    monthlyStreak = doc.getLong("monthlyStreak")?.toInt() ?: 0,
                    totalRecitationMinutes = doc.getLong("totalRecitationMinutes")?.toInt() ?: 0,
                    completedBanisCount = doc.getLong("completedBanisCount")?.toInt() ?: 0,
                    completedBanisToday = (doc.get("completedBanisToday") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    quizScore = doc.getLong("quizScore")?.toInt() ?: 0,
                    badges = (doc.get("badges") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    avatarMascotState = doc.getString("avatarMascotState") ?: "",
                    profilePhotoUrl = doc.getString("profilePhotoUrl"),
                    lastActiveTimestamp = doc.getLong("lastActiveTimestamp") ?: 0L
                )
            }.sortedByDescending { it.xp }

            val totalXp = members.sumOf { it.xp }

            FamilyGroup(
                groupId = groupId,
                groupName = groupName,
                inviteCode = groupDoc.getString("inviteCode") ?: groupId.takeLast(6).uppercase(),
                adminUserId = adminId.ifEmpty { members.firstOrNull()?.userId ?: "" },
                members = members,
                groupTotalStreak = totalStreak,
                groupTotalXp = totalXp
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getFamilyGroupByCode(inviteCode: String): FamilyGroup? = withContext(Dispatchers.IO) {
        try {
            val cleanCode = inviteCode.trim().uppercase()
            val query = firestore.collection("family_groups")
                .whereEqualTo("inviteCode", cleanCode)
                .limit(1)
                .get()
                .await()

            val doc = query.documents.firstOrNull() ?: return@withContext null
            getFamilyGroup(doc.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getAllFamilyGroups(): List<FamilyGroup> = withContext(Dispatchers.IO) {
        try {
            val groupsSnapshot = firestore.collection("family_groups").get().await()
            val groups = groupsSnapshot.documents.mapNotNull { groupDoc ->
                val groupId = groupDoc.id
                val groupName = groupDoc.getString("groupName") ?: "Family"
                val inviteCode = groupDoc.getString("inviteCode") ?: groupId.takeLast(6).uppercase()
                val adminId = groupDoc.getString("adminUserId") ?: ""

                val membersSnapshot = firestore.collection("family_groups")
                    .document(groupId)
                    .collection("members")
                    .get()
                    .await()

                val members = membersSnapshot.documents.mapNotNull { doc ->
                    val uid = doc.getString("userId") ?: doc.id
                    if (uid == "user_me" && membersSnapshot.documents.size > 1) return@mapNotNull null

                    FamilyMemberProgress(
                        userId = uid,
                        username = doc.getString("username") ?: "",
                        displayName = doc.getString("displayName") ?: "Gurmukh",
                        xp = doc.getLong("xp")?.toInt() ?: (doc.getLong("currentStreak")?.toInt() ?: 0) * 100,
                        level = doc.getLong("level")?.toInt() ?: 1,
                        currentStreak = doc.getLong("currentStreak")?.toInt() ?: 0,
                        bestStreak = doc.getLong("bestStreak")?.toInt() ?: 0,
                        weeklyStreak = doc.getLong("weeklyStreak")?.toInt() ?: 0,
                        monthlyStreak = doc.getLong("monthlyStreak")?.toInt() ?: 0,
                        totalRecitationMinutes = doc.getLong("totalRecitationMinutes")?.toInt() ?: 0,
                        completedBanisCount = doc.getLong("completedBanisCount")?.toInt() ?: 0,
                        completedBanisToday = (doc.get("completedBanisToday") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        quizScore = doc.getLong("quizScore")?.toInt() ?: 0,
                        badges = (doc.get("badges") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        avatarMascotState = "",
                        profilePhotoUrl = doc.getString("profilePhotoUrl"),
                        lastActiveTimestamp = doc.getLong("lastActiveTimestamp") ?: 0L
                    )
                }.sortedByDescending { it.xp }

                val totalXp = if (members.isNotEmpty()) members.sumOf { it.xp } else (groupDoc.getLong("groupTotalXp")?.toInt() ?: 0)
                val totalStreak = if (members.isNotEmpty()) members.maxOf { it.currentStreak } else (groupDoc.getLong("groupTotalStreak")?.toInt() ?: 0)

                FamilyGroup(
                    groupId = groupId,
                    groupName = groupName,
                    inviteCode = inviteCode,
                    adminUserId = adminId.ifEmpty { members.firstOrNull()?.userId ?: "" },
                    members = members,
                    groupTotalStreak = totalStreak,
                    groupTotalXp = totalXp
                )
            }
            groups.sortedByDescending { it.groupTotalXp }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun updateGroupName(groupId: String, newName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val map = hashMapOf<String, Any>("groupName" to newName.trim())
            firestore.collection("family_groups").document(groupId)
                .set(map, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun syncUserProgress(groupId: String, progress: FamilyMemberProgress): Boolean = withContext(Dispatchers.IO) {
        try {
            val memberData = hashMapOf(
                "userId" to progress.userId,
                "username" to progress.username.lowercase().trim(),
                "displayName" to progress.displayName,
                "xp" to progress.xp,
                "level" to progress.level,
                "currentStreak" to progress.currentStreak,
                "bestStreak" to progress.bestStreak,
                "weeklyStreak" to progress.weeklyStreak,
                "monthlyStreak" to progress.monthlyStreak,
                "totalRecitationMinutes" to progress.totalRecitationMinutes,
                "completedBanisCount" to progress.completedBanisCount,
                "completedBanisToday" to progress.completedBanisToday,
                "quizScore" to progress.quizScore,
                "badges" to progress.badges,
                "profilePhotoUrl" to progress.profilePhotoUrl,
                "lastActiveTimestamp" to System.currentTimeMillis()
            )

            // Update user in global users collection
            firestore.collection("users").document(progress.userId)
                .set(memberData, SetOptions.merge())
                .await()

            // Update in family group subcollection if group provided
            if (groupId.isNotBlank()) {
                firestore.collection("family_groups")
                    .document(groupId)
                    .collection("members")
                    .document(progress.userId)
                    .set(memberData, SetOptions.merge())
                    .await()
            }

            // If user is authenticated, clean up old anonymous "user_me" document from group
            if (progress.userId != "user_me") {
                try {
                    if (groupId.isNotBlank()) {
                        firestore.collection("family_groups")
                            .document(groupId)
                            .collection("members")
                            .document("user_me")
                            .delete()
                    }
                    firestore.collection("users").document("user_me").delete()
                } catch (ignored: Exception) {}
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun createFamilyGroup(groupName: String, creator: FamilyMemberProgress): FamilyGroup = withContext(Dispatchers.IO) {
        val newGroupId = "group_${System.currentTimeMillis()}"
        val inviteCode = "NK" + ((1000..9999).random())
        val groupData = hashMapOf(
            "groupId" to newGroupId,
            "groupName" to groupName,
            "inviteCode" to inviteCode,
            "adminUserId" to creator.userId,
            "groupTotalStreak" to creator.currentStreak,
            "groupTotalXp" to creator.xp,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("family_groups").document(newGroupId).set(groupData).await()
        syncUserProgress(newGroupId, creator)

        FamilyGroup(
            groupId = newGroupId,
            groupName = groupName,
            inviteCode = inviteCode,
            adminUserId = creator.userId,
            members = listOf(creator),
            groupTotalStreak = creator.currentStreak,
            groupTotalXp = creator.xp
        )
    }

    override suspend fun joinFamilyGroup(groupId: String, member: FamilyMemberProgress): Boolean = withContext(Dispatchers.IO) {
        try {
            val groupDoc = firestore.collection("family_groups").document(groupId).get().await()
            if (!groupDoc.exists()) return@withContext false
            syncUserProgress(groupId, member)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun joinGroupByCode(inviteCode: String, member: FamilyMemberProgress): Boolean = withContext(Dispatchers.IO) {
        val group = getFamilyGroupByCode(inviteCode) ?: return@withContext false
        joinFamilyGroup(group.groupId, member)
    }

    override suspend fun leaveFamilyGroup(groupId: String, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firestore.collection("family_groups")
                .document(groupId)
                .collection("members")
                .document(userId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun findUserByUsername(username: String): FamilyMemberProgress? = withContext(Dispatchers.IO) {
        try {
            val clean = username.lowercase().trim()
            val query = firestore.collection("users")
                .whereEqualTo("username", clean)
                .limit(1)
                .get()
                .await()

            val doc = query.documents.firstOrNull() ?: return@withContext null
            FamilyMemberProgress(
                userId = doc.getString("userId") ?: doc.id,
                username = doc.getString("username") ?: "",
                displayName = doc.getString("displayName") ?: "Gurmukh",
                xp = doc.getLong("xp")?.toInt() ?: 0,
                level = doc.getLong("level")?.toInt() ?: 1,
                currentStreak = doc.getLong("currentStreak")?.toInt() ?: 0,
                bestStreak = doc.getLong("bestStreak")?.toInt() ?: 0,
                weeklyStreak = doc.getLong("weeklyStreak")?.toInt() ?: 0,
                monthlyStreak = doc.getLong("monthlyStreak")?.toInt() ?: 0,
                totalRecitationMinutes = doc.getLong("totalRecitationMinutes")?.toInt() ?: 0,
                completedBanisCount = doc.getLong("completedBanisCount")?.toInt() ?: 0,
                completedBanisToday = (doc.get("completedBanisToday") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                quizScore = doc.getLong("quizScore")?.toInt() ?: 0,
                badges = (doc.get("badges") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                avatarMascotState = doc.getString("avatarMascotState") ?: "",
                profilePhotoUrl = doc.getString("profilePhotoUrl"),
                lastActiveTimestamp = doc.getLong("lastActiveTimestamp") ?: 0L
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun addMemberByUsername(groupId: String, username: String): Boolean = withContext(Dispatchers.IO) {
        val user = findUserByUsername(username) ?: return@withContext false
        joinFamilyGroup(groupId, user)
    }

    override suspend fun recordXpTransaction(item: XpHistoryItem): Boolean = withContext(Dispatchers.IO) {
        try {
            if (item.userId.isBlank()) return@withContext false
            val docId = item.id.ifBlank { "xp_${System.currentTimeMillis()}_${(100..999).random()}" }
            val map = hashMapOf(
                "id" to docId,
                "userId" to item.userId,
                "title" to item.title,
                "category" to item.category,
                "xpAmount" to item.xpAmount,
                "timestamp" to if (item.timestamp > 0) item.timestamp else System.currentTimeMillis(),
                "formattedDate" to item.formattedDate
            )
            firestore.collection("users")
                .document(item.userId)
                .collection("xp_history")
                .document(docId)
                .set(map, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getXpHistory(userId: String): List<XpHistoryItem> = withContext(Dispatchers.IO) {
        try {
            if (userId.isBlank()) return@withContext emptyList()
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("xp_history")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                XpHistoryItem(
                    id = doc.getString("id") ?: doc.id,
                    userId = doc.getString("userId") ?: userId,
                    title = doc.getString("title") ?: "Gurbani Paath",
                    category = doc.getString("category") ?: "Bani",
                    xpAmount = doc.getLong("xpAmount")?.toInt() ?: 0,
                    timestamp = doc.getLong("timestamp") ?: 0L,
                    formattedDate = doc.getString("formattedDate") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
