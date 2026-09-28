package com.creativeblisslabs.nitnemguktasteek.domain.billing

/**
 * RevenueCat entitlement identifiers.
 *
 * These MUST exactly match the entitlement identifiers
 * configured in RevenueCat.
 */
object BillingEntitlements {

    const val SUPPORTER = "nitnem_gutka_sahib_supporter"

    const val PREMIUM = "nitnem_gutka_sahib_premium"
}

/**
 * Application-level representation of the user's paid tier.
 */
enum class PremiumTier(
    val entitlementId: String
) {
    Freemium(""),
    SevaSupporter(BillingEntitlements.SUPPORTER),
    SevaPatron(BillingEntitlements.PREMIUM)
}

/**
 * Google Play / RevenueCat product IDs.
 */
object BillingProducts {
    const val FREEMIUM_00 = "freemium_00"
    const val SEVA_SUPPORTER_49 = "seva_supporter_49"
    const val SEVA_PATRON_99 = "seva_patron_99"
    const val LIFETIME_PATRON_199 = "lifetime_patron_199"
}

/**
 * Product displayed in the billing UI.
 */
data class InAppProductPlan(
    val id: String,
    val entitlement: String,
    val title: String,
    val formattedPriceInr: String,
    val formattedPriceUsd: String,
    val badge: String? = null,
    val isLaunchOffer: Boolean = false,
    val isFreeTier: Boolean = false,
    val description: String,
    val features: List<String>
)

val FREE_TIER_PLAN = InAppProductPlan(
    id = BillingProducts.FREEMIUM_00,
    entitlement = "",
    title = "Freemium",
    formattedPriceInr = "₹0",
    formattedPriceUsd = "$0",
    badge = "Active by Default",
    isFreeTier = true,
    description = "100% free and ad-free forever. Authentic Gurmukhi scripture, steek translations, audio sync, and offline reading.",
    features = listOf(
        "100% Free & Ad-Free Forever",
        "All 40+ Gurbani Banis & Steek Translations",
        "Synchronized Audio Playback & Offline Mode",
        "Daily Streaks, Monthly Heatmap & XP History",
        "5 Standard Themes & Core Typography"
    )
)

val DEFAULT_APP_PLANS = listOf(
    FREE_TIER_PLAN,

    InAppProductPlan(
        id = BillingProducts.SEVA_SUPPORTER_49,
        entitlement = BillingEntitlements.SUPPORTER,
        title = "Seva Supporter",
        formattedPriceInr = "₹49",
        formattedPriceUsd = "$0.99",
        badge = "Monthly Charged",
        description = "Support ongoing server hosting, audio CDN costs, and continuous app development with a small recurring seva.",
        features = listOf(
            "Golden Seva Supporter Badge",
            "4 Exclusive Spiritual Themes",
            "Unlocks Supporter Calligraphy (Anmol Lipi, Lora)",
            "Direct Contribution to Server & Audio Hosting"
        )
    ),

    InAppProductPlan(
        id = BillingProducts.SEVA_PATRON_99,
        entitlement = BillingEntitlements.PREMIUM,
        title = "Seva Patron",
        formattedPriceInr = "₹99",
        formattedPriceUsd = "$1.99",
        badge = "Yearly Charged",
        isLaunchOffer = true,
        description = "Unlock the ultimate Gurbani focus experience with advanced typography, ambient effects, and full-screen reading.",
        features = listOf(
            "Full-Screen Immersive Reading Mode",
            "Ultra-Minimalist Black & White AOD Mode",
            "All 10 Premium Calligraphy & Script Fonts",
            "Divine Ambient Particle Effects & Auras",
            "Advanced Verse Search & Sleep Timer"
        )
    ),

    InAppProductPlan(
        id = BillingProducts.LIFETIME_PATRON_199,
        entitlement = BillingEntitlements.PREMIUM,
        title = "Lifetime Patron",
        formattedPriceInr = "₹199",
        formattedPriceUsd = "$3.99",
        badge = "One Time Charged, Lifetime",
        description = "One-time contribution for lifetime full access. Keep all current and all future Premium features forever.",
        features = listOf(
            "Permanent Lifetime Access to All Features",
            "All Future Premium Features Included",
            "Lifetime Patron Golden Shield Badge",
            "Create & Administer Sangat Family Groups",
            "Direct Priority Support & Seva Recognition"
        )
    )
)