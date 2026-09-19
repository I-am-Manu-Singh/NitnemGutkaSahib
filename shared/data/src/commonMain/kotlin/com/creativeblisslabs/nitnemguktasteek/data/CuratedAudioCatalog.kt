package com.creativeblisslabs.nitnemguktasteek.data

import com.creativeblisslabs.nitnemguktasteek.models.AudioTrack

/**
 * Dynamic Curated Audio Catalog delegating to RemoteAssetCatalog (backed by Cloud Firestore).
 */
object CuratedAudioCatalog {

    fun getTracksForBani(baniId: String): List<AudioTrack> {
        return RemoteAssetCatalog.getTracksForBani(baniId).map { it.toAudioTrack() }
    }

    fun getAllTracks(): List<AudioTrack> {
        return RemoteAssetCatalog.currentAssets.map { it.toAudioTrack() }
    }
}