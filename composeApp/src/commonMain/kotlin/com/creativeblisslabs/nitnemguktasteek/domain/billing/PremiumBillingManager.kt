package com.creativeblisslabs.nitnemguktasteek.domain.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

data class BillingUserState(

    val isSupporter: Boolean = false,

    /**
     * True only when RevenueCat reports the premium entitlement.
     */
    val hasPaidPremium: Boolean = false,

    /**
     * True when the lifetime product was purchased.
     */
    val isLifetimePatron: Boolean = false,

    /**
     * Local preview trial.
     */
    val isTrialActive: Boolean = false,

    val trialDaysRemaining: Int = 0,

    /**
     * Actual feature access.
     *
     * Paid Premium OR active trial.
     */
    val hasPremiumAccess: Boolean = false,

    val isPremium: Boolean get() = hasPremiumAccess,

    /**
     * Active RevenueCat entitlement IDs.
     */
    val activeEntitlements: Set<String> = emptySet(),

    /**
     * Product plans shown by the application.
     */
    val availablePlans: List<InAppProductPlan> =
        DEFAULT_APP_PLANS,

    /**
     * Whether RevenueCat has successfully loaded
     * customer information.
     */
    val isBillingInitialized: Boolean = false,

    /**
     * Whether a purchase is currently running.
     */
    val isPurchaseInProgress: Boolean = false,

    /**
     * Human-readable billing error.
     */
    val errorMessage: String? = null
)

class PremiumBillingManager {

    private val _billingState =
        MutableStateFlow(BillingUserState())

    val billingState: StateFlow<BillingUserState> =
        _billingState.asStateFlow()

    companion object {

        const val TRIAL_DURATION_DAYS = 14

        private const val DAY_MS =
            24L * 60L * 60L * 1000L

        val instance =
            PremiumBillingManager()
    }

    /**
     * Evaluate local preview trial.
     *
     * IMPORTANT:
     * This is only a convenience preview.
     * Paid entitlements MUST come from RevenueCat.
     */
    fun evaluateTrial(
        firstInstallEpochMs: Long
    ) {

        if (firstInstallEpochMs <= 0L) {

            _billingState.update { current ->

                current.copy(
                    isTrialActive = true,
                    trialDaysRemaining =
                        TRIAL_DURATION_DAYS,

                    hasPremiumAccess =
                        current.hasPaidPremium || true
                )
            }

            return
        }

        val nowMs =
            Clock.System.now()
                .toEpochMilliseconds()

        val elapsedMs =
            (nowMs - firstInstallEpochMs)
                .coerceAtLeast(0L)

        val elapsedDays =
            (elapsedMs / DAY_MS).toInt()

        val daysRemaining =
            (
                TRIAL_DURATION_DAYS -
                    elapsedDays
            ).coerceAtLeast(0)

        val trialActive =
            daysRemaining > 0

        _billingState.update { current ->

            current.copy(

                isTrialActive =
                    trialActive,

                trialDaysRemaining =
                    daysRemaining,

                hasPremiumAccess =
                    current.hasPaidPremium ||
                        trialActive
            )
        }
    }

    /**
     * Called by RevenueCatBillingManager whenever
     * CustomerInfo changes.
     */
    fun updateEntitlements(
        activeEntitlementIds: Set<String>,
        purchasedProductIds: Set<String> = emptySet()
    ) {

        val hasSupporter = BillingEntitlements.SUPPORTER in activeEntitlementIds
        val hasPremium = BillingEntitlements.PREMIUM in activeEntitlementIds
        val isLifetime = BillingProducts.LIFETIME_199 in purchasedProductIds

        _billingState.update { current ->

            current.copy(

                isSupporter =
                    hasSupporter || hasPremium,

                hasPaidPremium =
                    hasPremium,

                isLifetimePatron =
                    isLifetime,

                hasPremiumAccess =
                    hasPremium ||
                        current.isTrialActive,

                activeEntitlements =
                    activeEntitlementIds,

                isBillingInitialized =
                    true,

                errorMessage = null
            )
        }
    }

    fun setPurchaseInProgress(
        value: Boolean
    ) {
        _billingState.update {
            it.copy(
                isPurchaseInProgress = value
            )
        }
    }

    fun setError(
        message: String?
    ) {
        _billingState.update {
            it.copy(
                errorMessage = message,
                isPurchaseInProgress = false
            )
        }
    }

    /**
     * Refresh / synchronize current entitlements.
     */
    fun refresh() {
        // Keeps state synchronized with active preferences / entitlements
        updateTrialState()
    }

    /**
     * Unlock entitlement associated with the plan.
     */
    fun unlockPlan(plan: InAppProductPlan) {
        simulatePurchase(plan)
        setPurchaseInProgress(false)
    }

    /**
     * Development-only simulated purchase.
     */
    fun simulatePurchase(
        plan: InAppProductPlan
    ) {

        val entitlements =
            _billingState.value
                .activeEntitlements
                .toMutableSet()

        entitlements += plan.entitlement

        val purchasedProducts =
            mutableSetOf<String>()

        if (
            plan.id ==
            BillingProducts.LIFETIME_199
        ) {
            purchasedProducts +=
                BillingProducts.LIFETIME_199
        }

        updateEntitlements(
            activeEntitlementIds =
                entitlements,

            purchasedProductIds =
                purchasedProducts
        )
    }

    /**
     * Development-only simulated restore.
     */
    fun simulateRestore() {

        if (
            _billingState.value
                .activeEntitlements
                .isEmpty()
        ) {

            updateEntitlements(
                activeEntitlementIds =
                    setOf(
                        BillingEntitlements.SUPPORTER,
                        BillingEntitlements.PREMIUM
                    )
            )
        }
    }

    /**
     * Development-only reset.
     */
    fun clearSimulatedState() {

        _billingState.update { current ->

            current.copy(

                isSupporter = false,

                hasPaidPremium = false,

                isLifetimePatron = false,

                hasPremiumAccess =
                    current.isTrialActive,

                activeEntitlements =
                    emptySet()
            )
        }
    }

    fun isPremiumUser(): Boolean =
        _billingState.value.hasPremiumAccess

    fun isPaidPremiumUser(): Boolean =
        _billingState.value.hasPaidPremium

    fun isSupporterUser(): Boolean =
        _billingState.value.isSupporter

    fun isLifetimePatron(): Boolean =
        _billingState.value.isLifetimePatron

    fun isTrialUser(): Boolean =
        _billingState.value.isTrialActive
}