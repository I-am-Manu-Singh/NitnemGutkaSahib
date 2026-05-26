package org.audiocular.nitnem.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.GlanceId
import org.audiocular.nitnem.MainActivity
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class NitnemWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            WidgetContent()
        }
    }
}

@Composable
private fun WidgetContent() {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("nitnem_widget_state", Context.MODE_PRIVATE)
    }

    val streakDays = prefs.getInt("streak_days", 0)
    val widgetTheme = prefs.getString("widget_theme", "Classic Gold") ?: "Classic Gold"
    val completedBaniIds = prefs.getStringSet("completed_bani_ids", emptySet()) ?: emptySet()

    val (backgroundColor, accentColor, textColor, secondaryTextColor) = when (widgetTheme) {
        "Classic Gold" -> Quadruple(
            Color(0xFF0F1115),
            Color(0xFFD4AF37),
            Color(0xFFFFFFFF),
            Color(0xCCFFFFFF)
        )
        "AMOLED Black" -> Quadruple(
            Color.Black,
            Color.White,
            Color.White,
            Color(0xAAFFFFFF)
        )
        else -> Quadruple( // App Theme
            Color(0xFF171A20),
            Color(0xFF10B981), // Emerald Green
            Color.White,
            Color(0xB3FFFFFF)
        )
    }

    val isJapjiDone = completedBaniIds.contains("japji-sahib")
    val isJaapDone = completedBaniIds.contains("jaap-sahib")
    val isChaupaiDone = completedBaniIds.contains("benti-chaupai-sahib")
    val isRehrasDone = completedBaniIds.contains("rehras-sahib")
    val isSohilaDone = completedBaniIds.contains("kirtan-sohila")

    var completedCount = 0
    if (isJapjiDone) completedCount++
    if (isJaapDone) completedCount++
    if (isChaupaiDone) completedCount++
    if (isRehrasDone) completedCount++
    if (isSohilaDone) completedCount++

    val totalPrayers = 5
    val progress = completedCount.toFloat() / totalPrayers.toFloat()

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(backgroundColor)
            .cornerRadius(28.dp)
            .clickable(
                actionStartActivity<MainActivity>()
            )
            .padding(16.dp)
    ) {
        // Date & Streak Header Row
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Daily Nitnem",
                    style = TextStyle(
                        color = ColorProvider(textColor),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = getCurrentDateString(),
                    style = TextStyle(
                        color = ColorProvider(secondaryTextColor),
                        fontSize = 11.sp
                    )
                )
            }
            Box(
                modifier = GlanceModifier
                    .background(ColorProvider(accentColor.copy(alpha = 0.15f)))
                    .cornerRadius(12.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔥 $streakDays Days",
                    style = TextStyle(
                        color = ColorProvider(accentColor),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Progress Text
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress: $completedCount / $totalPrayers Completed",
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "${(progress * 100).toInt()}%",
                style = TextStyle(
                    color = ColorProvider(accentColor),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(6.dp))

        // Linear Progress Indicator
        LinearProgressIndicator(
            progress = progress,
            modifier = GlanceModifier.fillMaxWidth().height(6.dp),
            color = ColorProvider(accentColor),
            backgroundColor = ColorProvider(textColor.copy(alpha = 0.12f))
        )

        Spacer(modifier = GlanceModifier.height(10.dp))

        // Checklist of Prayers
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            BaniChecklistItem("Japji Sahib", isJapjiDone, textColor, secondaryTextColor)
            Spacer(modifier = GlanceModifier.height(4.dp))
            BaniChecklistItem("Jaap Sahib", isJaapDone, textColor, secondaryTextColor)
            Spacer(modifier = GlanceModifier.height(4.dp))
            BaniChecklistItem("Chaupai Sahib", isChaupaiDone, textColor, secondaryTextColor)
            Spacer(modifier = GlanceModifier.height(4.dp))
            BaniChecklistItem("Rehras Sahib", isRehrasDone, textColor, secondaryTextColor)
            Spacer(modifier = GlanceModifier.height(4.dp))
            BaniChecklistItem("Kirtan Sohila", isSohilaDone, textColor, secondaryTextColor)
        }
    }
}

@Composable
private fun BaniChecklistItem(
    name: String,
    isDone: Boolean,
    textColor: Color,
    secondaryTextColor: Color
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isDone) "✅" else "⬜",
            style = TextStyle(fontSize = 12.sp)
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Text(
            text = name,
            style = TextStyle(
                color = ColorProvider(if (isDone) textColor else secondaryTextColor),
                fontSize = 12.sp,
                fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

private fun getCurrentDateString(): String {
    val sdf = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
    return sdf.format(Date())
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

class NitnemJourneyWidgetProvider : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NitnemWidget()
}