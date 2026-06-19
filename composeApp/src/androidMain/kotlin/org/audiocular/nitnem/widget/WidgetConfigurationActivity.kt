package org.audiocular.nitnem.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.audiocular.nitnem.app.ui.theme.*

class WidgetConfigurationActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED. This will cause the host to cancel
        // the widget placement if the user presses back.
        setResult(RESULT_CANCELED)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an invalid widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            WidgetConfigurationScreen(
                onSave = { theme, content ->
                    saveWidgetSettings(theme, content)
                },
                onCancel = {
                    finish()
                }
            )
        }
    }

    private fun saveWidgetSettings(widgetTheme: String, widgetContent: String) {
        val prefs = getSharedPreferences("nitnem_widget_state", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("widget_theme", widgetTheme)
            .putString("widget_content", widgetContent)
            .apply()

        // Request widget update
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope.launch {
            try {
                val settingsRepository = org.audiocular.nitnem.data.preferences.SettingsRepository(applicationContext)
                settingsRepository.setWidgetTheme(widgetTheme)
                settingsRepository.setWidgetContent(widgetContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                NitnemWidget().updateAll(applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Set the result to OK and finish
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            runOnUiThread {
                setResult(RESULT_OK, resultValue)
                finish()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetConfigurationScreen(
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("nitnem_widget_state", Context.MODE_PRIVATE)
    }

    var selectedTheme by remember {
        mutableStateOf(prefs.getString("widget_theme", "Dark") ?: "Dark")
    }
    var selectedContent by remember {
        mutableStateOf(prefs.getString("widget_content", "Mascot Assistant") ?: "Mascot Assistant")
    }

    val isLight = AppTheme.themeMode == ThemeMode.Light

    val backgroundGradient = when (AppTheme.themeMode) {
        ThemeMode.Light -> listOf(Cream, Mist, Gold.copy(alpha = 0.10f))
        ThemeMode.Dark -> listOf(Color(0xFF0E1014), Color(0xFF171A20), Color(0xFF1F242B))
        ThemeMode.Amoled -> listOf(Color.Black, Color(0xFF040404), Color(0xFF090909))
    }

    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)
    val cardColor = if (isLight) Color.White.copy(alpha = 0.86f) else Color.White.copy(alpha = 0.06f)
    val borderColor = if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.06f)

    Scaffold(
        bottomBar = {
            Surface(
                color = if (isLight) Color.White else Color(0xFF13151A),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Gold),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onSave(selectedTheme, selectedContent) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Text("Save & Add", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(backgroundGradient))
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Widget Setup",
                color = primaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Personalize your home screen widget style and contents.",
                color = secondaryText,
                fontSize = 14.sp
            )

            // Preview
            WidgetMockPreview(
                widgetTheme = selectedTheme,
                widgetContent = selectedContent
            )

            Text(
                text = "Widget Theme",
                color = primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val themes = listOf("Light", "Dark")
                themes.forEach { themeName ->
                    val selected = selectedTheme == themeName
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTheme = themeName },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) Accent.copy(alpha = 0.15f) else cardColor,
                        border = BorderStroke(1.dp, if (selected) Accent else borderColor)
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = themeName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Accent else primaryText
                            )
                        }
                    }
                }
            }

            Text(
                text = "Widget Content",
                color = primaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val contents = listOf("Mascot Assistant", "Meditation & Quote")
                contents.forEach { contentName ->
                    val selected = selectedContent == contentName
                    val labelName = if (contentName == "Meditation & Quote") "Reflection & Progress" else contentName
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedContent = contentName },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) Accent.copy(alpha = 0.15f) else cardColor,
                        border = BorderStroke(1.dp, if (selected) Accent else borderColor)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = labelName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Accent else primaryText
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetMockPreview(
    widgetTheme: String,
    widgetContent: String
) {
    val (bg, border, textPrimary, textSecondary, accentColor) = when (widgetTheme) {
        "Light" -> Quintuple(Color(0xFFFDFBF7), BorderStroke(1.dp, Gold.copy(alpha = 0.5f)), Primary, TextPrimary.copy(alpha = 0.70f), Gold)
        else -> Quintuple(Color(0xFF0F1115), BorderStroke(1.dp, Gold), Color.White, Color.White.copy(alpha = 0.65f), Gold)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().height(132.dp),
        shape = RoundedCornerShape(24.dp),
        color = bg,
        border = border
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            when (widgetContent) {
                "Mascot Assistant" -> {
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(56.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Brush.radialGradient(listOf(accentColor.copy(alpha = 0.55f), accentColor.copy(alpha = 0.20f)))), contentAlignment = Alignment.Center) {
                            Text("🦅", fontSize = 28.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Daily Nitnem", color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(3.dp))
                            Text(text = "Begin your Nitnem today 🌅", color = textSecondary, fontSize = 11.sp)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Japji", "Jaap", "Chaupai").forEach { name ->
                                    Box(modifier = Modifier.background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(name, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 5.dp)) {
                            Text("🔥 0", color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(44.dp).clip(androidx.compose.foundation.shape.CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "ੴ Satnam Waheguru", color = textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text(text = "Reflection • Upcoming: Japji Sahib", color = textSecondary, fontSize = 11.sp)
                        }
                        Box(modifier = Modifier.background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(100.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                            Text("🔥 0/5", color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
