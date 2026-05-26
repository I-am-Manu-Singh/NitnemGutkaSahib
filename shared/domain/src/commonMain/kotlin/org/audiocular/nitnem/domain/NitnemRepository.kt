package org.audiocular.nitnem.domain

import kotlinx.coroutines.flow.Flow
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.HomeSnapshot
import org.audiocular.nitnem.models.ScriptMode

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