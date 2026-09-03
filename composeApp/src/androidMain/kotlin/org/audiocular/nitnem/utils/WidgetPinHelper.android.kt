package org.audiocular.nitnem.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.audiocular.nitnem.widget.NitnemJourneyWidgetProvider

@Composable
actual fun rememberPinWidgetLauncher(): () -> Unit {
    val context = LocalContext.current
    return remember(context) {
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                val provider = ComponentName(context, NitnemJourneyWidgetProvider::class.java)
                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    appWidgetManager.requestPinAppWidget(provider, null, null)
                } else {
                    Toast.makeText(context, "Long-press your home screen to add the Nitnem widget.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Long-press your home screen to add the Nitnem widget.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
