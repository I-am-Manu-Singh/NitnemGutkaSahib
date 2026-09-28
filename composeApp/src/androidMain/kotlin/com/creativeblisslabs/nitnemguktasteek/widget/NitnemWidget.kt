package com.creativeblisslabs.nitnemguktasteek.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.ContentScale
import com.creativeblisslabs.nitnemguktasteek.MainActivity
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveBaniTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import androidx.glance.appwidget.SizeMode
import androidx.glance.LocalSize

// ─────────────────────────────────────────────────────────────────────────────
// Widget
// ─────────────────────────────────────────────────────────────────────────────

class NitnemWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { WidgetContent() }
    }
}

class NitnemJourneyWidgetProvider : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NitnemWidget()
}

// ─────────────────────────────────────────────────────────────────────────────
// Root composable — HeroCard styled layout
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WidgetContent() {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("nitnem_widget_state", Context.MODE_PRIVATE)
    }

    val streakDays        = prefs.getInt("streak_days", 0)
    val monthlyConsistencyDays = prefs.getInt("monthly_consistency_days", streakDays.coerceAtMost(30))
    val weeklyCompleted   = prefs.getInt("weekly_completed", 0)
    val widgetTheme       = prefs.getString("widget_theme", "Dark") ?: "Dark"
    val widgetContent     = prefs.getString("widget_content", "Mascot Assistant") ?: "Mascot Assistant"
    val completedBaniIds  = prefs.getStringSet("completed_bani_ids", emptySet()) ?: emptySet()
    val totalPref         = prefs.getInt("total", 7)
    val completedPref     = prefs.getInt("completed", 0)

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    // ── Recommended Bani from Time of Day ──────────────────────────────────
    val recommendedBani = when (hour) {
        in 3..6 -> "Japji Sahib"
        in 7..11 -> "Jaap Sahib"
        in 12..15 -> "Anand Sahib"
        in 16..18 -> "Rehras Sahib"
        else -> "Kirtan Sohila"
    }
    val theme = remember(recommendedBani) {
        resolveBaniTheme(recommendedBani)
    }

    // ── Theme colours ──────────────────────────────────────────────────────
    val isLight = widgetTheme == "Light"
    val accent = Color(0xFFFFC107) // Amber/Gold
    val textPrimary = if (isLight) Color(0xFF212529) else Color.White
    val textSecondary = if (isLight) Color(0xFF495057) else Color(0xCCFFFFFF)

    // ── State-derived values (Canonical 7 Nitnem Banis) ────────────────────
    val totalBanis      = 7
    val completedCount  = completedPref.coerceIn(0, totalBanis)
    val progress        = completedCount.toFloat() / totalBanis
    val activeDays      = if (monthlyConsistencyDays > 0) monthlyConsistencyDays else streakDays.coerceIn(0, 30)

    val headerTitle: String
    val subtitleMsg: String
    val detailMsg  : String

    if (completedCount >= totalBanis) {
        headerTitle = "Nitnem Complete"
        subtitleMsg = when {
            streakDays >= 30 -> "Sensational! $streakDays-day streak! Guru Kirpa!"
            streakDays >= 10 -> "Incredible! $streakDays-day streak! Bani is in bliss!"
            streakDays > 0 -> "Bani is proud of you! $streakDays-day streak alive!"
            else -> "Nitnem Complete! Waheguru Ji Ki Fateh!"
        }
        detailMsg   = "Daily target achieved!"
    } else {
        headerTitle = recommendedBani
        subtitleMsg = when {
            completedCount > 0 -> "You're on track ($completedCount/$totalBanis)! Keep reciting."
            hour >= 21 -> "Night has arrived... Don't miss Kirtan Sohila tonight!"
            hour >= 17 -> "Sunset is here! Recite Rehras Sahib with devotion."
            hour in 3..6 -> "Amrit Vela bliss! Start your morning path with Japji Sahib."
            streakDays == 0 -> "Start your daily Nitnem journey today!"
            streakDays > 0 -> "Keep your $streakDays-day streak active today."
            else -> "Tap to start your Nitnem today!"
        }
        detailMsg   = "$completedCount / $totalBanis banis"
    }

    // ── Responsive constraints & aspect-ratio classification ───────────────
    val size = LocalSize.current
    val wDp = size.width.value.toInt()
    val hDp = size.height.value.toInt()

    val widthDp = wDp.coerceAtLeast(80)
    val heightDp = hDp.coerceAtLeast(50)

    val aspectRatio = widthDp.toFloat() / heightDp.toFloat()
    val isSquareOrTall = aspectRatio <= 1.25f

    val isUltraNarrow = wDp < 130
    val isNarrow = wDp < 190
    val isUltraShort = hDp < 75
    val isShort = hDp < 100
    val isTall = hDp >= 135
    val isWide = wDp >= 220

    // ── Dynamic Background Generator ───────────────────────────────────────
    val bgBitmap = remember(hour, widgetTheme, progress, widthDp, heightDp) {
        drawWidgetBackground(context, widthDp, heightDp, widgetTheme, progress)
    }

    val mascotState = when {
        completedCount >= totalBanis -> MascotWidgetState.Celebrating
        completedCount == 0 && hour >= 17 -> MascotWidgetState.Encouraging
        completedCount == 0 -> MascotWidgetState.Encouraging
        else -> MascotWidgetState.Happy
    }
    val mascotBitmap = remember(accent, mascotState) {
        drawNitnemMascot(accent = accent, state = mascotState)
    }

    // ── Root container ─────────────────────────────────────────────────────
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(20.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Image(
            provider = ImageProvider(bgBitmap),
            contentDescription = null,
            modifier = GlanceModifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (isUltraShort && isUltraNarrow) {
            Box(
                modifier = GlanceModifier.fillMaxSize().padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (widgetContent == "Mascot Assistant") {
                    Image(
                        provider = ImageProvider(mascotBitmap),
                        contentDescription = "Mascot",
                        modifier = GlanceModifier.size(28.dp)
                    )
                } else {
                    Text(
                        text = "ੴ",
                        style = TextStyle(color = ColorProvider(accent), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        } else if (isUltraNarrow) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (widgetContent == "Mascot Assistant") {
                    Image(
                        provider = ImageProvider(mascotBitmap),
                        contentDescription = "Mascot",
                        modifier = GlanceModifier.size(32.dp)
                    )
                    Spacer(GlanceModifier.height(2.dp))
                    Text(
                        text = "$completedCount/$totalBanis",
                        style = TextStyle(color = ColorProvider(accent), fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = "ੴ",
                        style = TextStyle(color = ColorProvider(accent), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    Spacer(GlanceModifier.height(2.dp))
                    Text(
                        text = "$completedCount/$totalBanis",
                        style = TextStyle(color = ColorProvider(accent), fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }
                Spacer(GlanceModifier.height(3.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = GlanceModifier.fillMaxWidth().height(3.dp).cornerRadius(2.dp),
                    color = ColorProvider(accent),
                    backgroundColor = ColorProvider(if (isLight) Color(0x33000000) else Color(0x33FFFFFF))
                )
            }
        } else if (isUltraShort) {
            Row(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (widgetContent == "Mascot Assistant") {
                    Image(
                        provider = ImageProvider(mascotBitmap),
                        contentDescription = "Mascot",
                        modifier = GlanceModifier.size(26.dp)
                    )
                } else {
                    Text(
                        text = "ੴ",
                        style = TextStyle(color = ColorProvider(accent), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(GlanceModifier.width(8.dp))
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = headerTitle,
                            style = TextStyle(color = ColorProvider(textPrimary), fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Spacer(GlanceModifier.defaultWeight())
                        Text(
                            text = "$completedCount/$totalBanis",
                            style = TextStyle(color = ColorProvider(accent), fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                    }
                    Spacer(GlanceModifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = GlanceModifier.fillMaxWidth().height(3.dp).cornerRadius(2.dp),
                        color = ColorProvider(accent),
                        backgroundColor = ColorProvider(if (isLight) Color(0x33000000) else Color(0x33FFFFFF))
                    )
                }
            }
        } else if (isSquareOrTall && !isShort) {
            // ── THREE-ROW LAYOUT FOR SQUARE & TALL / LARGE WIDGETS ──
            val mascotSize = if (isTall) 52.dp else 42.dp
            val titleFontSize = if (isTall) 13.sp else 11.sp
            val descFontSize = if (isTall) 10.sp else 9.sp

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ROW 1: Mascot / Emblem Centered at Top
                if (widgetContent == "Mascot Assistant") {
                    Image(
                        provider = ImageProvider(mascotBitmap),
                        contentDescription = "Bani mascot",
                        modifier = GlanceModifier.size(mascotSize)
                    )
                } else {
                    Box(
                        modifier = GlanceModifier
                            .size(mascotSize)
                            .background(ColorProvider(accent.copy(alpha = 0.15f)))
                            .cornerRadius((mascotSize.value / 2).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ੴ",
                            style = TextStyle(color = ColorProvider(accent), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(GlanceModifier.height(6.dp))

                // ROW 2: Header Title & Detail / Subtitles
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = headerTitle,
                        style = TextStyle(
                            color = ColorProvider(textPrimary),
                            fontSize = titleFontSize,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Spacer(GlanceModifier.height(2.dp))
                    Text(
                        text = subtitleModelOrDetail(subtitleMsg, detailMsg, completedCount, totalBanis),
                        style = TextStyle(
                            color = ColorProvider(textSecondary),
                            fontSize = descFontSize
                        ),
                        maxLines = 2
                    )
                }

                Spacer(GlanceModifier.height(6.dp))

                // ROW 3: Progress & Weekly Heatmap Dots
                Column(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = GlanceModifier.fillMaxWidth().height(4.dp).cornerRadius(2.dp),
                        color = ColorProvider(accent),
                        backgroundColor = ColorProvider(if (isLight) Color(0x33000000) else Color(0x33FFFFFF))
                    )

                    if (hDp >= 130) {
                        Spacer(GlanceModifier.height(8.dp))
                        Box(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(ColorProvider(accent.copy(alpha = 0.18f)))
                        ) {}
                        Spacer(GlanceModifier.height(6.dp))

                        val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
                        val calDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                        val currentDayIndex = when(calDay) {
                            Calendar.MONDAY -> 0
                            Calendar.TUESDAY -> 1
                            Calendar.WEDNESDAY -> 2
                            Calendar.THURSDAY -> 3
                            Calendar.FRIDAY -> 4
                            Calendar.SATURDAY -> 5
                            else -> 6
                        }

                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                            verticalAlignment = Alignment.Vertical.CenterVertically
                        ) {
                            for (i in weekDays.indices) {
                                val isPastOrToday = i <= currentDayIndex
                                val isMarked = isPastOrToday && (i < weeklyCompleted || (completedCount > 0 && i == currentDayIndex))

                                Column(
                                    modifier = GlanceModifier.defaultWeight(),
                                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                                ) {
                                    Text(
                                        text = weekDays[i],
                                        style = TextStyle(
                                            color = ColorProvider(textSecondary.copy(alpha = 0.7f)),
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(GlanceModifier.height(2.dp))
                                    Box(
                                        modifier = GlanceModifier
                                            .size(9.dp)
                                            .cornerRadius(3.dp)
                                            .background(
                                                ColorProvider(
                                                    if (isMarked) accent.copy(alpha = 0.95f)
                                                    else textSecondary.copy(alpha = 0.18f)
                                                )
                                            )
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val mascotSize = when {
                isTall && isWide -> 44.dp
                isShort -> 30.dp
                else -> 36.dp
            }
            val titleFontSize = if (isWide && !isShort) 12.sp else 10.sp
            val descFontSize = if (isWide) 10.sp else 9.sp

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isNarrow) 8.dp else 12.dp,
                        vertical = if (isShort) 6.dp else 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ── TOP HERO ROW ──
                Row(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (widgetContent == "Mascot Assistant") {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                provider = ImageProvider(mascotBitmap),
                                contentDescription = "Bani mascot",
                                modifier = GlanceModifier.size(mascotSize)
                            )
                        }
                    } else {
                        Box(
                            modifier = GlanceModifier
                                .size(mascotSize)
                                .background(ColorProvider(accent.copy(alpha = 0.15f)))
                                .cornerRadius((mascotSize.value / 2).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ੴ",
                                style = TextStyle(color = ColorProvider(accent), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(GlanceModifier.width(8.dp))

                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = headerTitle,
                                style = TextStyle(
                                    color = ColorProvider(textPrimary),
                                    fontSize = titleFontSize,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1
                            )

                            Spacer(modifier = GlanceModifier.defaultWeight())

                            Text(
                                text = detailMsg,
                                style = TextStyle(
                                    color = ColorProvider(accent),
                                    fontSize = 12.sp
                                ),
                                maxLines = 1
                            )
                        }

                        if (!isShort) {
                            Spacer(GlanceModifier.height(1.dp))
                            Text(
                                text = subtitleMsg,
                                style = TextStyle(
                                    color = ColorProvider(textSecondary),
                                    fontSize = descFontSize
                                ),
                                maxLines = if (isTall) 2 else 1
                            )
                        }

                        Spacer(GlanceModifier.height(3.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = GlanceModifier.fillMaxWidth().height(4.dp).cornerRadius(2.dp),
                            color = ColorProvider(accent),
                            backgroundColor = ColorProvider(if (isLight) Color(0x33000000) else Color(0x33FFFFFF))
                        )
                    }
                }

                // ── SQUARED HEATMAP & WEEKLY STREAK SECTION (M, T, W, T, F, S, S) ──
                if (!isShort && !isNarrow) {
                    Spacer(GlanceModifier.height(4.dp))
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorProvider(accent.copy(alpha = 0.18f)))
                    ) {}
                    Spacer(GlanceModifier.height(4.dp))

                    val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
                    val calDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    val currentDayIndex = when(calDay) {
                        Calendar.MONDAY -> 0
                        Calendar.TUESDAY -> 1
                        Calendar.WEDNESDAY -> 2
                        Calendar.THURSDAY -> 3
                        Calendar.FRIDAY -> 4
                        Calendar.SATURDAY -> 5
                        else -> 6
                    }

                    Column(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                    ) {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Vertical.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Consistency",
                                style = TextStyle(
                                    color = ColorProvider(textPrimary.copy(alpha = 0.9f)),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1
                            )
                            Spacer(GlanceModifier.defaultWeight())
                            Text(
                                text = "Streak: ${streakDays}d",
                                style = TextStyle(
                                    color = ColorProvider(accent),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1
                            )
                        }
                        Spacer(GlanceModifier.height(3.dp))

                        Row(
                            modifier = GlanceModifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                            verticalAlignment = Alignment.Vertical.CenterVertically
                        ) {
                            for (i in weekDays.indices) {
                                val isPastOrToday = i <= currentDayIndex
                                val isMarked = isPastOrToday && (i < weeklyCompleted || (completedCount > 0 && i == currentDayIndex))

                                Column(
                                    modifier = GlanceModifier.defaultWeight(),
                                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                                ) {
                                    Text(
                                        text = weekDays[i],
                                        style = TextStyle(
                                            color = ColorProvider(textSecondary.copy(alpha = 0.7f)),
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(GlanceModifier.height(2.dp))
                                    Box(
                                        modifier = GlanceModifier
                                            .size(10.dp)
                                            .cornerRadius(3.dp)
                                            .background(
                                                ColorProvider(
                                                    if (isMarked) accent.copy(alpha = 0.95f)
                                                    else textSecondary.copy(alpha = 0.18f)
                                                )
                                            )
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun subtitleModelOrDetail(sub: String, detail: String, completed: Int, total: Int): String {
    return if (completed > 0) "$sub ($completed/$total)" else sub
}

// ─────────────────────────────────────────────────────────────────────────────
// Dynamic background generator
// ─────────────────────────────────────────────────────────────────────────────

private fun drawWidgetBackground(context: Context, widthDp: Int, heightDp: Int, widgetTheme: String, progress: Float): Bitmap {
    val density = context.resources.displayMetrics.density
    val width = (widthDp * density).toInt().coerceAtLeast(100)
    val height = (heightDp * density).toInt().coerceAtLeast(60)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val isNight = hour >= 19 || hour < 6

    if (widgetTheme == "Light") {
        val lightColors = intArrayOf(
            0xFFFDFBF7.toInt(),
            0xFFFAF3E0.toInt(),
            0xFFF5E6C4.toInt(),
            0xFFFAF3E0.toInt()
        )
        val grad = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            lightColors, null, Shader.TileMode.CLAMP
        )
        paint.shader = grad
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        paint.color = 0xFFFFC107.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3.5f * density
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.style = Paint.Style.FILL
    } else {
        val skyColors = when {
            hour in 3..5 -> intArrayOf(
                0xFF030712.toInt(),
                0xFF0F172A.toInt(),
                0xFF1E1B4B.toInt(),
                0xFF31102F.toInt()
            )
            hour in 6..8 -> intArrayOf(
                0xFF4C1D95.toInt(),
                0xFF8B5CF6.toInt(),
                0xFFEC4899.toInt(),
                0xFFF59E0B.toInt()
            )
            hour in 9..16 -> intArrayOf(
                0xFF0284C7.toInt(),
                0xFF0369A1.toInt(),
                0xFF075985.toInt(),
                0xFF0C4A6E.toInt()
            )
            hour in 17..18 -> intArrayOf(
                0xFF31102F.toInt(),
                0xFFBE123C.toInt(),
                0xFFF59E0B.toInt(),
                0xFF1E1035.toInt()
            )
            hour in 19..20 -> intArrayOf(
                0xFF0B0F19.toInt(),
                0xFF1E1B4B.toInt(),
                0xFF111827.toInt(),
                0xFF030712.toInt()
            )
            else -> intArrayOf(
                0xFF030712.toInt(),
                0xFF080E1E.toInt(),
                0xFF0F172A.toInt(),
                0xFF1E293B.toInt()
            )
        }

        val grad = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            skyColors, null, Shader.TileMode.CLAMP
        )
        paint.shader = grad
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null

        if (isNight) {
            paint.color = 0x80FFFFFF.toInt()
            val starPositions = floatArrayOf(
                0.10f, 0.18f,
                0.22f, 0.12f,
                0.35f, 0.28f,
                0.48f, 0.14f,
                0.62f, 0.24f,
                0.75f, 0.10f,
                0.85f, 0.30f,
                0.92f, 0.16f
            )
            for (i in 0 until starPositions.size step 2) {
                val sx = width * starPositions[i]
                val sy = height * starPositions[i + 1]
                canvas.drawCircle(sx, sy, 1.2f * density, paint)
            }
        }
    }
    if (progress > 0f) {
        paint.color = 0xFFFFC107.toInt()
        paint.style = Paint.Style.FILL
        val barHeight = 5.5f * density
        val progressWidth = width * progress.coerceIn(0f, 1f)
        canvas.drawRect(0f, height - barHeight, progressWidth, height.toFloat(), paint)
    }

    return bitmap
}

// ─────────────────────────────────────────────────────────────────────────────
// Mascot states & drawing logic
// ─────────────────────────────────────────────────────────────────────────────

private enum class MascotWidgetState { Encouraging, Happy, Celebrating }
private fun drawNitnemMascot(
    accent: Color,
    state: MascotWidgetState
): Bitmap {
    val sz     = 156
    val bitmap = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val cx     = sz / 2f
    val cy     = sz / 2f

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val goldLight = Color(0xFFFFEE58).toArgb()
    val goldMid   = Color(0xFFFFB300).toArgb()
    val goldDark  = Color(0xFFFF6F00).toArgb()
    val orange    = Color(0xFFFF8C00).toArgb()
    val deepOrange= Color(0xFFE65100).toArgb()
    val darkFace  = Color(0xFF2D2D2D).toArgb()
    val white     = Color.White.toArgb()
    val accentArgb= accent.toArgb()

    val bodyRadius = sz * 0.35f

    // ── Spiritual Aura Glow matching app design ───────────────────────────
    val auraRadius = bodyRadius * 2.2f
    val colorsList = intArrayOf(
        Color(0xFFFFC107).copy(alpha = 0.35f).toArgb(),
        Color(0xFFFFC107).copy(alpha = 0.12f).toArgb(),
        android.graphics.Color.TRANSPARENT
    )
    val auraGrad = RadialGradient(
        cx, cy, auraRadius,
        colorsList, null, Shader.TileMode.CLAMP
    )
    paint.shader = auraGrad
    canvas.drawCircle(cx, cy, auraRadius, paint)
    paint.shader = null

    // Radiating aura rays
    paint.color = Color(0xFFFFC107).copy(alpha = 0.55f).toArgb()
    paint.strokeWidth = 3f
    paint.style = Paint.Style.STROKE
    paint.strokeCap = Paint.Cap.ROUND
    for (i in 0 until 8) {
        val angle = (i * 2 * Math.PI / 8)
        val startRad = bodyRadius * 1.15f
        val endRad = bodyRadius * 1.45f
        val startX = cx + startRad * Math.cos(angle).toFloat()
        val startY = cy + startRad * Math.sin(angle).toFloat()
        val endX = cx + endRad * Math.cos(angle).toFloat()
        val endY = cy + endRad * Math.sin(angle).toFloat()
        canvas.drawLine(startX, startY, endX, endY, paint)
    }
    paint.style = Paint.Style.FILL

    // ── Shadow ────────────────────────────────────────────────────────────
    paint.color = Color.Black.copy(alpha = 0.12f).toArgb()
    canvas.drawOval(
        RectF(cx - bodyRadius * 0.8f, cy + bodyRadius * 0.95f, cx + bodyRadius * 0.8f, cy + bodyRadius * 1.30f),
        paint
    )

    // ── Body path ─────────────────────────────────────────────────────────
    val bodyPath = Path().apply {
        moveTo(cx, cy - bodyRadius * 1.20f)
        cubicTo(cx + bodyRadius * 0.55f, cy - bodyRadius * 1.18f, cx + bodyRadius * 0.82f, cy - bodyRadius * 0.55f, cx + bodyRadius * 0.78f, cy - bodyRadius * 0.05f)
        cubicTo(cx + bodyRadius * 0.95f, cy + bodyRadius * 0.45f, cx + bodyRadius * 0.90f, cy + bodyRadius * 0.95f, cx + bodyRadius * 0.45f, cy + bodyRadius * 1.18f)
        cubicTo(cx + bodyRadius * 0.20f, cy + bodyRadius * 1.28f, cx - bodyRadius * 0.20f, cy + bodyRadius * 1.28f, cx - bodyRadius * 0.45f, cy + bodyRadius * 1.18f)
        cubicTo(cx - bodyRadius * 0.90f, cy + bodyRadius * 0.95f, cx - bodyRadius * 0.95f, cy + bodyRadius * 0.45f, cx - bodyRadius * 0.78f, cy - bodyRadius * 0.05f)
        cubicTo(cx - bodyRadius * 0.82f, cy - bodyRadius * 0.55f, cx - bodyRadius * 0.55f, cy - bodyRadius * 1.18f, cx, cy - bodyRadius * 1.20f)
        close()
    }

    // ── Underlay Glow ─────────────────────────────────────────────────────
    val scaleMatrix = android.graphics.Matrix()
    scaleMatrix.setScale(1.15f, 1.15f, cx, cy)
    val underlayPath = Path(bodyPath)
    underlayPath.transform(scaleMatrix)
    paint.color = Color(0xFFFFD54F).copy(alpha = 0.15f).toArgb()
    canvas.drawPath(underlayPath, paint)

    // ── Body base ─────────────────────────────────────────────────────────
    val bodyGrad = LinearGradient(
        cx - bodyRadius, cy - bodyRadius,
        cx + bodyRadius, cy + bodyRadius,
        intArrayOf(goldLight, goldMid, goldDark),
        null, Shader.TileMode.CLAMP
    )
    paint.shader = bodyGrad
    paint.alpha = 255
    canvas.drawPath(bodyPath, paint)
    paint.shader = null

    // ── Body outline ──────────────────────────────────────────────────────
    paint.color = deepOrange
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2.5f
    paint.alpha = 255
    canvas.drawPath(bodyPath, paint)
    paint.style = Paint.Style.FILL

    // ── Belly feather details ──────────────────────────────────────────────
    paint.style = Paint.Style.STROKE
    paint.color = Color(0xFFE65100).copy(alpha = 0.25f).toArgb()
    paint.strokeWidth = bodyRadius * 0.04f
    paint.strokeCap = Paint.Cap.ROUND
    repeat(5) { i ->
        val fx = cx - bodyRadius * 0.38f + i * bodyRadius * 0.19f
        val fy = cy + bodyRadius * 0.18f
        canvas.drawArc(
            RectF(fx, fy, fx + bodyRadius * 0.22f, fy + bodyRadius * 0.14f),
            25f, 130f, false, paint
        )
    }
    paint.style = Paint.Style.FILL

    // ── Belly highlight ovals ─────────────────────────────────────────────
    paint.color = Color.White.copy(alpha = 0.45f).toArgb()
    canvas.drawOval(
        RectF(cx - bodyRadius * 0.55f, cy - bodyRadius * 0.75f, cx - bodyRadius * 0.30f, cy - bodyRadius * 0.15f),
        paint
    )
    paint.color = Color.White.copy(alpha = 0.12f).toArgb()
    canvas.drawOval(
        RectF(cx - bodyRadius * 0.75f, cy - bodyRadius * 0.9f, cx + bodyRadius * 0.75f, cy + bodyRadius * 0.9f),
        paint
    )

    paint.color = Color.White.toArgb()
    paint.alpha = 255

    // ── Left wing ─────────────────────────────────────────────────────────
    val leftWingX = cx - bodyRadius * 0.56f
    val leftWingY = cy + bodyRadius * 0.38f
    canvas.save()
    canvas.rotate(-6f, leftWingX, leftWingY)
    val leftWing = Path().apply {
        moveTo(leftWingX, leftWingY)
        cubicTo(leftWingX - bodyRadius * 0.25f, leftWingY - bodyRadius * 0.18f, leftWingX - bodyRadius * 0.55f, leftWingY - bodyRadius * 0.10f, leftWingX - bodyRadius * 0.70f, leftWingY + bodyRadius * 0.10f)
        quadTo(leftWingX - bodyRadius * 0.78f, leftWingY + bodyRadius * 0.22f, leftWingX - bodyRadius * 0.60f, leftWingY + bodyRadius * 0.34f)
        quadTo(leftWingX - bodyRadius * 0.25f, leftWingY + bodyRadius * 0.42f, leftWingX, leftWingY)
        close()
    }
    val wingGrad = LinearGradient(
        leftWingX - bodyRadius * 0.5f, leftWingY - bodyRadius * 0.2f,
        leftWingX, leftWingY + bodyRadius * 0.3f,
        intArrayOf(Color(0xFFFFF9C4).toArgb(), Color(0xFFFFD54F).toArgb()),
        null, Shader.TileMode.CLAMP
    )
    paint.shader = wingGrad
    paint.alpha = 255
    canvas.drawPath(leftWing, paint)
    paint.shader = null

    paint.color = Color(0xFFF5B041).copy(alpha = 0.45f).toArgb()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = bodyRadius * 0.015f
    paint.strokeCap = Paint.Cap.ROUND
    repeat(3) { i ->
        canvas.drawLine(
            leftWingX - bodyRadius * 0.12f,
            leftWingY + bodyRadius * 0.08f,
            leftWingX - bodyRadius * (0.35f + i * 0.10f),
            leftWingY + bodyRadius * (0.18f + i * 0.05f),
            paint
        )
    }
    paint.color = Color(0xFFF5B041).toArgb()
    paint.alpha = 255
    paint.strokeWidth = 2f
    canvas.drawPath(leftWing, paint)
    paint.style = Paint.Style.FILL
    canvas.restore()

    // ── Right wing ────────────────────────────────────────────────────────
    val rightWingX = cx + bodyRadius * 0.56f
    val rightWingY = cy + bodyRadius * 0.38f
    canvas.save()
    canvas.rotate(6f, rightWingX, rightWingY)
    val rightWing = Path().apply {
        moveTo(rightWingX, rightWingY)
        cubicTo(rightWingX + bodyRadius * 0.25f, rightWingY - bodyRadius * 0.18f, rightWingX + bodyRadius * 0.55f, rightWingY - bodyRadius * 0.10f, rightWingX + bodyRadius * 0.70f, rightWingY + bodyRadius * 0.10f)
        quadTo(rightWingX + bodyRadius * 0.78f, rightWingY + bodyRadius * 0.22f, rightWingX + bodyRadius * 0.60f, rightWingY + bodyRadius * 0.34f)
        quadTo(rightWingX + bodyRadius * 0.25f, rightWingY + bodyRadius * 0.42f, rightWingX, rightWingY)
        close()
    }
    val wingGrad2 = LinearGradient(
        rightWingX, rightWingY - bodyRadius * 0.2f,
        rightWingX + bodyRadius * 0.5f, rightWingY + bodyRadius * 0.3f,
        intArrayOf(Color(0xFFFFF9C4).toArgb(), Color(0xFFFFD54F).toArgb()),
        null, Shader.TileMode.CLAMP
    )
    paint.shader = wingGrad2
    paint.alpha = 255
    canvas.drawPath(rightWing, paint)
    paint.shader = null

    paint.color = Color(0xFFF5B041).copy(alpha = 0.45f).toArgb()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = bodyRadius * 0.015f
    paint.strokeCap = Paint.Cap.ROUND
    repeat(3) { i ->
        canvas.drawLine(
            rightWingX + bodyRadius * 0.12f,
            rightWingY + bodyRadius * 0.08f,
            rightWingX + bodyRadius * (0.35f + i * 0.10f),
            rightWingY + bodyRadius * (0.18f + i * 0.05f),
            paint
        )
    }
    paint.color = Color(0xFFF5B041).toArgb()
    paint.alpha = 255
    paint.strokeWidth = 2f
    canvas.drawPath(rightWing, paint)
    paint.style = Paint.Style.FILL
    canvas.restore()

    // ── Cute Feet ─────────────────────────────────────────────────────────
    fun drawCuteFoot(fx: Float, fy: Float) {
        paint.color = 0xFFFF9800.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = bodyRadius * 0.045f
        paint.strokeCap = Paint.Cap.ROUND
        paint.alpha = 255
        val legLength = bodyRadius * 0.42f
        val toeLength = bodyRadius * 0.18f

        canvas.drawLine(fx, fy - legLength, fx, fy, paint)
        canvas.drawLine(fx, fy, fx - toeLength, fy + toeLength * 0.45f, paint)
        canvas.drawLine(fx, fy, fx, fy + toeLength * 0.75f, paint)
        canvas.drawLine(fx, fy, fx + toeLength, fy + toeLength * 0.45f, paint)

        paint.style = Paint.Style.FILL
        paint.color = 0xFFF57C00.toInt()
        paint.alpha = 255
        val circleRadius = paint.strokeWidth * 0.35f
        canvas.drawCircle(fx - toeLength, fy + toeLength * 0.45f, circleRadius, paint)
        canvas.drawCircle(fx, fy + toeLength * 0.75f, circleRadius, paint)
        canvas.drawCircle(fx + toeLength, fy + toeLength * 0.45f, circleRadius, paint)
    }
    drawCuteFoot(cx - bodyRadius * 0.34f, cy + bodyRadius * 1.30f)
    drawCuteFoot(cx + bodyRadius * 0.34f, cy + bodyRadius * 1.30f)

    // ── Rosy cheeks ───────────────────────────────────────────────────────
    val cheekGrad = RadialGradient(
        cx - bodyRadius * 0.55f, cy + bodyRadius * 0.08f,
        bodyRadius * 0.18f,
        intArrayOf(Color(0xFFFF8A80).copy(alpha = 0.9f).toArgb(), Color(0x00FF8A80).toArgb()),
        null, Shader.TileMode.CLAMP
    )
    paint.shader = cheekGrad
    paint.alpha = 255
    canvas.drawCircle(cx - bodyRadius * 0.55f, cy + bodyRadius * 0.08f, bodyRadius * 0.18f, paint)
    paint.shader = null

    val cheekGrad2 = RadialGradient(
        cx + bodyRadius * 0.55f, cy + bodyRadius * 0.08f,
        bodyRadius * 0.18f,
        intArrayOf(Color(0xFFFF8A80).copy(alpha = 0.9f).toArgb(), Color(0x00FF8A80).toArgb()),
        null, Shader.TileMode.CLAMP
    )
    paint.shader = cheekGrad2
    paint.alpha = 255
    canvas.drawCircle(cx + bodyRadius * 0.55f, cy + bodyRadius * 0.08f, bodyRadius * 0.18f, paint)
    paint.shader = null

    // ── Eyes ──────────────────────────────────────────────────────────────
    val eyeRadius = bodyRadius * 0.12f
    val eyeOffsetX = bodyRadius * 0.42f
    val eyeOffsetY = bodyRadius * 0.28f
    paint.color = white
    paint.alpha = 255
    canvas.drawCircle(cx - eyeOffsetX, cy - eyeOffsetY, eyeRadius, paint)
    canvas.drawCircle(cx + eyeOffsetX, cy - eyeOffsetY, eyeRadius, paint)
    paint.color = darkFace
    paint.alpha = 255
    canvas.drawCircle(cx - eyeOffsetX, cy - eyeOffsetY, eyeRadius * 0.45f, paint)
    canvas.drawCircle(cx + eyeOffsetX, cy - eyeOffsetY, eyeRadius * 0.45f, paint)
    paint.color = white
    paint.alpha = 255
    canvas.drawCircle(cx - eyeOffsetX - eyeRadius * 0.15f, cy - eyeOffsetY - eyeRadius * 0.15f, eyeRadius * 0.18f, paint)
    canvas.drawCircle(cx + eyeOffsetX - eyeRadius * 0.15f, cy - eyeOffsetY - eyeRadius * 0.15f, eyeRadius * 0.18f, paint)

    // ── Beak ──────────────────────────────────────────────────────────────
    val beakGap = bodyRadius * 0.10f * (if (state == MascotWidgetState.Celebrating) 0.45f else 0.25f)
    val upperBeak = Path().apply {
        moveTo(cx - bodyRadius * 0.15f, cy + bodyRadius * 0.08f)
        quadTo(cx, cy, cx + bodyRadius * 0.15f, cy + bodyRadius * 0.08f)
        quadTo(cx, cy + bodyRadius * 0.18f, cx - bodyRadius * 0.15f, cy + bodyRadius * 0.08f)
        close()
    }
    val lowerBeak = Path().apply {
        moveTo(cx - bodyRadius * 0.12f, cy + bodyRadius * 0.16f + beakGap)
        quadTo(cx, cy + bodyRadius * 0.34f + beakGap, cx + bodyRadius * 0.12f, cy + bodyRadius * 0.16f + beakGap)
        quadTo(cx, cy + bodyRadius * 0.24f + beakGap, cx - bodyRadius * 0.12f, cy + bodyRadius * 0.16f + beakGap)
        close()
    }
    if (beakGap > 0f) {
        paint.color = Color(0xFF5D4037).toArgb()
        paint.alpha = 255
        canvas.drawOval(
            RectF(cx - bodyRadius * 0.06f, cy + bodyRadius * 0.14f, cx + bodyRadius * 0.06f, cy + bodyRadius * 0.14f + Math.max(bodyRadius * 0.03f, beakGap * 1.5f)),
            paint
        )
    }
    val beakGrad1 = LinearGradient(cx, cy + bodyRadius * 0.08f, cx, cy + bodyRadius * 0.18f, 0xFFFFD54F.toInt(), 0xFFFFA000.toInt(), Shader.TileMode.CLAMP)
    paint.shader = beakGrad1
    paint.alpha = 255
    canvas.drawPath(upperBeak, paint)
    val beakGrad2 = LinearGradient(cx, cy + bodyRadius * 0.16f, cx, cy + bodyRadius * 0.34f, 0xFFFFB74D.toInt(), 0xFFF57C00.toInt(), Shader.TileMode.CLAMP)
    paint.shader = beakGrad2
    paint.alpha = 255
    canvas.drawPath(lowerBeak, paint)
    paint.shader = null

    paint.color = Color.White.copy(alpha = 0.35f).toArgb()
    canvas.drawOval(
        RectF(cx - bodyRadius * 0.07f, cy + bodyRadius * 0.05f, cx + bodyRadius * 0.01f, cy + bodyRadius * 0.09f),
        paint
    )

    paint.color = Color(0xFFE65100).toArgb()
    paint.alpha = 255
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2f
    canvas.drawPath(upperBeak, paint)
    canvas.drawPath(lowerBeak, paint)
    paint.style = Paint.Style.FILL

    // ── Hair tuft ─────────────────────────────────────────────────────────
    canvas.save()
    val tuft = Path().apply {
        moveTo(cx, cy - bodyRadius * 1.05f)
        quadTo(cx - bodyRadius * 0.18f, cy - bodyRadius * 1.42f, cx - bodyRadius * 0.04f, cy - bodyRadius * 1.30f)
        quadTo(cx, cy - bodyRadius * 1.52f, cx + bodyRadius * 0.04f, cy - bodyRadius * 1.30f)
        quadTo(cx + bodyRadius * 0.18f, cy - bodyRadius * 1.42f, cx, cy - bodyRadius * 1.05f)
        close()
    }
    paint.color = Color(0xFFE65100).toArgb()
    paint.alpha = 255
    canvas.drawPath(tuft, paint)
    paint.color = Color(0xFFBF360C).toArgb()
    paint.alpha = 255
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = bodyRadius * 0.02f
    canvas.drawPath(tuft, paint)
    paint.style = Paint.Style.FILL
    canvas.restore()

    // ── Expressions ───────────────────────────────────────────────────────
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = bodyRadius * 0.048f
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = darkFace
    paint.alpha = 255

    when (state) {
        MascotWidgetState.Celebrating -> {
            canvas.drawArc(
                RectF(cx - bodyRadius * 0.28f, cy + bodyRadius * 0.30f, cx + bodyRadius * 0.28f, cy + bodyRadius * 0.56f),
                15f, 150f, false, paint
            )
            canvas.drawArc(RectF(cx - eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.4f, cx - eyeOffsetX + eyeRadius, cy - eyeOffsetY + eyeRadius * 0.7f), 0f, 180f, false, paint)
            canvas.drawArc(RectF(cx + eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.4f, cx + eyeOffsetX + eyeRadius, cy - eyeOffsetY + eyeRadius * 0.7f), 0f, 180f, false, paint)
            paint.style = Paint.Style.FILL
            paint.color = accentArgb
            paint.alpha = 255
            repeat(6) { i ->
                val angle = Math.toRadians(i * 60.0)
                canvas.drawCircle(
                    (cx + Math.cos(angle) * bodyRadius * 1.45f).toFloat(),
                    (cy - bodyRadius * 0.2f + Math.sin(angle) * bodyRadius * 0.6f).toFloat(),
                    bodyRadius * 0.06f, paint
                )
            }
        }
        MascotWidgetState.Happy -> {
            canvas.drawArc(
                RectF(cx - bodyRadius * 0.22f, cy + bodyRadius * 0.32f, cx + bodyRadius * 0.22f, cy + bodyRadius * 0.50f),
                10f, 160f, false, paint
            )
        }
        MascotWidgetState.Encouraging -> {
            canvas.drawArc(
                RectF(cx - bodyRadius * 0.18f, cy + bodyRadius * 0.32f, cx + bodyRadius * 0.18f, cy + bodyRadius * 0.46f),
                10f, 160f, false, paint
            )
            canvas.drawLine(cx - eyeOffsetX * 1.2f, cy - eyeOffsetY * 1.9f, cx - eyeOffsetX * 0.5f, cy - eyeOffsetY * 2.1f, paint)
            canvas.drawLine(cx + eyeOffsetX * 0.5f, cy - eyeOffsetY * 2.1f, cx + eyeOffsetX * 1.2f, cy - eyeOffsetY * 1.9f, paint)
        }
    }
    paint.style = Paint.Style.FILL

    return bitmap
}