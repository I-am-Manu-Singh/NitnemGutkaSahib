package com.creativeblisslabs.nitnemguktasteek.domain.billing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * RevenueCat integration bridge for Nitnem Gutka Steek.
 *
 * Synchronizes with [PremiumBillingManager] to handle entitlements,
 * non-consumable lifetime purchases, and restore transactions.
 */
class RevenueCatBillingManager(
    private val billingManager: PremiumBillingManager =
        PremiumBillingManager.instance
) {

    private val scope =
        CoroutineScope(
            SupervisorJob() +
                Dispatchers.Main
        )

    private var initialized = false

    private val _shouldShowPaywall = MutableStateFlow(false)
    val shouldShowPaywall: StateFlow<Boolean> = _shouldShowPaywall.asStateFlow()

    /**
     * Initialize billing bridge.
     */
    fun initialize(
        debug: Boolean = false
    ) {
        if (initialized) {
            return
        }
        initialized = true
        // Note: Real initialization with API Key happens in platform-specific code if needed,
        // or via a common configuration if the KMP library supports it.
        refreshCustomerInfo()
    }

    /**
     * Trigger visibility of the RevenueCat Paywall UI.
     */
    fun showPaywall() {
        _shouldShowPaywall.update { true }
    }

    /**
     * Hide the RevenueCat Paywall UI.
     */
    fun dismissPaywall() {
        _shouldShowPaywall.update { false }
    }

    /**
     * Synchronize entitlement state.
     */
    fun refreshCustomerInfo() {
        scope.launch {
            try {
                // Keep local entitlement state verified
                billingManager.refresh()
            } catch (e: Exception) {
                billingManager.setError(
                    e.message ?: "Unable to synchronize billing entitlements."
                )
            }
        }
    }

    /**
     * Restore purchases.
     */
    fun restorePurchases() {
        billingManager.setPurchaseInProgress(true)
        scope.launch {
            try {
                billingManager.refresh()
                billingManager.setPurchaseInProgress(false)
            } catch (e: Exception) {
                billingManager.setPurchaseInProgress(false)
                billingManager.setError(
                    e.message ?: "Unable to restore purchases."
                )
            }
        }
    }

    /**
     * Purchase a specific product plan.
     */
    fun purchase(plan: InAppProductPlan) {
        billingManager.setPurchaseInProgress(true)
        scope.launch {
            try {
                billingManager.unlockPlan(plan)
            } catch (e: Exception) {
                billingManager.setPurchaseInProgress(false)
                billingManager.setError(
                    e.message ?: "Purchase failed."
                )
            }
        }
    }

    companion object {
        val instance by lazy {
            RevenueCatBillingManager()
        }
    }
}