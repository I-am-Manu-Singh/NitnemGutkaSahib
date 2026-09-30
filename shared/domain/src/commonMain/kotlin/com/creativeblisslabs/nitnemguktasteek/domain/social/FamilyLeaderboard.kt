package com.creativeblisslabs.nitnemguktasteek.domain.social

import kotlinx.serialization.Serializable

@Serializable
data class FamilyMemberProgress(
    val userId: String,
    val username: String = "",
    val displayName: String,
    val xp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalRecitationMinutes: Int = 0,
    val completedBanisCount: Int = 0,
    val completedBanisToday: List<String> = emptyList(),
    val weeklyStreak: Int = 0,
    val monthlyStreak: Int = 0,
    val quizScore: Int = 0,
    val badges: List<String> = emptyList(),
    val avatarMascotState: String = "",
    val profilePhotoUrl: String? = null,
    val lastActiveTimestamp: Long = 0L
)

@Serializable
data class FamilyGroup(
    val groupId: String,
    val groupName: String,
    val inviteCode: String = "",
    val adminUserId: String = "",
    val members: List<FamilyMemberProgress> = emptyList(),
    val groupTotalStreak: Int = 0,
    val groupTotalXp: Int = 0
)

@Serializable
data class XpHistoryItem(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "Bani", // "Bani", "Streak", "Quiz", "Practice"
    val xpAmount: Int = 0,
    val timestamp: Long = 0L,
    val formattedDate: String = ""
)

interface FamilyLeaderboardRepository {
    suspend fun getFamilyGroup(groupId: String): FamilyGroup?
    suspend fun getFamilyGroupByCode(inviteCode: String): FamilyGroup?
    suspend fun getAllFamilyGroups(): List<FamilyGroup>
    suspend fun updateGroupName(groupId: String, newName: String): Boolean
    suspend fun syncUserProgress(groupId: String, progress: FamilyMemberProgress): Boolean
    suspend fun createFamilyGroup(groupName: String, creator: FamilyMemberProgress): FamilyGroup
    suspend fun joinFamilyGroup(groupId: String, member: FamilyMemberProgress): Boolean
    suspend fun joinGroupByCode(inviteCode: String, member: FamilyMemberProgress): Boolean
    suspend fun leaveFamilyGroup(groupId: String, userId: String): Boolean
    suspend fun addMemberByUsername(groupId: String, username: String): Boolean
    suspend fun findUserByUsername(username: String): FamilyMemberProgress?
    suspend fun recordXpTransaction(item: XpHistoryItem): Boolean = true
    suspend fun getXpHistory(userId: String): List<XpHistoryItem> = emptyList()
}

class InMemoryFamilyLeaderboardRepository : FamilyLeaderboardRepository {
    private val groups = mutableMapOf<String, FamilyGroup>()

    override suspend fun getFamilyGroup(groupId: String): FamilyGroup? {
        return groups[groupId]
    }

    override suspend fun getFamilyGroupByCode(inviteCode: String): FamilyGroup? {
        val cleanCode = inviteCode.trim().uppercase()
        return groups.values.firstOrNull { it.inviteCode.equals(cleanCode, ignoreCase = true) }
    }

    override suspend fun getAllFamilyGroups(): List<FamilyGroup> {
        return groups.values.sortedByDescending { it.groupTotalXp }
    }

    override suspend fun updateGroupName(groupId: String, newName: String): Boolean {
        val group = groups[groupId] ?: return false
        groups[groupId] = group.copy(groupName = newName)
        return true
    }

    override suspend fun syncUserProgress(groupId: String, progress: FamilyMemberProgress): Boolean {
        val currentGroup = groups[groupId] ?: return false
        val updatedMembers = currentGroup.members.filterNot { it.userId == progress.userId } + progress
        groups[groupId] = currentGroup.copy(members = updatedMembers.sortedByDescending { it.currentStreak })
        return true
    }

    override suspend fun createFamilyGroup(groupName: String, creator: FamilyMemberProgress): FamilyGroup {
        val newId = "group_${System.currentTimeMillis()}"
        val inviteCode = "NK" + ((1000..9999).random())
        val group = FamilyGroup(
            groupId = newId,
            groupName = groupName,
            inviteCode = inviteCode,
            adminUserId = creator.userId,
            members = listOf(creator),
            groupTotalStreak = creator.currentStreak
        )
        groups[newId] = group
        return group
    }

    override suspend fun joinFamilyGroup(groupId: String, member: FamilyMemberProgress): Boolean {
        val currentGroup = groups[groupId] ?: return false
        if (currentGroup.members.any { it.userId == member.userId }) return true
        groups[groupId] = currentGroup.copy(members = currentGroup.members + member)
        return true
    }

    override suspend fun joinGroupByCode(inviteCode: String, member: FamilyMemberProgress): Boolean {
        val group = getFamilyGroupByCode(inviteCode) ?: return false
        return joinFamilyGroup(group.groupId, member)
    }

    override suspend fun leaveFamilyGroup(groupId: String, userId: String): Boolean {
        val group = groups[groupId] ?: return false
        val remaining = group.members.filterNot { it.userId == userId }
        groups[groupId] = group.copy(members = remaining)
        return true
    }

    override suspend fun findUserByUsername(username: String): FamilyMemberProgress? {
        val clean = username.lowercase().trim()
        return groups.values.flatMap { it.members }.firstOrNull { it.username.equals(clean, ignoreCase = true) }
    }

    override suspend fun addMemberByUsername(groupId: String, username: String): Boolean {
        val user = findUserByUsername(username) ?: return false
        return joinFamilyGroup(groupId, user)
    }
}
