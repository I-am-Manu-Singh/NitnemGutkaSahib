package org.audiocular.nitnem.gamification

import org.audiocular.nitnem.models.CompanionState

class CompanionProgression {
    fun nurture(current: CompanionState, completedToday: Int): CompanionState {
        val bloom = (current.environment.lotusBloom + completedToday * 0.08f).coerceAtMost(1f)
        return current.copy(
            stage = (current.stage + completedToday / 2).coerceAtMost(7),
            aura = (current.aura + completedToday * 0.06f).coerceAtMost(1f),
            environment = current.environment.copy(
                flowers = current.environment.flowers + completedToday,
                stars = current.environment.stars + completedToday * 2,
                lotusBloom = bloom
            )
        )
    }
}
