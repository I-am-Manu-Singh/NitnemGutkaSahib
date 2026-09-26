package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * RollingNumberText displays numbers that slide vertically like a suitcase combination lock / odometer
 * when the value changes or increases.
 */
@Composable
fun RollingNumberText(
    value: Any,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    style: TextStyle = TextStyle.Default
) {
    val stringValue = value.toString()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefix.isNotEmpty()) {
            Text(
                text = prefix,
                color = color,
                fontSize = fontSize,
                fontWeight = fontWeight,
                style = style
            )
        }

        for (i in stringValue.indices) {
            val char = stringValue[i]
            if (char.isDigit()) {
                val digit = char.digitToInt()
                key(i) {
                    AnimatedContent(
                        targetState = digit,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInVertically(
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) { height -> height } + fadeIn(animationSpec = tween(400))) togetherWith
                                        (slideOutVertically(
                                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                                        ) { height -> -height } + fadeOut(animationSpec = tween(400)))
                            } else {
                                (slideInVertically(
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) { height -> -height } + fadeIn(animationSpec = tween(400))) togetherWith
                                        (slideOutVertically(
                                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                                        ) { height -> height } + fadeOut(animationSpec = tween(400)))
                            }
                        },
                        label = "SuitcasePinDigit_$i"
                    ) { targetDigit ->
                        Text(
                            text = targetDigit.toString(),
                            color = color,
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            style = style
                        )
                    }
                }
            } else {
                Text(
                    text = char.toString(),
                    color = color,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    style = style
                )
            }
        }

        if (suffix.isNotEmpty()) {
            Text(
                text = suffix,
                color = color,
                fontSize = fontSize,
                fontWeight = fontWeight,
                style = style
            )
        }
    }
}
