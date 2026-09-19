package com.creativeblisslabs.nitnemguktasteek.data.bookmarks

import android.content.Context
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.bookmarksDataStore by
    preferencesDataStore(
        name = "nitnem_bookmarks"
    )

class BookmarksRepository(
    private val context: Context
) {

    companion object {

        private val BOOKMARKS =
            stringSetPreferencesKey(
                "bookmarks"
            )
    }

    val bookmarksFlow:
        Flow<Set<String>> =

        context.bookmarksDataStore
            .data
            .map {

                it[BOOKMARKS]
                    ?: emptySet()
            }

    suspend fun toggleBookmark(
        lineId: String
    ) {

        context.bookmarksDataStore.edit {

            val current =
                it[BOOKMARKS]
                    ?.toMutableSet()
                    ?: mutableSetOf()

            if (
                current.contains(lineId)
            ) {

                current.remove(lineId)

            } else {

                current.add(lineId)
            }

            it[BOOKMARKS] = current
        }
    }

    suspend fun clearBookmarks() {

        context.bookmarksDataStore.edit {

            it[BOOKMARKS] =
                emptySet()
        }
    }
}