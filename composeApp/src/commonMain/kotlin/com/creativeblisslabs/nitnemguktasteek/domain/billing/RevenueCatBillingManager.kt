package com.creativeblisslabs.nitnemguktasteek.domain.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesException
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.ktx.awaitRestore
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.PurchasesErrorCode

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

 fun initialize(
        debug: Boolean = false
    ) {
        if (initialized) {
            return
        }
        initialized = true

        // Fetches customer info and synchronizes entitlements via awaitCustomerInfo()
        refreshCustomerInfo()
    }

    private fun processCustomerInfo(customerInfo: CustomerInfo) {
        val activeEntitlements = customerInfo.entitlements.active.keys
        val purchasedProductIds = customerInfo.allPurchasedProductIdentifiers.toSet()

        billingManager.updateEntitlements(
            activeEntitlementIds = activeEntitlements,
            purchasedProductIds = purchasedProductIds
        )
    }

    fun showPaywall() {
        _shouldShowPaywall.update { true }
    }

    fun dismissPaywall() {
        _shouldShowPaywall.update { false }
    }

    fun refreshCustomerInfo() {
        scope.launch {
            try {
                val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
                processCustomerInfo(customerInfo)
            } catch (e: PurchasesException) {
                billingManager.setError(e.error.message)
            } catch (e: Exception) {
                billingManager.setError(e.message ?: "Failed to refresh customer info")
            }
        }
    }

    fun restorePurchases() {
        billingManager.setPurchaseInProgress(true)
        scope.launch {
            try {
                val customerInfo = Purchases.sharedInstance.awaitRestore()
                billingManager.setPurchaseInProgress(false)
                processCustomerInfo(customerInfo)
            } catch (e: PurchasesException) {
                billingManager.setPurchaseInProgress(false)
                billingManager.setError(e.error.message)
            } catch (e: Exception) {
                billingManager.setPurchaseInProgress(false)
                billingManager.setError(e.message ?: "Restore failed")
            }
        }
    }

    fun purchase(plan: InAppProductPlan) {
        if (plan.isFreeTier) return

        billingManager.setPurchaseInProgress(true)

        scope.launch {
            try {
                val offerings = Purchases.sharedInstance.awaitOfferings()
                val packageToPurchase = offerings.current?.availablePackages?.find {
                    it.storeProduct.id == plan.id || it.identifier == plan.id
                }

                if (packageToPurchase != null) {
                    val purchaseResult = Purchases.sharedInstance.awaitPurchase(packageToPurchase)
                    billingManager.setPurchaseInProgress(false)
                    processCustomerInfo(purchaseResult.customerInfo)
                    dismissPaywall()
                } else {
                    billingManager.setPurchaseInProgress(false)
                    billingManager.setError("Product not found in offerings.")
                }
            } catch (e: PurchasesException) {
                billingManager.setPurchaseInProgress(false)
                if (e.error.code != PurchasesErrorCode.PurchaseCancelledError) {
                    billingManager.setError(e.error.message)
                }
            } catch (e: Exception) {
                billingManager.setPurchaseInProgress(false)
                billingManager.setError(e.message ?: "Purchase failed")
            }
        }
    }

    companion object {
        val instance by lazy {
            RevenueCatBillingManager()
        }
    }
}