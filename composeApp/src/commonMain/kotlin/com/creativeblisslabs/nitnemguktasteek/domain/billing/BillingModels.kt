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
    Free(""),
    Supporter(BillingEntitlements.SUPPORTER),
    Premium(BillingEntitlements.PREMIUM)
}

/**
 * Google Play / RevenueCat product IDs.
 */
object BillingProducts {
    const val FREE_TIER = "free_forever"
    const val SUPPORTER_MONTHLY = "supporter_monthly"
    const val PREMIUM_ANNUAL = "premium_annual"
    const val LIFETIME_PATRON = "lifetime_patron"

    // Legacy backwards compatibility constants
    const val SUPPORTER_49 = "supporter_49"
    const val FOUNDING_SUPPORTER_49 = "founding_supporter_49"
    const val PREMIUM_99 = "premium_99"
    const val LIFETIME_199 = "lifetime_199"
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
    id = BillingProducts.FREE_TIER,
    entitlement = "",
    title = "Keep Free Forever",
    formattedPriceInr = "Free",
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
        id = BillingProducts.SUPPORTER_MONTHLY,
        entitlement = BillingEntitlements.SUPPORTER,
        title = "Seva Supporter",
        formattedPriceInr = "₹49",
        formattedPriceUsd = "$0.99",
        badge = "Seva",
        description = "Support ongoing server hosting, audio CDN costs, and continuous app development with a small recurring seva.",
        features = listOf(
            "Golden Seva Supporter Badge",
            "4 Exclusive Spiritual Themes",
            "Unlocks Supporter Calligraphy (Anmol Lipi, Lora)",
            "Direct Contribution to Server & Audio Hosting"
        )
    ),

    InAppProductPlan(
        id = BillingProducts.PREMIUM_ANNUAL,
        entitlement = BillingEntitlements.PREMIUM,
        title = "Seva Patron",
        formattedPriceInr = "₹299",
        formattedPriceUsd = "$4.99",
        badge = "Most Popular • 50% Off",
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
        id = BillingProducts.LIFETIME_PATRON,
        entitlement = BillingEntitlements.PREMIUM,
        title = "Lifetime Sangat Patron",
        formattedPriceInr = "₹599",
        formattedPriceUsd = "$9.99",
        badge = "Lifetime • Best Value",
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