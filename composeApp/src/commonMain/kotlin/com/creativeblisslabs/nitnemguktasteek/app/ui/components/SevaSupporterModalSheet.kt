package com.creativeblisslabs.nitnemguktasteek.app.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.domain.billing.InAppProductPlan
import com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager
import com.creativeblisslabs.nitnemguktasteek.utils.translate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevaSupporterModalSheet(
    displayLanguage: DisplayLanguage,
    onDismiss: () -> Unit,
    onPurchasePlan: (InAppProductPlan) -> Unit = {}
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val billingManager = remember { PremiumBillingManager.instance }
    val billingState by billingManager.billingState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPlan by remember {
        mutableStateOf(billingState.availablePlans.firstOrNull { it.isLaunchOffer } ?: billingState.availablePlans.first())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isLight) Color(0xFFFFFBF5) else Color(0xFF141210),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── TOP HEADER EMBLEM ──
            Surface(
                shape = CircleShape,
                color = Gold.copy(alpha = 0.16f),
                border = BorderStroke(1.5.dp, Gold.copy(alpha = 0.45f)),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "ੴ",
                        color = Gold,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Keep Nitnem Free & Ad-Free".translate(displayLanguage),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isLight) Color(0xFF2B1D12) else Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Nitnem and all Gurbani text & audio are completely free forever. Support server costs and continuous development with a one-time Seva contribution.".translate(displayLanguage),
                fontSize = 13.sp,
                color = if (isLight) Color(0xFF5D4037).copy(alpha = 0.85f) else Color(0xFFB0BEC5),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            // Free Trial Banner if applicable
            if (billingState.isTrialActive) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Accent.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, Accent.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = Accent, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Free Premium Preview: ${billingState.trialDaysRemaining} days remaining".translate(displayLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Accent
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── TIER PLANS CARDS ──
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                billingState.availablePlans.forEach { plan ->
                    val isSelected = selectedPlan.id == plan.id

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlan = plan },
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) {
                            if (isLight) Gold.copy(alpha = 0.18f) else Gold.copy(alpha = 0.15f)
                        } else {
                            if (isLight) Color.White else Color(0xFF1E1B18)
                        },
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Gold else (if (isLight) Color(0xFFE8DFD3) else Color(0xFF2E2924))
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPlan = plan },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Gold,
                                            unselectedColor = Color.Gray
                                        )
                                    )
                                    Column {
                                        Text(
                                            text = plan.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (isLight) Color(0xFF2B1D12) else Color.White
                                        )
                                        if (plan.badge != null) {
                                            Text(
                                                text = plan.badge,
                                                color = Gold,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${plan.formattedPriceInr} / ${plan.formattedPriceUsd}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = Gold
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = plan.description,
                                fontSize = 12.sp,
                                color = if (isLight) Color(0xFF6D4C41) else Color(0xFF9E9E9E),
                                modifier = Modifier.padding(start = 36.dp)
                            )

                            if (isSelected) {
                                Spacer(Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier
                                        .padding(start = 36.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
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

            Spacer(Modifier.height(20.dp))

            // ── ACTION BUTTON ──
            Button(
                onClick = {
                    billingManager.simulatePurchase(selectedPlan)
                    onPurchasePlan(selectedPlan)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Support with ${selectedPlan.formattedPriceInr} (One-Time)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ── RESTORE PURCHASES & MAYBE LATER ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        billingManager.simulateRestore()
                        onDismiss()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Restore Purchases".translate(displayLanguage),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold
                    )
                }

                Text(
                    text = "•",
                    color = if (isLight) Color.LightGray else Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Maybe Later".translate(displayLanguage),
                        fontSize = 12.5.sp,
                        color = if (isLight) Color.Gray else Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
