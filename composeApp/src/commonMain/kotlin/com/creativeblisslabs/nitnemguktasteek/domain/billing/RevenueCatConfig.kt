package com.creativeblisslabs.nitnemguktasteek.domain.billing

/**
 * Platform-specific RevenueCat configuration.
 *
 * Android returns the Google Play public SDK key.
 * iOS can later return the App Store public SDK key.
 */
expect object RevenueCatConfig {

    val apiKey: String
}