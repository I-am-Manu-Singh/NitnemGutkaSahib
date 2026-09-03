package org.audiocular.nitnem.domain.social

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
    val quizScore: Int = 0,
    val badges: List<String> = emptyList(),
    val avatarMascotState: String = "",
    val lastActiveTimestamp: Long = 0L
)

@Serializable
data class FamilyGroup(
    val groupId: String,
    val groupName: String,
    val inviteCode: String = "",
    val members: List<FamilyMemberProgress> = emptyList(),
    val groupTotalStreak: Int = 0,
    val groupTotalXp: Int = 0
)

interface FamilyLeaderboardRepository {
    suspend fun getFamilyGroup(groupId: String): FamilyGroup?
    suspend fun getAllFamilyGroups(): List<FamilyGroup>
    suspend fun updateGroupName(groupId: String, newName: String): Boolean
    suspend fun syncUserProgress(groupId: String, progress: FamilyMemberProgress): Boolean
    suspend fun createFamilyGroup(groupName: String, creator: FamilyMemberProgress): FamilyGroup
    suspend fun joinFamilyGroup(groupId: String, member: FamilyMemberProgress): Boolean
    suspend fun addMemberByUsername(groupId: String, username: String): Boolean
    suspend fun findUserByUsername(username: String): FamilyMemberProgress?
}

class InMemoryFamilyLeaderboardRepository : FamilyLeaderboardRepository {
    private val groups = mutableMapOf<String, FamilyGroup>()

    override suspend fun getFamilyGroup(groupId: String): FamilyGroup? {
        return groups[groupId]
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
        val group = FamilyGroup(
            groupId = newId,
            groupName = groupName,
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

    override suspend fun findUserByUsername(username: String): FamilyMemberProgress? {
        val clean = username.lowercase().trim()
        return groups.values.flatMap { it.members }.firstOrNull { it.username.equals(clean, ignoreCase = true) }
    }

    override suspend fun addMemberByUsername(groupId: String, username: String): Boolean {
        val user = findUserByUsername(username) ?: return false
        return joinFamilyGroup(groupId, user)
    }
}
