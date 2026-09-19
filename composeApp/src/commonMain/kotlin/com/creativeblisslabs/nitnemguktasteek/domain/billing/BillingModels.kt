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

    const val SUPPORTER_49 = "supporter_49"

    const val FOUNDING_SUPPORTER_49 =
        "founding_supporter_49"

    const val PREMIUM_99 = "premium_99"

    const val LIFETIME_199 = "lifetime_199"
}

/**
 * Product displayed in the billing UI.
 *
 * Prices below are fallback/display values only.
 * Production UI should prefer localized prices returned
 * by RevenueCat / Google Play.
 */
data class InAppProductPlan(

    val id: String,

    /**
     * RevenueCat entitlement granted by this product.
     */
    val entitlement: String,

    val title: String,

    val formattedPriceInr: String,

    val formattedPriceUsd: String,

    val badge: String? = null,

    val isLaunchOffer: Boolean = false,

    val description: String,

    val features: List<String>
)

/**
 * Default product configuration used before RevenueCat
 * offerings are loaded.
 */
val DEFAULT_APP_PLANS = listOf(

    InAppProductPlan(
        id = BillingProducts.SUPPORTER_49,
        entitlement = BillingEntitlements.SUPPORTER,

        title = "Seva Supporter",

        formattedPriceInr = "₹49",
        formattedPriceUsd = "$1.99",

        badge = "❤️ Seva",

        description =
            "Support the continued development of Nitnem Gutka Sahib " +
            "and help keep the app free and ad-free.",

        features = listOf(
            "Seva Supporter badge",
            "2 additional spiritual themes",
            "Remove voluntary support prompts",
            "Help support audio and data hosting"
        )
    ),

    InAppProductPlan(
        id = BillingProducts.FOUNDING_SUPPORTER_49,
        entitlement = BillingEntitlements.PREMIUM,

        title = "Early Supporter",

        formattedPriceInr = "₹49",
        formattedPriceUsd = "$1.99",

        badge = "🚀 Launch Offer",
        isLaunchOffer = true,

        description =
            "A special launch price for early supporters. " +
            "Get Premium permanently with a one-time purchase.",

        features = listOf(
            "Lifetime Premium access",
            "Full-screen immersive reading mode",
            "Ultra-minimalist black & white AOD mode",
            "8 exclusive AMOLED & spiritual themes",
            "Advanced Gurbani search",
            "First-letter and transliteration search",
            "Teeka-aware search",
            "Future Premium features included"
        )
    ),

    InAppProductPlan(
        id = BillingProducts.PREMIUM_99,
        entitlement = BillingEntitlements.PREMIUM,

        title = "Gurbani Premium",

        formattedPriceInr = "₹99",
        formattedPriceUsd = "$3.99",

        badge = "⭐ Popular",

        description =
            "Unlock advanced reading, search, audio, " +
            "customization and convenience features.",

        features = listOf(
            "Full-screen immersive reading mode",
            "Ultra-minimalist black & white AOD mode",
            "8 exclusive AMOLED & spiritual themes",
            "Advanced Gurbani search",
            "Sleep timer",
            "Custom audio controls",
            "Supporter tier benefits",
            "Future Premium features included"
        )
    ),

    InAppProductPlan(
        id = BillingProducts.LIFETIME_199,
        entitlement = BillingEntitlements.PREMIUM,

        title = "Lifetime Patron",

        formattedPriceInr = "₹199",
        formattedPriceUsd = "$7.99",

        badge = "🪯 Lifetime",

        description =
            "One-time purchase. Keep all current and future " +
            "Premium features permanently.",

        features = listOf(
            "Every current Premium feature",
            "All future Premium features",
            "Lifetime Patron badge",
            "Support ongoing Gurmat app development",
            "Priority developer support"
        )
    )
)