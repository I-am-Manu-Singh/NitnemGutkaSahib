package com.creativeblisslabs.nitnemguktasteek.domain.billing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * RevenueCat integration bridge for Nitnem Gutka Sahib.
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
        refreshCustomerInfo()
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