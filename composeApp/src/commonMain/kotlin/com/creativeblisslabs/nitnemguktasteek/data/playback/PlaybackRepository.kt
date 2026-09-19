package com.creativeblisslabs.nitnemguktasteek.data.playback

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.playbackDataStore by
    preferencesDataStore(
        name = "nitnem_playback"
    )

data class PlaybackPersistence(

    val baniId: String? = null,

    val position: Long = 0L,

    val speed: Float = 1f
)

class PlaybackRepository(
    private val context: Context
) {

    companion object {

        private val BANI_ID =
            stringPreferencesKey(
                "bani_id"
            )

        private val POSITION =
            longPreferencesKey(
                "position"
            )

        private val SPEED =
            floatPreferencesKey(
                "speed"
            )
    }

    val playbackFlow:
        Flow<PlaybackPersistence> =

        context.playbackDataStore
            .data
            .map { prefs ->

                PlaybackPersistence(

                    baniId =
                        prefs[BANI_ID],

                    position =
                        prefs[POSITION]
                            ?: 0L,

                    speed =
                        prefs[SPEED]
                            ?: 1f
                )
            }

    suspend fun savePlayback(

        baniId: String,

        position: Long,

        speed: Float
    ) {

        context.playbackDataStore.edit {

            it[BANI_ID] =
                baniId

            it[POSITION] =
                position

            it[SPEED] =
                speed
        }
    }

    suspend fun clearPlayback() {

        context.playbackDataStore.edit {

            it.clear()
        }
    }
}