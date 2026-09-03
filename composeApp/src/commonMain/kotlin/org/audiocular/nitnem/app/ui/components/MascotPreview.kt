package org.audiocular.nitnem.app.ui.components

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun AmritVelaMascotPreview() {
    Box(modifier = Modifier.size(300.dp)) {
        MascotBani(state = MascotState.AmritVela, modifier = Modifier.size(300.dp))
    }
}
