package com.creativeblisslabs.nitnemguktasteek.domain.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

data class BillingUserState(
    val isSupporter: Boolean = false,
    val hasPaidPremium: Boolean = false,
    val isLifetimePatron: Boolean = false,
    val isTrialActive: Boolean = false,
    val trialDaysRemaining: Int = 0,
    val hasPremiumAccess: Boolean = false,
    val activeEntitlements: Set<String> = emptySet(),
    val availablePlans: List<InAppProductPlan> = DEFAULT_APP_PLANS,
    val isBillingInitialized: Boolean = false,
    val isPurchaseInProgress: Boolean = false,
    val errorMessage: String? = null
) {
    val isPremium: Boolean get() = hasPremiumAccess
}

class PremiumBillingManager {

    private val _billingState =
        MutableStateFlow(BillingUserState())

    val billingState: StateFlow<BillingUserState> =
        _billingState.asStateFlow()

    private var installTimeMs: Long = 0L

    companion object {
        const val TRIAL_DURATION_DAYS = 14
        private const val DAY_MS = 24L * 60L * 60L * 1000L

        val instance = PremiumBillingManager()
    }

    /**
     * Evaluate local preview trial.
     */
    fun evaluateTrial(firstInstallEpochMs: Long) {
        installTimeMs = firstInstallEpochMs
        if (firstInstallEpochMs <= 0L) {
            _billingState.update { current ->
                current.copy(
                    isTrialActive = true,
                    trialDaysRemaining = TRIAL_DURATION_DAYS,
                    hasPremiumAccess = current.hasPaidPremium || true
                )
            }
            return
        }

        val now = Clock.System.now().toEpochMilliseconds()
        val elapsedMs = now - firstInstallEpochMs
        val daysElapsed = (elapsedMs / DAY_MS).toInt()
        val daysRemaining = (TRIAL_DURATION_DAYS - daysElapsed).coerceAtLeast(0)
        val trialActive = daysRemaining > 0

        _billingState.update { current ->
            current.copy(
                isTrialActive = trialActive,
                trialDaysRemaining = daysRemaining,
                hasPremiumAccess = current.hasPaidPremium || trialActive
            )
        }
    }

    /**
     * Called whenever customer info / entitlements update.
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
                isSupporter = hasSupporter || hasPremium,
                hasPaidPremium = hasPremium,
                isLifetimePatron = isLifetime,
                hasPremiumAccess = hasPremium || current.isTrialActive,
                activeEntitlements = activeEntitlementIds,
                isBillingInitialized = true,
                errorMessage = null
            )
        }
    }

    fun setPurchaseInProgress(value: Boolean) {
        _billingState.update {
            it.copy(isPurchaseInProgress = value)
        }
    }

    fun setError(message: String?) {
        _billingState.update {
            it.copy(
                errorMessage = message,
                isPurchaseInProgress = false
            )
        }
    }

    fun refresh() {
        if (installTimeMs > 0L) {
            evaluateTrial(installTimeMs)
        }
    }

    fun unlockPlan(plan: InAppProductPlan) {
        simulatePurchase(plan)
        setPurchaseInProgress(false)
    }

    fun simulatePurchase(plan: InAppProductPlan) {
        val entitlements = _billingState.value.activeEntitlements.toMutableSet()
        entitlements += plan.entitlement

        val purchasedProducts = mutableSetOf<String>()
        if (plan.id == BillingProducts.LIFETIME_199) {
            purchasedProducts += BillingProducts.LIFETIME_199
        }

        updateEntitlements(
            activeEntitlementIds = entitlements,
            purchasedProductIds = purchasedProducts
        )
    }

    fun simulateRestore() {
        if (_billingState.value.activeEntitlements.isEmpty()) {
            updateEntitlements(
                activeEntitlementIds = setOf(
                    BillingEntitlements.SUPPORTER,
                    BillingEntitlements.PREMIUM
                )
            )
        }
    }

    fun clearSimulatedState() {
        _billingState.update { current ->
            current.copy(
                isSupporter = false,
                hasPaidPremium = false,
                isLifetimePatron = false,
                hasPremiumAccess = current.isTrialActive,
                activeEntitlements = emptySet()
            )
        }
    }

    fun isPremiumUser(): Boolean = _billingState.value.hasPremiumAccess
    fun isPaidPremiumUser(): Boolean = _billingState.value.hasPaidPremium
    fun isSupporterUser(): Boolean = _billingState.value.isSupporter
    fun isLifetimePatron(): Boolean = _billingState.value.isLifetimePatron
    fun isTrialUser(): Boolean = _billingState.value.isTrialActive
}