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
        billingState.isLifetimePatron -> BillingProducts.LIFETIME_PATRON
        billingState.hasPaidPremium -> BillingProducts.PREMIUM_ANNUAL
        billingState.isSupporter -> BillingProducts.SUPPORTER_MONTHLY
        else -> BillingProducts.FREE_TIER
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
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── TOP HEADER EMBLEM WITH GLOW ──
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Gold.copy(alpha = 0.4f))
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Gold.copy(alpha = 0.25f),
                            Gold.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(BorderStroke(1.5.dp, Gold.copy(alpha = 0.5f)), CircleShape)
        ) {
            Text(
                text = "ੴ",
                color = Gold,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }


        Spacer(Modifier.height(8.dp))

        Text(
            text = "Nitnem Gutka Steek is 100% free forever for all Sangat worldwide. Voluntary Seva contributions directly fund cloud servers, high-quality audio bandwidth, and ongoing Gurmat app features.".translate(displayLanguage),
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
                border = BorderStroke(1.dp, Accent.copy(alpha = 0.3f))
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
                        text = plan.badge,
                        color = Gold,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.5.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // Right section: Clean stacked pricing block (Prevents clipping)
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
                            text = plan.formattedPriceUsd,
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
                    billingManager.simulatePurchase(selectedPlan)
                    onPurchasePlan(selectedPlan)
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
                    billingManager.simulateRestore()
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
        
        Spacer(Modifier.height(16.dp))
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