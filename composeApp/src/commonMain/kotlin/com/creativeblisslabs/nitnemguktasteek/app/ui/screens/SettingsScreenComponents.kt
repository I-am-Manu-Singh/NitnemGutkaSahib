package com.creativeblisslabs.nitnemguktasteek.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import com.creativeblisslabs.nitnemguktasteek.data.CuratedAudioCatalog
import com.creativeblisslabs.nitnemguktasteek.utils.timingKey
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.data.preferences.SettingsRepository
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import com.creativeblisslabs.nitnemguktasteek.utils.rememberAudioPicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import com.creativeblisslabs.nitnemguktasteek.app.sync.SyncEditorScreen

data class SettingsToggleItem(

    val title: String,

    val checked: Boolean,

    val onChecked: (Boolean) -> Unit
)

enum class SettingsCategory(
    val title: String
) {

    Reading("Reading"),

    Appearance("Appearance"),

    Reminders("Account & Data"),

    Audio("Manage Audio"),

    Supporter("Seva & Tiers"),

    About("About & Feedback"),
}

@Composable
fun ReminderEditableRow(

    emoji: String,

    title: String,

    time: String,

    onClick: () -> Unit
) {

    Surface(

        modifier = Modifier
            .fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            SurfaceColor,

        border =
            BorderStroke(
                1.dp,
                Accent.copy(alpha = 0.08f)
            ),

        onClick = onClick
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = emoji,
                fontSize = 22.sp
            )

            Spacer(
                Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = title,

                    color = TextPrimary,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 15.sp
                )

                Spacer(
                    Modifier.height(2.dp)
                )

                Text(

                    text = time,

                    color =
                        Accent,

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }

            Icon(

                imageVector =
                    Icons.Default.Edit,

                contentDescription = null,

                tint =
                    Accent
            )
        }
    }
}
/*
 * SECTION CARD
 */

@Composable
fun SettingsSectionCard(

    title: String,

    icon:
        androidx.compose.ui.graphics.vector
            .ImageVector,

    content:
        @Composable ColumnScope.() -> Unit
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        color =
            SurfaceColor,

        border = BorderStroke(

            1.dp,

            Accent.copy(alpha = 0.08f)
        )
    ) {

        Column(

            modifier =
                Modifier.padding(22.dp)
        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier =
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Accent.copy(
                                    alpha = 0.12f
                                )
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector = icon,

                        contentDescription =
                            null,

                        tint = Accent,

                        modifier =
                            Modifier.size(20.dp)
                    )
                }

                Spacer(
                    Modifier.width(14.dp)
                )

                Text(

                    text = title,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 20.sp,

                    color = Primary
                )
            }

            Spacer(
                Modifier.height(24.dp)
            )

            content()
        }
    }
}

/*
 * SWITCH ROW
 */

@Composable
fun SettingsSwitchRow(

    title: String,

    checked: Boolean,

    onChecked:
        (Boolean) -> Unit
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(

            text = title,

            color = Primary,

            fontWeight =
                FontWeight.Medium
        )

        Switch(

            checked = checked,

            onCheckedChange =
                onChecked
        )
    }
}

/*
 * SLIDER
 */

@Composable
fun CompactSlider(

    title: String,

    value: Float,

    range:
        ClosedFloatingPointRange<Float>,

    onValueChange:
        (Float) -> Unit
) {

    Column {

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(

                text = title,

                color = Primary,

                fontWeight =
                    FontWeight.Medium
            )

            Text(

                text =
                    String.format(
                        "%.1f",
                        value
                    ),

                color = Accent,

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Slider(

            value = value,

            onValueChange =
                onValueChange,

            valueRange = range
        )
    }
}

/*
 * TOGGLE GROUP
 */

@Composable
fun SettingsToggleGroup(

    title: String,

    items:
        List<SettingsToggleItem>
) {

    Column {

        Text(

            text = title,

            color = Primary,

            fontWeight =
                FontWeight.SemiBold,

            fontSize = 14.sp
        )

        Spacer(
            Modifier.height(12.dp)
        )

        Column(

            verticalArrangement =
                Arrangement.spacedBy(2.dp)
        ) {

            items.forEachIndexed { index, item ->

                CompactToggleRow(

                    title =
                        item.title,

                    checked =
                        item.checked,

                    onChecked =
                        item.onChecked
                )

                if (
                    index !=
                    items.lastIndex
                ) {

                    HorizontalDivider(

                        color =
                            TextPrimary.copy(
                                alpha = 0.08f
                            )
                    )
                }
            }
        }
    }
}

/*
 * TOGGLE ROW
 */

@Composable
fun CompactToggleRow(

    title: String,

    checked: Boolean,

    onChecked:
        (Boolean) -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(

            text = title,

            modifier =
                Modifier.weight(1f),

            color = Primary,

            fontSize = 14.sp
        )

        Switch(

            checked = checked,

            onCheckedChange =
                onChecked
        )
    }
}

/*
 * REMINDER ROW
 */

@Composable
fun ReminderRow(

    emoji: String,

    title: String,

    displayLanguage: DisplayLanguage
){

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            MaterialTheme.colorScheme
                .surfaceContainerHighest
                .copy(alpha = 0.35f),

        border = BorderStroke(

            1.dp,

            Accent.copy(alpha = 0.06f)
        )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(

                text = emoji,

                fontSize = 24.sp
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = title,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 15.sp,

                    color = Primary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

          Text(

    text =
        "Custom reminder schedule".translate(displayLanguage),

    fontSize = 12.sp,

    color =
        TextPrimary.copy(
            alpha = 0.55f
        )
)
            }

            Surface(

                shape =
                    RoundedCornerShape(50.dp),

                color =
                    Accent.copy(alpha = 0.12f)
            ) {

              Text(

    text = "Manage".translate(displayLanguage),

    modifier =
        Modifier.padding(
            horizontal = 12.dp,
            vertical = 6.dp
        ),

    color = Accent,

    fontSize = 12.sp,

    fontWeight =
        FontWeight.SemiBold
)
            }
        }
    }
}

/*
 * ACCENT SELECTOR (TIER-AWARE)
 */

@Composable
fun AccentThemeSelector(
    displayLanguage: DisplayLanguage,
    onAccentSelected: (AccentStyle) -> Unit,
    onLockedThemeClicked: () -> Unit = {}
) {
    val billingManager = remember { com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager.instance }
    val billingState by billingManager.billingState.collectAsState()

    val accentOptions = remember {
        listOf(
            // Free Tiers
            AccentStyle.Emerald to "Emerald",
            AccentStyle.Sapphire to "Sapphire",
            AccentStyle.Royal to "Royal",
            AccentStyle.Sunset to "Sunset",

            // Supporter Tiers
            AccentStyle.GoldenAnand to "Golden Anand",
            AccentStyle.SimranRose to "Simran Rose",

            // Premium Tiers
            AccentStyle.CosmicIndigo to "Cosmic Indigo",
            AccentStyle.MidnightGold to "Midnight Gold",
            AccentStyle.ForestSilence to "Forest Silence",
            AccentStyle.SaffronSunset to "Saffron Sunset",
            AccentStyle.CelestialViolet to "Celestial Violet",
            AccentStyle.SlateMinimal to "Slate Minimal"
        )
    }

    val previewColors = remember {
        mapOf(
            AccentStyle.Emerald to Color(0xFF44D7B6),
            AccentStyle.Sapphire to Color(0xFF5DAEFF),
            AccentStyle.Royal to Color(0xFF9B7CFF),
            AccentStyle.Sunset to Color(0xFFFF9E57),
            AccentStyle.GoldenAnand to Color(0xFFFFD700),
            AccentStyle.SimranRose to Color(0xFFFF7597),
            AccentStyle.CosmicIndigo to Color(0xFF818CF8),
            AccentStyle.MidnightGold to Color(0xFFFFDF73),
            AccentStyle.ForestSilence to Color(0xFF34D399),
            AccentStyle.SaffronSunset to Color(0xFFFB923C),
            AccentStyle.CelestialViolet to Color(0xFFA78BFA),
            AccentStyle.SlateMinimal to Color(0xFF94A3B8)
        )
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
    ) {
        items(accentOptions) { (style, label) ->
            val selected = AppTheme.accentStyle == style
            val color = previewColors[style] ?: Accent

            val isUnlocked = when (style.requiredTier) {
                "supporter" -> billingState.isSupporter || billingState.isPremium || billingState.isLifetimePatron || billingState.isTrialActive
                "premium" -> billingState.isPremium || billingState.isLifetimePatron || billingState.isTrialActive
                else -> true
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(if (selected) 56.dp else 46.dp),
                    shape = CircleShape,
                    color = color,
                    border = BorderStroke(
                        if (selected) 3.dp else 1.dp,
                        if (selected) color.copy(alpha = 0.6f) else Color.Transparent
                    ),
                    onClick = {
                        if (isUnlocked) {
                            onAccentSelected(style)
                        } else {
                            onLockedThemeClicked()
                        }
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = label.translate(displayLanguage),
                        fontSize = 11.sp,
                        color = if (selected) color else TextPrimary.copy(alpha = if (isUnlocked) 0.8f else 0.45f),
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (style.requiredTier != "free") {
                        Text(
                            text = if (style.requiredTier == "supporter") "❤️" else "⭐",
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSelectorRow(
    title: String,
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    val modes = listOf(
        "default", "accent", "muted", "white", "gold", "purple", "blue", "green", "orange"
    )
    val modeColors = mapOf(
        "default" to TextPrimary.copy(alpha = 0.8f),
        "accent" to Accent,
        "muted" to TextSecondary,
        "white" to Color.White,
        "gold" to Color(0xFFD6B25E),
        "purple" to Color(0xFFB39DFF),
        "blue" to Color(0xFF7AB8FF),
        "green" to Color(0xFF63D8B6),
        "orange" to Color(0xFFFFB36A)
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(modes) { mode ->
                val color = modeColors[mode] ?: Color.Gray
                val selected = selectedMode == mode
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) Accent else Color.LightGray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { onModeSelected(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (mode == "white") Color.Black else Color.White)
                        )
                    }
                }
            }
        }
    }
}/*
 * HELPERS
 */
fun formatDuration(
    durationMs: Long
): String {

    if (durationMs <= 0L) {
        return ""
    }

    val totalSeconds =
        durationMs / 1000

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    return when {

        hours > 0 -> {

            String.format(
                "%d:%02d:%02d",
                hours,
                minutes,
                seconds
            )
        }

        else -> {

            String.format(
                "%02d:%02d",
                minutes,
                seconds
            )
        }
    }
}
@Composable
fun SyncedBadge() {

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Accent.copy(alpha = 0.12f),
        modifier = Modifier.padding(start = 6.dp)
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 6.dp,
                vertical = 2.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(10.dp)
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "Synced",
                color = Accent,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AudioSectionHeader(
    title: String,
    subtitle: String
) {

    Text(
        text = title,
        color = Accent,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )

    Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 11.sp,
        modifier = Modifier.padding(
            bottom = 8.dp
        )
    )
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AudioTrackRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    synced: Boolean,
    downloading: Boolean = false,
    showDelete: Boolean = false,
    audioFileName: String? = null,
    timingFileName: String? = null,
    isTimingDownloaded: Boolean = false,
    isTimingDownloading: Boolean = false,
    onDownloadTiming: (() -> Unit)? = null,
    isChecked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onSelect: () -> Unit,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onDownload: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isChecked != null && onCheckedChange != null) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Accent,
                    uncheckedColor = Accent.copy(alpha = 0.5f),
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.padding(end = 2.dp)
            )
        }

        RadioButton(
            selected = selected,
            onClick = onSelect
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f, fill = false),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (synced || isTimingDownloaded) {
                    SyncedBadge()
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!audioFileName.isNullOrBlank() || !timingFileName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    if (showDelete) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.14f),
                            border = BorderStroke(0.6.dp, Color(0xFF4CAF50).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Downloaded", tint = Color(0xFF4CAF50), modifier = Modifier.size(11.dp))
                                Text(
                                    text = "Downloaded",
                                    fontSize = 9.5.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    if (!audioFileName.isNullOrBlank()) {
                        val audioLabel = when {
                            audioFileName.endsWith(".opus", ignoreCase = true) -> "Opus Audio"
                            audioFileName.endsWith(".mp3", ignoreCase = true) -> "MP3 Audio"
                            audioFileName.endsWith(".m4a", ignoreCase = true) -> "M4A Audio"
                            else -> "Audio File"
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Accent.copy(alpha = 0.08f),
                            border = BorderStroke(0.6.dp, Accent.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Accent, modifier = Modifier.size(10.dp))
                                Text(
                                    text = audioLabel,
                                    fontSize = 9.5.sp,
                                    color = Accent,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (!timingFileName.isNullOrBlank()) {
                        val hasLocalTiming = synced || isTimingDownloaded
                        val timingLabel = if (hasLocalTiming) "Synced" else if (onDownloadTiming != null) "Get Timing" else "Edit Timing"
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (hasLocalTiming) Gold.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(0.6.dp, if (hasLocalTiming) Gold.copy(alpha = 0.4f) else Accent.copy(alpha = 0.25f)),
                            modifier = if (!hasLocalTiming && onDownloadTiming != null) {
                                Modifier.clickable { onDownloadTiming() }
                            } else if (!hasLocalTiming) {
                                Modifier.clickable { onEdit() }
                            } else {
                                Modifier
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                if (isTimingDownloading) {
                                    CircularProgressIndicator(modifier = Modifier.size(9.dp), strokeWidth = 1.dp, color = Gold)
                                } else {
                                    Icon(
                                        imageVector = if (hasLocalTiming) Icons.Default.Sync else if (onDownloadTiming != null) Icons.Default.Download else Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = if (hasLocalTiming) Gold else Accent,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                Text(
                                    text = timingLabel,
                                    fontSize = 9.5.sp,
                                    color = if (hasLocalTiming) Gold else Accent,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (downloading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(2.dp),
                    color = Accent,
                    strokeWidth = 2.dp
                )
            } else {
                onDownload?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onPlay,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (showDelete) {
                IconButton(
                    onClick = {
                        onDelete?.invoke()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

/*
 * TIER-AWARE FONT SELECTOR
 */
@Composable
fun TierAwareFontSelector(
    title: String,
    options: List<Triple<String, String, String>>, // (Key/Id, Display Title, Required Tier)
    selectedKey: String,
    onSelect: (String) -> Unit,
    onLockedClicked: () -> Unit
) {
    val billingManager = remember { com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager.instance }
    val billingState by billingManager.billingState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Primary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
        ) {
            items(options) { (key, name, tier) ->
                val selected = selectedKey.equals(key, ignoreCase = true)
                val isUnlocked = when (tier) {
                    "supporter" -> billingState.isSupporter || billingState.isPremium || billingState.isLifetimePatron || billingState.isTrialActive
                    "premium" -> billingState.isPremium || billingState.isLifetimePatron || billingState.isTrialActive
                    else -> true
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) Accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                    border = BorderStroke(
                        if (selected) 1.5.dp else 0.8.dp,
                        if (selected) Accent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.clickable {
                        if (isUnlocked) {
                            onSelect(key)
                        } else {
                            onLockedClicked()
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) Accent else Primary
                        )

                        if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Gold,
                                modifier = Modifier.size(13.dp)
                            )
                        } else if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Accent,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}