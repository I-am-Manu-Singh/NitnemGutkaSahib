package org.audiocular.nitnem.domain.social

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberFamilyLeaderboardRepository(): FamilyLeaderboardRepository? {
    return remember {
        FirestoreFamilyLeaderboardRepository()
    }
}
