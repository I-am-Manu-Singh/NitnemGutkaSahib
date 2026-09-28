package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.domain.billing.BillingProducts
import com.creativeblisslabs.nitnemguktasteek.domain.billing.InAppProductPlan
import com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager
import com.creativeblisslabs.nitnemguktasteek.utils.translate

@Composable
fun SevaTiersContent(
    displayLanguage: DisplayLanguage,
    onPurchasePlan: (InAppProductPlan) -> Unit = {},
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val billingManager = remember { PremiumBillingManager.instance }
    val billingState by billingManager.billingState.collectAsState()

    // Determine currently active plan
    val activePlanId = when {
        billingState.isLifetimePatron -> BillingProducts.LIFETIME_PATRON_199
        billingState.hasPaidPremium -> BillingProducts.SEVA_PATRON_99
        billingState.isSupporter -> BillingProducts.SEVA_SUPPORTER_49
        else -> BillingProducts.FREEMIUM_00
    }

    var selectedPlan by remember(activePlanId) {
        mutableStateOf(
            billingState.availablePlans.firstOrNull { it.id == activePlanId }
                ?: billingState.availablePlans.firstOrNull { it.isLaunchOffer }
                ?: billingState.availablePlans.first()
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── MAIN BRANDING & SEVA CONTAINER ──
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = if (isLight) Color(0xFFFFFFFF) else SurfaceColor,
            shadowElevation = if (isLight) 6.dp else 0.dp,
            border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
             Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
        .size(72.dp)
        // 1. Apply shadow first so it spreads outside the boundaries
        .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Gold.copy(alpha = 0.4f))
        // 2. Clip strictly to the circle so the background/border doesn't leak
        .clip(CircleShape)
        // 3. Add a solid background color first to block out the shadow artifacts underneath
        .background(Color.White) // Or your app's specific background color (e.g., MaterialTheme.colorScheme.surface)
        // 4. Layer the golden gradient smoothly over the solid base
        .background(
            Brush.linearGradient(
                colors = listOf(
                    Gold.copy(alpha = 0.25f),
                    Gold.copy(alpha = 0.08f)
                )
            )
        )
        // 5. Draw the border last so it cleanly sits on the edge
        .border(BorderStroke(1.5.dp, Gold.copy(alpha = 0.5f)), CircleShape)
) {
    Text(
        text = "ੴ",
        color = Gold,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    )
}

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Nitnem Steek is 100% free forever for all Sangat worldwide. Voluntary Seva contributions directly fund cloud servers, high-quality audio bandwidth, and ongoing Gurmat app features.".translate(displayLanguage),
                    fontSize = 13.5.sp,
                    color = if (isLight) Color(0xFF5D4037).copy(alpha = 0.85f) else Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                // Free Trial Banner if applicable
                if (billingState.isTrialActive && !billingState.hasPaidPremium) {
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Accent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = Accent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Free Premium Preview: ${billingState.trialDaysRemaining} days remaining".translate(displayLanguage),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Accent
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── TIER PLANS CARDS ──
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    billingState.availablePlans.forEach { plan ->
                        val isSelected = selectedPlan.id == plan.id
                        val isCurrentActivePlan = plan.id == activePlanId

                        val cardBgColor by animateColorAsState(
                            targetValue = if (isSelected) {
                                if (isLight) Gold.copy(alpha = 0.12f) else Gold.copy(alpha = 0.1f)
                            } else {
                                if (isLight) Color.White else Color(0xFF1A1714)
                            },
                            label = "cardBgColor"
                        )

                        val borderColor by animateColorAsState(
                            targetValue = if (isSelected) Gold else {
                                if (isLight) Color(0xFFE8DFD3) else Color(0xFF2E2924)
                            },
                            label = "borderColor"
                        )

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlan = plan },
                            shape = RoundedCornerShape(20.dp),
                            color = cardBgColor,
                            border = BorderStroke(width = if (isSelected) 2.dp else 1.dp, color = borderColor)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left section: Radio button + Titles/Badges
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedPlan = plan },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = Gold,
                                                unselectedColor = Color.Gray
                                            ),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = plan.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = if (isLight) Color(0xFF2B1D12) else Color.White,
                                                    maxLines = 1,
                                                    modifier = Modifier.weight(if (isCurrentActivePlan) 0.6f else 1f, fill = false)
                                                )
                                                
                                                if (isCurrentActivePlan) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = Accent.copy(alpha = 0.18f),
                                                        border = BorderStroke(0.5.dp, Accent)
                                                    ) {
                                                        Text(
                                                            text = "Active".translate(displayLanguage),
                                                            fontSize = 9.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Accent,
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            
                                            if (plan.badge != null) {
                                                Text(
                                                    text = plan.badge!!,
                                                    color = Gold,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 11.5.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    // Right section: Clean stacked pricing block
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (plan.isFreeTier) Accent.copy(alpha = 0.1f) else Gold.copy(alpha = 0.12f),
                                        border = BorderStroke(0.8.dp, if (plan.isFreeTier) Accent.copy(alpha = 0.4f) else Gold.copy(alpha = 0.4f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            if (plan.isFreeTier) {
                                                Text(
                                                    text = "Free".translate(displayLanguage),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 14.sp,
                                                    color = Accent
                                                )
                                            } else {
                                                Text(
                                                    text = plan.formattedPriceInr,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 14.sp,
                                                    color = Gold
                                                )
                                                if (!plan.formattedPriceUsd.isNullOrBlank()) {
                                                    Text(
                                                        text = plan.formattedPriceUsd!!,
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 10.5.sp,
                                                        color = if (isLight) Color(0xFF795548) else Color(0xFFB0BEC5)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                // Description text aligned neatly underneath
                                Text(
                                    text = plan.description,
                                    fontSize = 12.sp,
                                    color = if (isLight) Color(0xFF6D4C41) else Color(0xFF9E9E9E),
                                    modifier = Modifier.padding(start = 32.dp),
                                    lineHeight = 16.sp
                                )

                                // Expanded Features List when selected
                                if (isSelected) {
                                    Spacer(Modifier.height(10.dp))
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 32.dp)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        plan.features.forEach { feat ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Gold,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = feat,
                                                    fontSize = 11.5.sp,
                                                    color = if (isLight) Color(0xFF3E2723) else Color.White.copy(alpha = 0.9f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── ACTION BUTTON ──
                if (!selectedPlan.isFreeTier) {
                    Button(
                        onClick = {
                            com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.purchase(selectedPlan)
                            onDismiss?.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Gold.copy(alpha = 0.45f)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Gold,
                                            Gold.copy(alpha = 0.85f),
                                            Gold.copy(alpha = 0.95f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Support with ${selectedPlan.formattedPriceInr} (${selectedPlan.title})",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            onDismiss?.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Gold)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Enjoying Nitnem Free Forever".translate(displayLanguage),
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold,
                            maxLines = 1
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ── RESTORE PURCHASES & MAYBE LATER FOOTER ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.restorePurchases()
                            onDismiss?.invoke()
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Restore Purchases".translate(displayLanguage),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gold
                        )
                    }

                    if (onDismiss != null) {
                        Text(
                            text = "•",
                            color = if (isLight) Color.LightGray else Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        TextButton(
                            onClick = onDismiss,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Maybe Later".translate(displayLanguage),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isLight) Color.Gray else Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── LEGAL TERMS & PRIVACY LINKS (REVENUECAT & GOOGLE PLAY COMPLIANT) ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var showLegalDoc by remember { mutableStateOf<String?>(null) }

                    Text(
                        text = "Terms of Service".translate(displayLanguage),
                        fontSize = 11.sp,
                        color = if (isLight) Color(0xFF8D6E63) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clickable { showLegalDoc = "Terms of Service" }
                            .padding(4.dp)
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = if (isLight) Color.LightGray else Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Text(
                        text = "Privacy Policy".translate(displayLanguage),
                        fontSize = 11.sp,
                        color = if (isLight) Color(0xFF8D6E63) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clickable { showLegalDoc = "Privacy Policy" }
                            .padding(4.dp)
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = if (isLight) Color.LightGray else Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    Text(
                        text = "Refunds".translate(displayLanguage),
                        fontSize = 11.sp,
                        color = if (isLight) Color(0xFF8D6E63) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .clickable { showLegalDoc = "Refund Policy" }
                            .padding(4.dp)
                    )

                    showLegalDoc?.let { doc ->
                        LegalDocumentDialog(
                            docType = doc,
                            displayLanguage = displayLanguage,
                            onDismiss = { showLegalDoc = null }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevaSupporterModalSheet(
    displayLanguage: DisplayLanguage,
    onDismiss: () -> Unit,
    onPurchasePlan: (InAppProductPlan) -> Unit = {}
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Trigger RevenueCat CustomerInfo refresh upon opening
    LaunchedEffect(Unit) {
        com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.refreshCustomerInfo()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isLight) Color(0xFFFFFBF5) else Color(0xFF141210),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        SevaTiersContent(
            displayLanguage = displayLanguage,
            onPurchasePlan = onPurchasePlan,
            onDismiss = onDismiss,
            modifier = Modifier
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        )
    }
}

// package com.creativeblisslabs.nitnemguktasteek.app.ui.components

// import androidx.compose.animation.animateColorAsState
// import androidx.compose.animation.core.animateFloatAsState
// import androidx.compose.animation.core.spring
// import androidx.compose.foundation.*
// import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.shape.CircleShape
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.*
// import androidx.compose.material3.*
// import androidx.compose.runtime.*
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.draw.scale
// import androidx.compose.ui.draw.shadow
// import androidx.compose.ui.graphics.Brush
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.text.style.TextAlign
// import androidx.compose.ui.unit.dp
// import androidx.compose.ui.unit.sp
// import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
// import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
// import com.creativeblisslabs.nitnemguktasteek.domain.billing.BillingProducts
// import com.creativeblisslabs.nitnemguktasteek.domain.billing.InAppProductPlan
// import com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager
// import com.creativeblisslabs.nitnemguktasteek.utils.translate

// @Composable
// fun SevaTiersContent(
//     displayLanguage: DisplayLanguage,
//     onPurchasePlan: (InAppProductPlan) -> Unit = {},
//     onDismiss: (() -> Unit)? = null,
//     modifier: Modifier = Modifier
// ) {
//     val isLight = AppTheme.themeMode == ThemeMode.Light
//     val billingManager = remember { PremiumBillingManager.instance }
//     val billingState by billingManager.billingState.collectAsState()

//     // Determine currently active plan
//     val activePlanId = when {
//         billingState.isLifetimePatron -> BillingProducts.LIFETIME_PATRON_199
//         billingState.hasPaidPremium -> BillingProducts.SEVA_PATRON_99
//         billingState.isSupporter -> BillingProducts.SEVA_SUPPORTER_49
//         else -> BillingProducts.FREEMIUM_00
//     }

//     var selectedPlan by remember(activePlanId) {
//         mutableStateOf(
//             billingState.availablePlans.firstOrNull { it.id == activePlanId }
//                 ?: billingState.availablePlans.firstOrNull { it.isLaunchOffer }
//                 ?: billingState.availablePlans.first()
//         )
//     }

//     var showLegalDoc by remember { mutableStateOf<String?>(null) }

//     BoxWithConstraints(
//         modifier = modifier
//             .fillMaxWidth()
//             .padding(horizontal = 8.dp, vertical = 8.dp)
//     ) {
//         val isWideScreen = maxWidth >= 840.dp

//         Surface(
//             shape = RoundedCornerShape(24.dp),
//             color = if (isLight) Color(0xFFFFFFFF) else SurfaceColor,
//             shadowElevation = if (isLight) 6.dp else 0.dp,
//             border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
//             modifier = Modifier.fillMaxWidth()
//         ) {
//             if (isWideScreen) {
//                 // ── LANDSCAPE / WIDE SPLIT LAYOUT (LEFT & RIGHT) ──
//                 Row(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(24.dp),
//                     horizontalArrangement = Arrangement.spacedBy(24.dp),
//                     verticalAlignment = Alignment.CenterVertically
//                 ) {
//                     // Left Pane: Branding, Description, Trial Banner, Footers
//                     Column(
//                         modifier = Modifier
//                             .weight(1f)
//                             .fillMaxHeight(),
//                         horizontalAlignment = Alignment.CenterHorizontally,
//                         verticalArrangement = Arrangement.Center
//                     ) {
//                         BrandingHeader(isLight, displayLanguage)

//                         Spacer(Modifier.height(16.dp))

//                         // Free Trial Banner if applicable
//                         if (billingState.isTrialActive && !billingState.hasPaidPremium) {
//                             TrialBanner(billingState, displayLanguage)
//                             Spacer(Modifier.height(16.dp))
//                         }

//                         Spacer(Modifier.weight(1f))

//                         // Legal & Footer links on the left side
//                         LegalAndRestoreFooter(
//                             isLight = isLight,
//                             displayLanguage = displayLanguage,
//                             onDismiss = onDismiss,
//                             onRestore = {
//                                 com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.restorePurchases()
//                                 onDismiss?.invoke()
//                             },
//                             onShowLegalDoc = { doc -> showLegalDoc = doc }
//                         )
//                     }

//                     // Divider between columns
//                     VerticalDivider(
//                         modifier = Modifier
//                             .height(420.dp)
//                             .width(1.dp),
//                         color = Gold.copy(alpha = 0.2f)
//                     )

//                     // Right Pane: Tier Cards & Action Button
//                     Column(
//                         modifier = Modifier
//                             .weight(1f)
//                             .fillMaxHeight(),
//                         verticalArrangement = Arrangement.SpaceBetween
//                     ) {
//                         Column(
//                             modifier = Modifier
//                                 .fillMaxWidth()
//                                 .verticalScroll(rememberScrollState()),
//                             verticalArrangement = Arrangement.spacedBy(12.dp)
//                         ) {
//                             TierPlansList(
//                                 billingState = billingState,
//                                 activePlanId = activePlanId,
//                                 selectedPlan = selectedPlan,
//                                 isLight = isLight,
//                                 displayLanguage = displayLanguage,
//                                 onPlanSelected = { selectedPlan = it }
//                             )
//                         }

//                         Spacer(Modifier.height(16.dp))

//                         ActionSupportButton(
//                             selectedPlan = selectedPlan,
//                             displayLanguage = displayLanguage,
//                             onDismiss = onDismiss
//                         )
//                     }
//                 }
//             } else {
//                 // ── PORTRAIT / STANDARD VERTICAL STACK LAYOUT ──
//                 Column(
//                     modifier = Modifier
//                         .fillMaxWidth()
//                         .padding(20.dp),
//                     horizontalAlignment = Alignment.CenterHorizontally
//                 ) {
//                     BrandingHeader(isLight, displayLanguage)

//                     if (billingState.isTrialActive && !billingState.hasPaidPremium) {
//                         Spacer(Modifier.height(14.dp))
//                         TrialBanner(billingState, displayLanguage)
//                     }

//                     Spacer(Modifier.height(20.dp))

//                     TierPlansList(
//                         billingState = billingState,
//                         activePlanId = activePlanId,
//                         selectedPlan = selectedPlan,
//                         isLight = isLight,
//                         displayLanguage = displayLanguage,
//                         onPlanSelected = { selectedPlan = it }
//                     )

//                     Spacer(Modifier.height(24.dp))

//                     ActionSupportButton(
//                         selectedPlan = selectedPlan,
//                         displayLanguage = displayLanguage,
//                         onDismiss = onDismiss
//                     )

//                     Spacer(Modifier.height(14.dp))

//                     LegalAndRestoreFooter(
//                         isLight = isLight,
//                         displayLanguage = displayLanguage,
//                         onDismiss = onDismiss,
//                         onRestore = {
//                             com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.restorePurchases()
//                             onDismiss?.invoke()
//                         },
//                         onShowLegalDoc = { doc -> showLegalDoc = doc }
//                     )
//                 }
//             }
//         }
//     }

//     showLegalDoc?.let { doc ->
//         LegalDocumentDialog(
//             docType = doc,
//             displayLanguage = displayLanguage,
//             onDismiss = { showLegalDoc = null }
//         )
//     }
// }

// // ── SUB-COMPONENTS FOR CLEAN MODULARITY & REUSE ──

// @Composable
// private fun BrandingHeader(isLight: Boolean, displayLanguage: DisplayLanguage) {
//     Column(horizontalAlignment = Alignment.CenterHorizontally) {
//         Box(
//             contentAlignment = Alignment.Center,
//             modifier = Modifier
//                 .size(72.dp)
//                 .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Gold.copy(alpha = 0.4f))
//                 .clip(CircleShape)
//                 .background(Color.White)
//                 .background(
//                     Brush.linearGradient(
//                         colors = listOf(
//                             Gold.copy(alpha = 0.25f),
//                             Gold.copy(alpha = 0.08f)
//                         )
//                     )
//                 )
//                 .border(BorderStroke(1.5.dp, Gold.copy(alpha = 0.5f)), CircleShape)
//         ) {
//             Text(
//                 text = "ੴ",
//                 color = Gold,
//                 fontSize = 32.sp,
//                 fontWeight = FontWeight.Bold
//             )
//         }

//         Spacer(Modifier.height(12.dp))

//         Text(
//             text = "Nitnem Steek is 100% free forever for all Sangat worldwide. Voluntary Seva contributions directly fund cloud servers, high-quality audio bandwidth, and ongoing Gurmat app features.".translate(displayLanguage),
//             fontSize = 13.5.sp,
//             color = if (isLight) Color(0xFF5D4037).copy(alpha = 0.85f) else Color(0xFFB0BEC5),
//             textAlign = TextAlign.Center,
//             lineHeight = 20.sp
//         )
//     }
// }

// @Composable
// private fun TrialBanner(billingState: com.creativeblisslabs.nitnemguktasteek.domain.billing.BillingState, displayLanguage: DisplayLanguage) {
//     Surface(
//         shape = RoundedCornerShape(14.dp),
//         color = Accent.copy(alpha = 0.12f),
//         border = BorderStroke(1.dp, Accent.copy(alpha = 0.3f)),
//         modifier = Modifier.fillMaxWidth()
//     ) {
//         Row(
//             modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
//             verticalAlignment = Alignment.CenterVertically,
//             horizontalArrangement = Arrangement.spacedBy(10.dp)
//         ) {
//             Icon(
//                 imageVector = Icons.Default.Celebration,
//                 contentDescription = null,
//                 tint = Accent,
//                 modifier = Modifier.size(20.dp)
//             )
//             Text(
//                 text = "Free Premium Preview: ${billingState.trialDaysRemaining} days remaining".translate(displayLanguage),
//                 fontSize = 12.5.sp,
//                 fontWeight = FontWeight.Bold,
//                 color = Accent
//             )
//         }
//     }
// }

// @Composable
// private fun TierPlansList(
//     billingState: com.creativeblisslabs.nitnemguktasteek.domain.billing.BillingState,
//     activePlanId: String,
//     selectedPlan: InAppProductPlan,
//     isLight: Boolean,
//     displayLanguage: DisplayLanguage,
//     onPlanSelected: (InAppProductPlan) -> Unit
// ) {
//     Column(
//         modifier = Modifier.fillMaxWidth(),
//         verticalArrangement = Arrangement.spacedBy(12.dp)
//     ) {
//         billingState.availablePlans.forEach { plan ->
//             val isSelected = selectedPlan.id == plan.id
//             val isCurrentActivePlan = plan.id == activePlanId

//             val cardBgColor by animateColorAsState(
//                 targetValue = if (isSelected) {
//                     if (isLight) Gold.copy(alpha = 0.12f) else Gold.copy(alpha = 0.1f)
//                 } else {
//                     if (isLight) Color.White else Color(0xFF1A1714)
//                 },
//                 label = "cardBgColor"
//             )

//             val borderColor by animateColorAsState(
//                 targetValue = if (isSelected) Gold else {
//                     if (isLight) Color(0xFFE8DFD3) else Color(0xFF2E2924)
//                 },
//                 label = "borderColor"
//             )

//             Surface(
//                 modifier = Modifier
//                     .fillMaxWidth()
//                     .clickable { onPlanSelected(plan) },
//                 shape = RoundedCornerShape(20.dp),
//                 color = cardBgColor,
//                 border = BorderStroke(width = if (isSelected) 2.dp else 1.dp, color = borderColor)
//             ) {
//                 Column(modifier = Modifier.padding(12.dp)) {
//                     Row(
//                         modifier = Modifier.fillMaxWidth(),
//                         horizontalArrangement = Arrangement.SpaceBetween,
//                         verticalAlignment = Alignment.CenterVertically
//                     ) {
//                         Row(
//                             modifier = Modifier.weight(1f),
//                             verticalAlignment = Alignment.CenterVertically,
//                             horizontalArrangement = Arrangement.spacedBy(8.dp)
//                         ) {
//                             RadioButton(
//                                 selected = isSelected,
//                                 onClick = { onPlanSelected(plan) },
//                                 colors = RadioButtonDefaults.colors(
//                                     selectedColor = Gold,
//                                     unselectedColor = Color.Gray
//                                 ),
//                                 modifier = Modifier.size(24.dp)
//                             )
                            
//                             Column(
//                                 modifier = Modifier.weight(1f),
//                                 verticalArrangement = Arrangement.spacedBy(2.dp)
//                             ) {
//                                 Row(
//                                     verticalAlignment = Alignment.CenterVertically,
//                                     horizontalArrangement = Arrangement.spacedBy(6.dp),
//                                     modifier = Modifier.fillMaxWidth()
//                                 ) {
//                                     Text(
//                                         text = plan.title,
//                                         fontWeight = FontWeight.Bold,
//                                         fontSize = 15.sp,
//                                         color = if (isLight) Color(0xFF2B1D12) else Color.White,
//                                         maxLines = 1,
//                                         modifier = Modifier.weight(if (isCurrentActivePlan) 0.6f else 1f, fill = false)
//                                     )
                                    
//                                     if (isCurrentActivePlan) {
//                                         Surface(
//                                             shape = RoundedCornerShape(6.dp),
//                                             color = Accent.copy(alpha = 0.18f),
//                                             border = BorderStroke(0.5.dp, Accent)
//                                         ) {
//                                             Text(
//                                                 text = "Active".translate(displayLanguage),
//                                                 fontSize = 9.5.sp,
//                                                 fontWeight = FontWeight.Bold,
//                                                 color = Accent,
//                                                 modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
//                                             )
//                                         }
//                                     }
//                                 }
                                
//                                 if (plan.badge != null) {
//                                     Text(
//                                         text = plan.badge!!,
//                                         color = Gold,
//                                         fontWeight = FontWeight.SemiBold,
//                                         fontSize = 11.5.sp,
//                                         maxLines = 1
//                                     )
//                                 }
//                             }
//                         }

//                         Spacer(Modifier.width(8.dp))

//                         Surface(
//                             shape = RoundedCornerShape(10.dp),
//                             color = if (plan.isFreeTier) Accent.copy(alpha = 0.1f) else Gold.copy(alpha = 0.12f),
//                             border = BorderStroke(0.8.dp, if (plan.isFreeTier) Accent.copy(alpha = 0.4f) else Gold.copy(alpha = 0.4f))
//                         ) {
//                             Column(
//                                 modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
//                                 horizontalAlignment = Alignment.End
//                             ) {
//                                 if (plan.isFreeTier) {
//                                     Text(
//                                         text = "Free".translate(displayLanguage),
//                                         fontWeight = FontWeight.ExtraBold,
//                                         fontSize = 14.sp,
//                                         color = Accent
//                                     )
//                                 } else {
//                                     Text(
//                                         text = plan.formattedPriceInr,
//                                         fontWeight = FontWeight.ExtraBold,
//                                         fontSize = 14.sp,
//                                         color = Gold
//                                     )
//                                     if (!plan.formattedPriceUsd.isNullOrBlank()) {
//                                         Text(
//                                             text = plan.formattedPriceUsd!!,
//                                             fontWeight = FontWeight.SemiBold,
//                                             fontSize = 10.5.sp,
//                                             color = if (isLight) Color(0xFF795548) else Color(0xFFB0BEC5)
//                                         )
//                                     }
//                                 }
//                             }
//                         }
//                     }

//                     Spacer(Modifier.height(8.dp))

//                     Text(
//                         text = plan.description,
//                         fontSize = 12.sp,
//                         color = if (isLight) Color(0xFF6D4C41) else Color(0xFF9E9E9E),
//                         modifier = Modifier.padding(start = 32.dp),
//                         lineHeight = 16.sp
//                     )

//                     if (isSelected) {
//                         Spacer(Modifier.height(10.dp))
//                         Column(
//                             modifier = Modifier
//                                 .padding(start = 32.dp)
//                                 .fillMaxWidth(),
//                             verticalArrangement = Arrangement.spacedBy(6.dp)
//                         ) {
//                             plan.features.forEach { feat ->
//                                 Row(
//                                     verticalAlignment = Alignment.CenterVertically,
//                                     horizontalArrangement = Arrangement.spacedBy(6.dp)
//                                 ) {
//                                     Icon(
//                                         imageVector = Icons.Default.CheckCircle,
//                                         contentDescription = null,
//                                         tint = Gold,
//                                         modifier = Modifier.size(14.dp)
//                                     )
//                                     Text(
//                                         text = feat,
//                                         fontSize = 11.5.sp,
//                                         color = if (isLight) Color(0xFF3E2723) else Color.White.copy(alpha = 0.9f)
//                                     )
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//     }
// }

// @Composable
// private fun ActionSupportButton(
//     selectedPlan: InAppProductPlan,
//     displayLanguage: DisplayLanguage,
//     onDismiss: (() -> Unit)?
// ) {
//     if (!selectedPlan.isFreeTier) {
//         Button(
//             onClick = {
//                 com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.purchase(selectedPlan)
//                 onDismiss?.invoke()
//             },
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .height(54.dp)
//                 .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), spotColor = Gold.copy(alpha = 0.45f)),
//             shape = RoundedCornerShape(16.dp),
//             colors = ButtonDefaults.buttonColors(
//                 containerColor = Color.Transparent,
//                 contentColor = Color.Black
//             ),
//             contentPadding = PaddingValues(0.dp)
//         ) {
//             Box(
//                 modifier = Modifier
//                     .fillMaxSize()
//                     .background(
//                         Brush.horizontalGradient(
//                             colors = listOf(
//                                 Gold,
//                                 Gold.copy(alpha = 0.85f),
//                                 Gold.copy(alpha = 0.95f)
//                             )
//                         )
//                     ),
//                 contentAlignment = Alignment.Center
//             ) {
//                 Row(
//                     verticalAlignment = Alignment.CenterVertically,
//                     horizontalArrangement = Arrangement.Center,
//                     modifier = Modifier.padding(horizontal = 16.dp)
//                 ) {
//                     Icon(
//                         imageVector = Icons.Default.Favorite,
//                         contentDescription = null,
//                         tint = Color.Black,
//                         modifier = Modifier.size(18.dp)
//                     )
//                     Spacer(Modifier.width(8.dp))
//                     Text(
//                         text = "Support with ${selectedPlan.formattedPriceInr} (${selectedPlan.title})",
//                         fontSize = 14.5.sp,
//                         fontWeight = FontWeight.Bold,
//                         color = Color.Black,
//                         maxLines = 1
//                     )
//                 }
//             }
//         }
//     } else {
//         OutlinedButton(
//             onClick = {
//                 onDismiss?.invoke()
//             },
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .height(54.dp),
//             shape = RoundedCornerShape(16.dp),
//             border = BorderStroke(1.5.dp, Gold)
//         ) {
//             Icon(
//                 imageVector = Icons.Default.Favorite,
//                 contentDescription = null,
//                 tint = Gold,
//                 modifier = Modifier.size(18.dp)
//             )
//             Spacer(Modifier.width(8.dp))
//             Text(
//                 text = "Enjoying Nitnem Free Forever".translate(displayLanguage),
//                 fontSize = 14.5.sp,
//                 fontWeight = FontWeight.Bold,
//                 color = Gold,
//                 maxLines = 1
//             )
//         }
//     }
// }

// @Composable
// private fun LegalAndRestoreFooter(
//     isLight: Boolean,
//     displayLanguage: DisplayLanguage,
//     onDismiss: (() -> Unit)?,
//     onRestore: () -> Unit,
//     onShowLegalDoc: (String) -> Unit
// ) {
//     Column(
//         modifier = Modifier.fillMaxWidth(),
//         horizontalAlignment = Alignment.CenterHorizontally
//     ) {
//         // Restore Purchases & Maybe Later
//         Row(
//             modifier = Modifier.fillMaxWidth(),
//             horizontalArrangement = Arrangement.Center,
//             verticalAlignment = Alignment.CenterVertically
//         ) {
//             TextButton(
//                 onClick = onRestore,
//                 contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
//             ) {
//                 Icon(
//                     imageVector = Icons.Default.Sync,
//                     contentDescription = null,
//                     tint = Gold,
//                     modifier = Modifier.size(14.dp)
//                 )
//                 Spacer(Modifier.width(6.dp))
//                 Text(
//                     text = "Restore Purchases".translate(displayLanguage),
//                     fontSize = 12.5.sp,
//                     fontWeight = FontWeight.SemiBold,
//                     color = Gold
//                 )
//             }

//             if (onDismiss != null) {
//                 Text(
//                     text = "•",
//                     color = if (isLight) Color.LightGray else Color.DarkGray,
//                     modifier = Modifier.padding(horizontal = 6.dp)
//                 )

//                 TextButton(
//                     onClick = onDismiss,
//                     contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
//                 ) {
//                     Text(
//                         text = "Maybe Later".translate(displayLanguage),
//                         fontSize = 12.5.sp,
//                         fontWeight = FontWeight.Medium,
//                         color = if (isLight) Color.Gray else Color.White.copy(alpha = 0.6f)
//                     )
//                 }
//             }
//         }

//         Spacer(Modifier.height(8.dp))

//         // Legal Terms & Privacy Links
//         Row(
//             modifier = Modifier.fillMaxWidth(),
//             horizontalArrangement = Arrangement.Center,
//             verticalAlignment = Alignment.CenterVertically
//         ) {
//             Text(
//                 text = "Terms of Service".translate(displayLanguage),
//                 fontSize = 11.sp,
//                 color = if (isLight) Color(0xFF8D6E63) else Color.White.copy(alpha = 0.5f),
//                 modifier = Modifier
//                     .clickable { onShowLegalDoc("Terms of Service") }
//                     .padding(4.dp)
//             )
//             Text(
//                 text = "•",
//                 fontSize = 11.sp,
//                 color = if (isLight) Color.LightGray else Color.DarkGray,
//                 modifier = Modifier.padding(horizontal = 6.dp)
//             )
//             Text(
//                 text = "Privacy Policy".translate(displayLanguage),
//                 fontSize = 11.sp,
//                 color = if (isLight) Color(0xFF8D6E63) else Color.White.copy(alpha = 0.5f),
//                 modifier = Modifier
//                     .clickable { onShowLegalDoc("Privacy Policy") }
//                     .padding(4.dp)
//             )
//             Text(
//                 text = "•",
//                 fontSize = 11.sp,
//                 color = if (isLight) Color.LightGray else Color.DarkGray,
//                 modifier = Modifier.padding(horizontal = 5.dp)
//             )
//             Text(
//                 text = "Refunds".translate(displayLanguage),
//                 fontSize = 11.sp,
//                 color = if (isLight) Color(0xFF8D6E63) else Color.White.copy(alpha = 0.5f),
//                 modifier = Modifier
//                     .clickable { onShowLegalDoc("Refund Policy") }
//                     .padding(4.dp)
//             )
//         }
//     }
// }

// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun SevaSupporterModalSheet(
//     displayLanguage: DisplayLanguage,
//     onDismiss: () -> Unit,
//     onPurchasePlan: (InAppProductPlan) -> Unit = {}
// ) {
//     val isLight = AppTheme.themeMode == ThemeMode.Light
//     val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

//     LaunchedEffect(Unit) {
//         com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.refreshCustomerInfo()
//     }

//     ModalBottomSheet(
//         onDismissRequest = onDismiss,
//         sheetState = sheetState,
//         containerColor = if (isLight) Color(0xFFFFFBF5) else Color(0xFF141210),
//         dragHandle = { BottomSheetDefaults.DragHandle() }
//     ) {
//         SevaTiersContent(
//             displayLanguage = displayLanguage,
//             onPurchasePlan = onPurchasePlan,
//             onDismiss = onDismiss,
//             modifier = Modifier
//                 .navigationBarsPadding()
//                 .verticalScroll(rememberScrollState())
//         )
//     }
// }