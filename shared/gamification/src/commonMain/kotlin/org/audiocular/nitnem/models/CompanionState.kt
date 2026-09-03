package org.audiocular.nitnem.models

data class CompanionEnvironment(
    val flowers: Int = 0,
    val stars: Int = 0,
    val lotusBloom: Float = 0f
)

data class CompanionState(
    val stage: Int = 0,
    val aura: Float = 0f,
    val environment: CompanionEnvironment = CompanionEnvironment()
)
