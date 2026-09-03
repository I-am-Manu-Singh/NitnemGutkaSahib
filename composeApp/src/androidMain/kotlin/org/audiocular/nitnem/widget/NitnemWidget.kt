package org.audiocular.nitnem.widget

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
import org.audiocular.nitnem.MainActivity
import org.audiocular.nitnem.app.ui.theme.resolveBaniTheme
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

    // ── State-derived values ───────────────────────────────────────────────
    val totalBanis      = if (totalPref == 5 || totalPref <= 0) 7 else totalPref
    val completedCount  = completedPref.coerceIn(0, totalBanis)
    val progress        = completedCount.toFloat() / totalBanis

    val headerTitle: String
    val subtitleMsg: String
    val detailMsg  : String

    if (completedCount >= totalBanis) {
        headerTitle = "Nitnem Complete"
        subtitleMsg = when {
            streakDays >= 30 -> "Sensational! $streakDays-day streak! Guru Kirpa! 🌟"
            streakDays >= 10 -> "Incredible! $streakDays-day streak! Bani is in bliss! ✨"
            streakDays > 0 -> "Bani is so proud of you! 🎉 $streakDays-day streak alive!"
            else -> "Nitnem Complete! Bani is so proud of you! 🎉 Waheguru!"
        }
        detailMsg   = "Daily target achieved!"
    } else {
        headerTitle = recommendedBani
        subtitleMsg = when {
            completedCount > 0 -> "You're on track ($completedCount/$totalBanis)! 🌟 Bani is cheering you on."
            hour >= 21 -> "Night has arrived... 🌙 Don't miss Kirtan Sohila tonight!"
            hour >= 17 -> "Sunset is here! 🌇 Quick, do Rehras Sahib with Bani!"
            hour in 3..6 -> "Amrit Vela bliss! 🌅 Start your morning path with Japji Sahib."
            streakDays == 0 -> "Bani is missing you... 💔 Tap to revive your daily streak!"
            streakDays > 0 -> "Keep your $streakDays-day streak burning! 🔥 Bani is waiting."
            else -> "Bani misses you... 💔 Tap to start your Nitnem today!"
        }
        detailMsg   = "$completedCount / $totalBanis banis"
    }

    // ── Responsive constraints ─────────────────────────────────────────────
    val size = LocalSize.current
    val isSmallWidth = size.width < 150.dp
    val isSmallHeight = size.height < 90.dp
    val isLarge = size.width >= 260.dp
    val isUltraSmall = size.width < 110.dp || size.height < 70.dp

    val widthDp = size.width.value.toInt().coerceAtLeast(100)
    val heightDp = size.height.value.toInt().coerceAtLeast(60)

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
            .cornerRadius(24.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Image(
            provider = ImageProvider(bgBitmap),
            contentDescription = null,
            modifier = GlanceModifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        if (isUltraSmall) {
            Box(
                modifier = GlanceModifier.fillMaxSize().padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (widgetContent == "Mascot Assistant") {
                    Image(
                        provider = ImageProvider(mascotBitmap),
                        contentDescription = "Mascot",
                        modifier = GlanceModifier.size(36.dp)
                    )
                } else {
                    Text(
                        text = "ੴ",
                        style = TextStyle(color = ColorProvider(accent), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }
        } else {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(horizontal = if (isSmallWidth) 8.dp else 14.dp, vertical = if (isSmallHeight) 6.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSmallWidth) {
                    Column(
                        modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (widgetContent == "Mascot Assistant") {
                            Image(
                                provider = ImageProvider(mascotBitmap),
                                contentDescription = "Mascot",
                                modifier = GlanceModifier.size(38.dp)
                            )
                            Spacer(GlanceModifier.height(2.dp))
                            Text(
                                text = "$completedCount/$totalBanis Banis",
                                style = TextStyle(color = ColorProvider(accent), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            )
                        } else {
                            Text(text = "✨", style = TextStyle(fontSize = 16.sp))
                            Spacer(GlanceModifier.height(2.dp))
                            Text(
                                text = "ੴ",
                                style = TextStyle(color = ColorProvider(textPrimary), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                } else {
                    val mascotSize = if (isLarge) 68.dp else 60.dp
                    val titleFontSize = if (isLarge) 16.sp else 14.sp
                    val descFontSize = if (isLarge) 12.sp else 11.sp
                    if (widgetContent == "Mascot Assistant") {
    Row(
        modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(mascotBitmap),
                contentDescription = "Bani mascot",
                modifier = GlanceModifier.size(mascotSize)
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = "$completedCount/$totalBanis Banis",
                style = TextStyle(
                    color = ColorProvider(accent),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(GlanceModifier.width(10.dp))

        Column(modifier = GlanceModifier.defaultWeight()) {
            // Inner row pushing elements to each end
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
                
                // This weight spacer pushes the next text to the far right end
                Spacer(modifier = GlanceModifier.defaultWeight())

                Text(
                    text = detailMsg,
                    style = TextStyle(
                        color = ColorProvider(textSecondary.copy(alpha = 0.7f)),
                        fontSize = 10.sp
                    ),
                    maxLines = 1
                )
            }

            Text(
                text = subtitleMsg,
                style = TextStyle(
                    color = ColorProvider(textSecondary),
                    fontSize = descFontSize
                ),
                maxLines = 2
            )

            Spacer(GlanceModifier.height(4.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                modifier = GlanceModifier.fillMaxWidth().height(4.dp).cornerRadius(2.dp),
                color = ColorProvider(accent),
                backgroundColor = ColorProvider(if (isLight) Color(0x33000000) else Color(0x33FFFFFF))
            )
        }
    }
}
                    else {
                        Row(
                            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = GlanceModifier
                                    .size(if (isLarge) 52.dp else 46.dp)
                                    .background(ColorProvider(accent.copy(alpha = 0.15f)))
                                    .cornerRadius(if (isLarge) 26.dp else 23.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✨",
                                    style = TextStyle(fontSize = if (isLarge) 22.sp else 18.sp)
                                )
                            }

                            Spacer(GlanceModifier.width(10.dp))

                            Column(modifier = GlanceModifier.defaultWeight()) {
                                Text(
                                    text = "ੴ Satnam Waheguru",
                                    style = TextStyle(
                                        color = ColorProvider(textPrimary),
                                        fontSize = titleFontSize,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1
                                )

                                Spacer(GlanceModifier.height(2.dp))

                                Text(
                                    text = "Upcoming: $recommendedBani",
                                    style = TextStyle(
                                        color = ColorProvider(textSecondary),
                                        fontSize = descFontSize
                                    ),
                                    maxLines = 1
                                )

                                Spacer(GlanceModifier.height(2.dp))

                                Text(
                                    text = "$completedCount of $totalBanis completed",
                                    style = TextStyle(
                                        color = ColorProvider(textSecondary.copy(alpha = 0.7f)),
                                        fontSize = 10.sp
                                    ),
                                    maxLines = 1
                                )

                                Spacer(GlanceModifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = progress,
                                    modifier = GlanceModifier.fillMaxWidth().height(4.dp).cornerRadius(2.dp),
                                    color = ColorProvider(accent),
                                    backgroundColor = ColorProvider(if (isLight) Color(0x33000000) else Color(0x33FFFFFF))
                                )
                            }
                        }
                    }
                }

                if (!isSmallHeight && isLarge) {
                    Spacer(GlanceModifier.height(4.dp))
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorProvider(accent.copy(alpha = 0.2f)))
                    ) {}
                    Spacer(GlanceModifier.height(4.dp))
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
                        val cal = Calendar.getInstance()
                        val day = cal.get(Calendar.DAY_OF_WEEK)
                        val currentDayIndex = when (day) {
                            Calendar.MONDAY -> 0
                            Calendar.TUESDAY -> 1
                            Calendar.WEDNESDAY -> 2
                            Calendar.THURSDAY -> 3
                            Calendar.FRIDAY -> 4
                            Calendar.SATURDAY -> 5
                            else -> 6
                        }
                        
                        weekdays.forEachIndexed { index, dayName ->
                            val isToday = index == currentDayIndex
                            val isCompleted = index < currentDayIndex || (isToday && progress >= 1f)
                            
                            Row {
                                if (index > 0) Spacer(GlanceModifier.width(6.dp))
                                Column(
                                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
                                ) {
                                    Text(
                                        text = dayName,
                                        style = TextStyle(
                                            color = ColorProvider(if (isToday) accent else textSecondary.copy(alpha = 0.6f)),
                                            fontSize = 8.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                    Spacer(GlanceModifier.height(1.dp))
                                    Box(
                                        modifier = GlanceModifier
                                            .size(14.dp)
                                            .background(
                                                ColorProvider(
                                                    if (isCompleted) accent.copy(alpha = 0.2f)
                                                    else textSecondary.copy(alpha = 0.08f)
                                                )
                                            )
                                            .cornerRadius(7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Text(
                                                text = "✓",
                                                style = TextStyle(color = ColorProvider(accent), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            )
                                        } else {
                                            Box(
                                                modifier = GlanceModifier
                                                    .size(3.dp)
                                                    .background(
                                                        ColorProvider(
                                                            if (isToday) accent.copy(alpha = 0.6f)
                                                            else textSecondary.copy(alpha = 0.3f)
                                                        )
                                                    )
                                                    .cornerRadius(1.5.dp)
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
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dynamic background generator (matching HeroCard sky styling)
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
        // Cream/Gold gradient matching the app Light theme / HeroCard
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

        // Light Theme Gold Border
        paint.color = 0xFFFFC107.toInt()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3.5f * density
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.style = Paint.Style.FILL
    } else {
        // Dark Theme gradients
        val skyColors = when {
            // Amrit Vela: deep twilight
            hour in 3..5 -> intArrayOf(
                0xFF030712.toInt(),
                0xFF0F172A.toInt(),
                0xFF1E1B4B.toInt(),
                0xFF31102F.toInt()
            )
            // Sunrise: golden-orange-purple
            hour in 6..8 -> intArrayOf(
                0xFF4C1D95.toInt(),
                0xFF8B5CF6.toInt(),
                0xFFEC4899.toInt(),
                0xFFF59E0B.toInt()
            )
            // Day: deep serene navy-sapphire to preserve text contrast
            hour in 9..16 -> intArrayOf(
                0xFF0C2444.toInt(),
                0xFF103663.toInt(),
                0xFF1A467D.toInt(),
                0xFF132F53.toInt()
            )
            // Sunset: dramatic deep crimson/purple/indigo
            hour in 17..18 -> intArrayOf(
                0xFF2D0E2B.toInt(),
                0xFF5B1736.toInt(),
                0xFF4A154B.toInt(),
                0xFF1E1035.toInt()
            )
            // Twilight / Early Night: dark violet-indigo
            hour in 19..20 -> intArrayOf(
                0xFF0B0F19.toInt(),
                0xFF1E1B4B.toInt(),
                0xFF111827.toInt(),
                0xFF030712.toInt()
            )
            // Late Night: space black-blue
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

        // Draw shining stars at night
        if (isNight) {
            paint.color = 0x66FFFFFF
            val randomX = floatArrayOf(width * 0.12f, width * 0.34f, width * 0.52f, width * 0.76f, width * 0.92f)
            val randomY = floatArrayOf(height * 0.18f, height * 0.28f, height * 0.15f, height * 0.32f, height * 0.22f)
            repeat(5) { i ->
                canvas.drawCircle(randomX[i], randomY[i], 1.5f * density, paint)
            }
        }
    }

    // Draw Sun/Moon corona and body (proportional to widget dimensions to prevent stretching)
    val sunMoonX = width * 0.88f
    val sunMoonY = height * 0.20f
    val baseRadius = Math.min(width, height) * 0.08f

    val showMoon = isNight && widgetTheme == "Dark"

    if (showMoon) {
        // Lunar glow with reduced opacity
        paint.color = 0x08FFFFFF
        canvas.drawCircle(sunMoonX, sunMoonY, baseRadius * 2.0f, paint)
        paint.color = 0x14FFFFFF
        canvas.drawCircle(sunMoonX, sunMoonY, 1.4f * baseRadius, paint)
        
        // Crescent moon drawing path
        val moonPath = android.graphics.Path()
        moonPath.addCircle(sunMoonX, sunMoonY, baseRadius, android.graphics.Path.Direction.CW)
        val shadowPath = android.graphics.Path()
        shadowPath.addCircle(sunMoonX - baseRadius * 0.5f, sunMoonY - baseRadius * 0.15f, baseRadius * 0.95f, android.graphics.Path.Direction.CW)
        moonPath.op(shadowPath, android.graphics.Path.Op.DIFFERENCE)
        
        paint.color = 0xAAFAFBFD.toInt()
        canvas.drawPath(moonPath, paint)
    } else {
        // Solar glow with soft opacity
        val sunColor = when {
            widgetTheme == "Light" -> 0x88FFA000.toInt()
            hour in 6..8 -> 0x88F59E0B.toInt()
            hour in 17..18 -> 0x88E11D48.toInt()
            else -> 0x66FFFFFF.toInt()
        }
        val glowColor = if (widgetTheme == "Light") 0x0D000000 else 0x0CFFFFFF
        val glowColor2 = if (widgetTheme == "Light") 0x06000000 else 0x18FFFFFF
        paint.color = glowColor
        canvas.drawCircle(sunMoonX, sunMoonY, baseRadius * 2.2f, paint)
        paint.color = glowColor2
        canvas.drawCircle(sunMoonX, sunMoonY, baseRadius * 1.5f, paint)
        
        // Solar body
        paint.color = sunColor
        canvas.drawCircle(sunMoonX, sunMoonY, baseRadius, paint)
        
        // Radiant rays matching app ones with subtle opacity
        paint.color = sunColor
        paint.strokeWidth = 1.5f * density
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        for (i in 0 until 8) {
            val angle = (i * 2 * Math.PI / 8)
            val startRad = baseRadius * 1.2f
            val endRad = baseRadius * 1.45f
            val startX = sunMoonX + startRad * Math.cos(angle).toFloat()
            val startY = sunMoonY + startRad * Math.sin(angle).toFloat()
            val endX = sunMoonX + endRad * Math.cos(angle).toFloat()
            val endY = sunMoonY + endRad * Math.sin(angle).toFloat()
            canvas.drawLine(startX, startY, endX, endY, paint)
        }
        paint.style = Paint.Style.FILL
    }

    // Draw dynamic progress bar directly at the bottom border of the background bitmap
    if (progress > 0f) {
        paint.color = 0xFFFFC107.toInt() // Gold Accent Color
        paint.style = Paint.Style.FILL
        val barHeight = 5.5f * density
        val progressWidth = width * progress.coerceIn(0f, 1f)
        canvas.drawRect(0f, height - barHeight, progressWidth, height.toFloat(), paint)
    }

    return bitmap
}

// ─────────────────────────────────────────────────────────────────────────────
// Mascot states for the widget
// ─────────────────────────────────────────────────────────────────────────────

private enum class MascotWidgetState { Encouraging, Happy, Celebrating }

// ─────────────────────────────────────────────────────────────────────────────
// Canvas-drawn mascot — matches in-app golden owl/chick design with feathers & feet
// ─────────────────────────────────────────────────────────────────────────────

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

    // ── Underlay Glow (matching in-app design) ────────────────────────────
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

    // IMPORTANT: Reset paint to opaque so transparency doesn't bleed into wings/eyes/beak/feet
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

    // wing lines
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

    // wing lines
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

    // ── Cute Feet (matching app exactly with circle toes) ──────────────────
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
    // Whites
    paint.color = white
    paint.alpha = 255
    canvas.drawCircle(cx - eyeOffsetX, cy - eyeOffsetY, eyeRadius, paint)
    canvas.drawCircle(cx + eyeOffsetX, cy - eyeOffsetY, eyeRadius, paint)
    // Pupils
    paint.color = darkFace
    paint.alpha = 255
    canvas.drawCircle(cx - eyeOffsetX, cy - eyeOffsetY, eyeRadius * 0.45f, paint)
    canvas.drawCircle(cx + eyeOffsetX, cy - eyeOffsetY, eyeRadius * 0.45f, paint)
    // Gleam
    paint.color = white
    paint.alpha = 255
    canvas.drawCircle(cx - eyeOffsetX - eyeRadius * 0.15f, cy - eyeOffsetY - eyeRadius * 0.15f, eyeRadius * 0.18f, paint)
    canvas.drawCircle(cx + eyeOffsetX - eyeRadius * 0.15f, cy - eyeOffsetY - eyeRadius * 0.15f, eyeRadius * 0.18f, paint)

    // ── Beak (with white highlight) ───────────────────────────────────────
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

    // White beak highlight
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

    // ── Hair tuft (rotated) ───────────────────────────────────────────────
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
            // Happy smile
            canvas.drawArc(
                RectF(cx - bodyRadius * 0.28f, cy + bodyRadius * 0.30f, cx + bodyRadius * 0.28f, cy + bodyRadius * 0.56f),
                15f, 150f, false, paint
            )
            // squints
            canvas.drawArc(RectF(cx - eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.4f, cx - eyeOffsetX + eyeRadius, cy - eyeOffsetY + eyeRadius * 0.7f), 0f, 180f, false, paint)
            canvas.drawArc(RectF(cx + eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.4f, cx + eyeOffsetX + eyeRadius, cy - eyeOffsetY + eyeRadius * 0.7f), 0f, 180f, false, paint)
            // Sparkles
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
            // Raised eyebrows
            canvas.drawLine(cx - eyeOffsetX * 1.2f, cy - eyeOffsetY * 1.9f, cx - eyeOffsetX * 0.5f, cy - eyeOffsetY * 2.1f, paint)
            canvas.drawLine(cx + eyeOffsetX * 0.5f, cy - eyeOffsetY * 2.1f, cx + eyeOffsetX * 1.2f, cy - eyeOffsetY * 1.9f, paint)
        }
    }
    paint.style = Paint.Style.FILL

    return bitmap
}

private fun dateString(): String =
    SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date())