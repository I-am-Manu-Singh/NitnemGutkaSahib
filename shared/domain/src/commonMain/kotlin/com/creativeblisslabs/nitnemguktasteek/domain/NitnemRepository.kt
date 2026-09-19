package com.creativeblisslabs.nitnemguktasteek.domain

import kotlinx.coroutines.flow.Flow
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.HomeSnapshot
import com.creativeblisslabs.nitnemguktasteek.models.ScriptMode

interface NitnemRepository {

    fun observeHome():
        Flow<HomeSnapshot>

    fun observeBanis():
        Flow<List<Bani>>

    suspend fun markComplete(
        bani: Bani
    )

    suspend fun updateScriptMode(
        mode: ScriptMode
    )
}

class ObserveHomeUseCase(

    private val repository:
        NitnemRepository
) {

    operator fun invoke():
        Flow<HomeSnapshot> {

        return repository.observeHome()
    }
}

class ObserveBanisUseCase(

    private val repository:
        NitnemRepository
) {

    operator fun invoke():
        Flow<List<Bani>> {

        return repository.observeBanis()
    }
}

class CompleteBaniUseCase(

    private val repository:
        NitnemRepository
) {
suspend operator fun invoke(
    bani: Bani
) {
    repository.markComplete(bani)
}
}