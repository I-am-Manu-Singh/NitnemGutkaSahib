package com.creativeblisslabs.nitnemguktasteek.domain.billing

actual object RevenueCatConfig {
    actual val apiKey: String
        get() = System.getenv("REVENUECAT_GOOGLE_API_KEY")
            ?: System.getProperty("REVENUECAT_GOOGLE_API_KEY")
            ?: ""
}
