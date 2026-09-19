package com.creativeblisslabs.nitnemguktasteek.domain.billing

import com.creativeblisslabs.nitnemguktasteek.BuildConfig

actual object RevenueCatConfig {

    actual val apiKey: String
        get() =
            BuildConfig.REVENUECAT_GOOGLE_API_KEY
}