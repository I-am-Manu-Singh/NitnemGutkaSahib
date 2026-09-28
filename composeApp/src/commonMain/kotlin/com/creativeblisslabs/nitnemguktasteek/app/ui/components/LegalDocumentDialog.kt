package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.utils.translate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentDialog(
    docType: String,
    displayLanguage: DisplayLanguage,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.45f)),
            shadowElevation = 12.dp
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val icon = when (docType) {
                                    "Privacy Policy" -> Icons.Default.Security
                                    "Terms of Service" -> Icons.Default.Description
                                    "Refund Policy" -> Icons.Default.Receipt
                                    else -> Icons.Default.MenuBook
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = docType.translate(displayLanguage),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                bottomBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Color.Black)
                            ) {
                                Text("I Understand".translate(displayLanguage), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (docType) {
                        "Privacy Policy" -> PrivacyPolicyContent(displayLanguage)
                        "Terms of Service" -> TermsOfServiceContent(displayLanguage)
                        "Refund Policy" -> CancellationRefundContent(displayLanguage)
                        else -> AttributionsContent(displayLanguage)
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun PrivacyPolicyContent(displayLanguage: DisplayLanguage) {
    DocumentHeader("Privacy Policy & Data Transparency", "Effective Date: January 1, 2026 • Creative Bliss Labs")
    
    SectionCard(
        title = "1. 100% Offline-First Core",
        body = "Nitnem Steek is engineered to be completely offline-first. All Gurmukhi Gurbani texts, English/Punjabi Steek translations, Roman transliterations, and core audio files function entirely on your device without transmitting your reading activity or private personal data to external ad networks."
    )

    SectionCard(
        title = "2. Optional Cloud Synchronization",
        body = "When you choose to sign in with Google Firebase, only your non-sensitive spiritual journey milestones (Total XP, active streak count, completed Banis for the day, and Sangat group leaderboard standings) are backed up securely to Cloud Firestore so you never lose your practice history when switching devices."
    )

    SectionCard(
        title = "3. Zero Third-Party Advertising & Zero Tracking",
        body = "We do not sell, rent, or monetize your personal data. We do not integrate third-party advertising networks (like AdMob or Facebook Pixel). The app is 100% ad-free forever as part of our Seva commitment."
    )

    SectionCard(
        title = "4. Secure Billing & In-App Purchases",
        body = "All voluntary Seva contributions and in-app memberships are processed directly via Google Play In-App Billing (powered by RevenueCat). Creative Bliss Labs never accesses or stores your payment details or credit cards."
    )

    SectionCard(
        title = "5. Device Permissions",
        body = "• POST_NOTIFICATIONS: Used solely to schedule local Amritvela, Rehras Sahib, and Kirtan Sohila Nitnem reminders chosen by you.\n• FOREGROUND_SERVICE_MEDIA_PLAYBACK: Used to allow continuous uninterrupted Nitnem Gurbani audio listening while your screen is locked."
    )

    SectionCard(
        title = "6. Account & Data Deletion",
        body = "You can request complete deletion of your Firebase profile and progress data at any time by contacting creativeblisslabs@gmail.com or using the in-app feedback channel."
    )
}

@Composable
private fun TermsOfServiceContent(displayLanguage: DisplayLanguage) {
    DocumentHeader("Terms of Service & EULA", "Creative Bliss Labs • Nitnem Gutka Steek")

    SectionCard(
        title = "1. Reverence & Sacred Intent",
        body = "Nitnem Steek is provided to the global Sangat as a digital spiritual companion for reading, contemplating, and listening to sacred Gurbani from Sri Guru Granth Sahib Ji, Sri Dasam Granth Sahib, and Bhai Gurdas Ji Vaaran. Users are encouraged to maintain maryada, cleanliness, and respect while reciting Gurbani."
    )

    SectionCard(
        title = "2. Guaranteed 100% Free Core Scripture Access",
        body = "All Gurbani texts, translations, transliterations, and standard audio recordings are 100% free and will always remain free forever. Voluntary Seva Supporter contributions never restrict or gate scripture content."
    )

    SectionCard(
        title = "3. Seva Supporter Contributions & Billing",
        body = "Voluntary Seva memberships (Monthly Seva, Annual Sangat Patron, Lifetime Patron) are voluntary contributions that fund audio hosting infrastructure, Steek research, and continuous development. In appreciation, supporters receive visual aesthetic enhancements, ambient particle auras, and Sangat badges. Subscriptions renew automatically unless cancelled via Google Play at least 24 hours before the end of the current period."
    )

    SectionCard(
        title = "4. Intellectual Property & Fair Use",
        body = "Sacred Gurbani belongs to the Timeless Creator (Akaal Purakh) and the Sikh Panth. The application code, curated typography layout engines, synchronized timestamp engines, and UI design are proprietary works of Creative Bliss Labs."
    )
}

@Composable
private fun CancellationRefundContent(displayLanguage: DisplayLanguage) {
    DocumentHeader("Cancellation & Refund Policy", "Standard Google Play Billing Guidelines")

    SectionCard(
        title = "1. Easy Cancellation Anytime",
        body = "All voluntary recurring Seva memberships (e.g. Monthly Seva, Annual Patron) can be cancelled at any time directly through Google Play Store > Payments & Subscriptions. Upon cancellation, your supporter perks will remain active until the end of your current billing period with zero cancellation fees."
    )

    SectionCard(
        title = "2. Google Play Refund Process",
        body = "Purchases made through Google Play In-App Billing are subject to standard Google Play refund policies. If you made an accidental purchase or experienced a technical billing discrepancy, you can request a full refund within 48 hours directly via Google Play Order History or by emailing order details to creativeblisslabs@gmail.com."
    )

    SectionCard(
        title = "3. Lifetime Supporter Tier",
        body = "Lifetime Sangat Patron is a single one-time voluntary contribution. Once processed, it grants perpetual supporter recognition and benefits across all future app updates."
    )
}

@Composable
private fun AttributionsContent(displayLanguage: DisplayLanguage) {
    DocumentHeader("Attributions & Open Source Credits", "Gratitude to Panthic Contributors & Scholars")

    SectionCard(
        title = "1. Gurbani Sources & Steek Translations",
        body = "• Gurbani Gurmukhi database and Steek translations curated in accordance with authentic Panthic Steeks (Prof. Sahib Singh Ji, Sant Kartar Singh Ji Khalsa Bhindranwale, and Bhai Vir Singh Ji).\n• English translation courtesy of Sant Singh Khalsa, MD, and Panthic translation archives."
    )

    SectionCard(
        title = "2. Open Source Typographies (OFL)",
        body = "• Mukta Gurmukhi / Mukta Devanagari by Ek Type (SIL Open Font License 1.1)\n• Anmol Lipi & Gurbani Akhar by Dr. Kulbir Singh Thind\n• Rozha One & Yatra One by Modular Infotech (SIL Open Font License 1.1)\n• Lora & Roboto Mono by Google Fonts (SIL Open Font License / Apache 2.0)"
    )

    SectionCard(
        title = "3. Audio Recitations",
        body = "• Nitnem recitations courtesy of Panthic audio archives and independent Gurbani Sevaks.\n• Synchronized timecode alignments developed by Creative Bliss Labs for synchronized word-by-word tracking."
    )
}

@Composable
private fun DocumentHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 6.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = Gold
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun SectionCard(title: String, body: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
        border = BorderStroke(0.8.dp, Gold.copy(alpha = 0.20f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Accent
            )
            Text(
                text = body,
                fontSize = 12.5.sp,
                color = TextPrimary.copy(alpha = 0.9f),
                lineHeight = 18.sp
            )
        }
    }
}
