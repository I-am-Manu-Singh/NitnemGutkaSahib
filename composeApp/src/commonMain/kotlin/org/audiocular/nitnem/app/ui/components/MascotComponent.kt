package org.audiocular.nitnem.app.ui.components

import androidx.compose.animation.core.*
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap

private val Gold = Color(0xFFFFC107)
private val FaceColor = Color(0xFF2D2D2D)

enum class MascotState {
    Idle, Happy, Thinking, Meditating, Reading, Listening, Sleeping, Oops, Celebrating
}

@Composable
fun MascotBani(
    state: MascotState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val mouthOpen by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mouth"
    )
    
    val readingBob by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "readingBob"
    )
    
    val thinkingTilt by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thinkingTilt"
    )
    
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    
    val readingPupil by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = -0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "readingPupil"
    )
    
    val bookOpen by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "book"
    )
    
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )
    
    val hairSway by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hair"
    )

    val footWiggle by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "foot"
    )
    
    val eyebrowOffset by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eyebrow"
    )
    
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    val zzzAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "zzz"
    )

    val tiltAngle by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt"
    )
    
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000
                1f at 0
                1f at 4300
                0.05f at 4400
                0.05f at 4475
                1f at 4550
                1f at 5000
            }
        ),
        label = "blink"
    )
    
    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wingFlap"
    )
    
    val pupilOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pupil"
    )

    Canvas(
        modifier = modifier.graphicsLayer {
            translationY = if (state == MascotState.Reading) floatAnim + readingBob else floatAnim
            
            when (state) {
                MascotState.Sleeping -> {
                    scaleX = breathingScale
                    scaleY = breathingScale
                }
                MascotState.Celebrating -> {
                    scaleX = 1f + (breathingScale - 1f) * 1.5f
                    scaleY = 1f + (breathingScale - 1f) * 1.5f
                    rotationZ = tiltAngle * 2.5f
                }
                MascotState.Thinking -> {
                    rotationZ = thinkingTilt
                }
                else -> {
                    scaleX = 1f + (breathingScale - 1f) * 0.4f
                    scaleY = 1f + (breathingScale - 1f) * 0.4f
                }
            }
        }
    ) { 
        val cx = size.width / 2f
        val cy = size.height / 2f

        val bodyRadius = size.minDimension * 0.35f
        val mouthWidth = bodyRadius * 0.9f
        val mouthHeight = bodyRadius * 0.5f
        val eyeRadius = bodyRadius * 0.12f
        val eyeOffsetX = bodyRadius * 0.42f
        val eyeOffsetY = bodyRadius * 0.28f

        val wingOffset = when (state) {
            MascotState.Reading -> -25f
            MascotState.Happy, MascotState.Celebrating -> wingFlap
            MascotState.Listening -> wingFlap * 0.4f
            else -> 0f
        }
        
        drawMascotBody(
            cx = cx,
            cy = cy, 
            bodyRadius = bodyRadius,
            wingFlap = wingOffset,
            hairSway = hairSway,
            footWiggle = footWiggle,
            beakOpenAmount = when (state) {
                MascotState.Listening, MascotState.Thinking -> 0.4f
                MascotState.Happy -> 0.25f
                MascotState.Celebrating -> 0.45f
                MascotState.Reading -> 0.15f
                MascotState.Idle -> mouthOpen * 1.1f
                else -> 0f
            },
            showBeak = state != MascotState.Sleeping,
            isReadingState = state == MascotState.Reading,
            drawWingsOnly = false
        )

        when (state) {
            MascotState.Idle -> {
                drawOpenEyes(cx, cy, eyeOffsetX, pupilOffset, eyeOffsetY, eyeRadius, blinkProgress)
            }
            MascotState.Happy -> {
                drawHappyEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.10f)
                drawSmile(cx, cy, bodyRadius * 0.28f, bodyRadius * 0.10f, bodyRadius * 0.025f)
            }
            MascotState.Sleeping -> {
                drawSleepingEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.045f)
                drawArc(
                    color = FaceColor, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(cx - bodyRadius * 0.05f, cy + bodyRadius * 0.25f),
                    size = Size(bodyRadius * 0.10f, bodyRadius * 0.035f),
                    style = Stroke(width = bodyRadius * 0.02f, cap = StrokeCap.Round)
                )

                val z1Progress = (zzzAnim + 0.0f) % 1f
                val z2Progress = (zzzAnim + 0.33f) % 1f
                val z3Progress = (zzzAnim + 0.66f) % 1f
                val startX = cx + bodyRadius * 0.55f
                val startY = cy - bodyRadius * 0.45f
                val travel = bodyRadius * 0.8f
                val sway = bodyRadius * 0.08f

                drawZ(startX + sin(z1Progress * PI.toFloat() * 2f) * sway, startY - z1Progress * travel, bodyRadius * 0.30f, (1f - z1Progress) * 0.9f, bodyRadius * 0.06f)
                drawZ(startX + sin(z2Progress * PI.toFloat() * 2f) * sway, startY - z2Progress * travel, bodyRadius * 0.38f, (1f - z2Progress) * 0.9f, bodyRadius * 0.08f)
                drawZ(startX + sin(z3Progress * PI.toFloat() * 2f) * sway, startY - z3Progress * travel, bodyRadius * 0.46f, (1f - z3Progress) * 0.9f, bodyRadius * 0.10f)
            }
                   MascotState.Reading -> {
                // Passed 1f at the end to lower the pupils directly onto the page sightline
                drawOpenEyes(cx, cy, eyeOffsetX, readingPupil, eyeOffsetY, eyeRadius, blinkProgress, 1f)
                drawThinkingEyebrows(cx, cy, eyeOffsetX, eyeOffsetY, bodyRadius, eyebrowOffset * 0.5f)
                drawBook(cx, cy, bodyRadius, bookOpen)
                
                drawMascotBody(
                    cx = cx, cy = cy, bodyRadius = bodyRadius, wingFlap = wingOffset,
                    hairSway = hairSway, footWiggle = footWiggle, beakOpenAmount = 0.15f,
                    showBeak = true, isReadingState = true, drawWingsOnly = true
                )
            }
            MascotState.Listening -> {
                drawHeadphones(cx, cy, bodyRadius, eyeOffsetY)
                drawClosedEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.10f)
            }
            MascotState.Thinking -> {
                drawOpenEyes(cx, cy, eyeOffsetX, pupilOffset, eyeOffsetY, eyeRadius, blinkProgress)
                drawThinkingEyebrows(cx, cy, eyeOffsetX, eyeOffsetY, bodyRadius, eyebrowOffset)
                drawThinkingMouth(cx, cy, bodyRadius)
            }
            MascotState.Meditating -> {
                drawClosedEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.08f)
                drawAura(cx, cy, bodyRadius, auraScale)
                drawArc(
                    color = FaceColor, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(cx - bodyRadius * 0.08f, cy + bodyRadius * 0.28f),
                    size = Size(bodyRadius * 0.16f, bodyRadius * 0.06f),
                    style = Stroke(width = bodyRadius * 0.025f, cap = StrokeCap.Round)
                )
            }
            MascotState.Oops -> {
                drawXEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.08f)
                drawArc(
                    color = FaceColor, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(cx - bodyRadius * 0.10f, cy + bodyRadius * 0.26f),
                    size = Size(bodyRadius * 0.20f, bodyRadius * 0.08f),
                    style = Stroke(width = bodyRadius * 0.028f, cap = StrokeCap.Round)
                )
            }
            MascotState.Celebrating -> {
                drawHappyEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.10f)
                drawSparkles(cx, cy, bodyRadius)
                drawArc(
                    color = FaceColor, startAngle = 15f, sweepAngle = 150f, useCenter = false,
                    topLeft = Offset(cx - bodyRadius * 0.14f, cy + bodyRadius * 0.24f),
                    size = Size(bodyRadius * 0.28f, bodyRadius * 0.10f),
                    style = Stroke(width = bodyRadius * 0.035f, cap = StrokeCap.Round)
                )
            }   
        }
    }
}

private fun DrawScope.drawOpenEyes(
    cx: Float,
    cy: Float,
    eyeOffsetX: Float,
    pupilOffset: Float,
    eyeOffsetY: Float,
    eyeRadius: Float,
    blinkProgress: Float,
    pupilOffsetY: Float = 0f // New parameter for looking down
) {
    val leftEye = Offset(cx - eyeOffsetX, cy - eyeOffsetY)
    val rightEye = Offset(cx + eyeOffsetX, cy - eyeOffsetY)
    val eyeHeight = eyeRadius * 2f * blinkProgress

    // Eyeballs
    drawOval(color = Color.White, topLeft = Offset(leftEye.x - eyeRadius, leftEye.y - eyeHeight / 2f), size = Size(eyeRadius * 2f, eyeHeight.coerceAtLeast(2f)))
    drawOval(color = Color.White, topLeft = Offset(rightEye.x - eyeRadius, rightEye.y - eyeHeight / 2f), size = Size(eyeRadius * 2f, eyeHeight.coerceAtLeast(2f)))

    if (blinkProgress > 0.25f) {
        val actualPupilOffsetX = pupilOffset * eyeRadius * 0.25f
        // Smoothly map vertical gaze constraint within eyeball limits
        val actualPupilOffsetY = pupilOffsetY * eyeRadius * 0.40f 

        // Left pupil
        drawCircle(
            color = FaceColor,
            radius = eyeRadius * 0.45f,
            center = Offset(leftEye.x + actualPupilOffsetX, leftEye.y + actualPupilOffsetY)
        )

        // Right pupil
        drawCircle(
            color = FaceColor,
            radius = eyeRadius * 0.45f,
            center = Offset(rightEye.x + actualPupilOffsetX, rightEye.y + actualPupilOffsetY)
        )

        // Highlights (Shifted with the pupils for consistency)
        drawCircle(
            color = Color.White,
            radius = eyeRadius * 0.18f,
            center = Offset(leftEye.x - eyeRadius * 0.15f + actualPupilOffsetX, leftEye.y - eyeRadius * 0.15f + actualPupilOffsetY)
        )
        drawCircle(
            color = Color.White,
            radius = eyeRadius * 0.18f,
            center = Offset(rightEye.x - eyeRadius * 0.15f + actualPupilOffsetX, rightEye.y - eyeRadius * 0.15f + actualPupilOffsetY)
        )
    } else {
        drawLine(color = FaceColor, start = Offset(leftEye.x - eyeRadius, leftEye.y), end = Offset(leftEye.x + eyeRadius, leftEye.y), strokeWidth = eyeRadius * 0.18f, cap = StrokeCap.Round)
        drawLine(color = FaceColor, start = Offset(rightEye.x - eyeRadius, rightEye.y), end = Offset(rightEye.x + eyeRadius, rightEye.y), strokeWidth = eyeRadius * 0.18f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawClosedEyes(cx: Float, cy: Float, eyeOffsetX: Float, eyeOffsetY: Float, eyeRadius: Float, strokeWidth: Float) {
    drawLine(color = Color.Black, start = Offset(cx - eyeOffsetX - eyeRadius, cy - eyeOffsetY), end = Offset(cx - eyeOffsetX + eyeRadius, cy - eyeOffsetY), strokeWidth = strokeWidth)
    drawLine(color = Color.Black, start = Offset(cx + eyeOffsetX - eyeRadius, cy - eyeOffsetY), end = Offset(cx + eyeOffsetX + eyeRadius, cy - eyeOffsetY), strokeWidth = strokeWidth)
}

private fun DrawScope.drawHappyEyes(cx: Float, cy: Float, eyeOffsetX: Float, eyeOffsetY: Float, eyeRadius: Float, strokeWidth: Float) {
    drawArc(color = FaceColor, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(cx - eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.35f), size = Size(eyeRadius * 2f, eyeRadius * 1.4f), style = Stroke(strokeWidth))
    drawArc(color = FaceColor, startAngle = 0f, sweepAngle = 180f, useCenter = false, topLeft = Offset(cx + eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.35f), size = Size(eyeRadius * 2f, eyeRadius * 1.4f), style = Stroke(strokeWidth))
}

private fun DrawScope.drawXEyes(cx: Float, cy: Float, eyeOffsetX: Float, eyeOffsetY: Float, eyeRadius: Float, strokeWidth: Float) {
    fun drawX(centerX: Float) {
        drawLine(color = Color.Black, start = Offset(centerX - eyeRadius, cy - eyeOffsetY - eyeRadius), end = Offset(centerX + eyeRadius, cy - eyeOffsetY + eyeRadius), strokeWidth = strokeWidth)
        drawLine(color = Color.Black, start = Offset(centerX - eyeRadius, cy - eyeOffsetY + eyeRadius), end = Offset(centerX + eyeRadius, cy - eyeOffsetY - eyeRadius), strokeWidth = strokeWidth)
    }
    drawX(cx - eyeOffsetX)
    drawX(cx + eyeOffsetX)
}

private fun DrawScope.drawSmile(cx: Float, cy: Float, mouthWidth: Float, mouthHeight: Float, strokeWidth: Float) {
    drawArc(color = FaceColor, startAngle = 15f, sweepAngle = 150f, useCenter = false, topLeft = Offset(cx - mouthWidth / 2f, cy), size = Size(mouthWidth, mouthHeight), style = Stroke(strokeWidth))
}

private fun DrawScope.drawSparkles(cx: Float, cy: Float, bodyRadius: Float) {
    repeat(6) { index ->
        val angle = Math.toRadians(index * 60.0)
        drawCircle(
            color = Gold, radius = bodyRadius * 0.08f,
            center = Offset(cx + kotlin.math.cos(angle).toFloat() * bodyRadius * 1.6f, cy - bodyRadius + kotlin.math.sin(angle).toFloat() * bodyRadius * 0.8f)
        )
    }
}

private fun DrawScope.drawThinkingEyebrows(cx: Float, cy: Float, eyeOffsetX: Float, eyeOffsetY: Float, bodyRadius: Float, eyebrowOffset: Float) {
    drawLine(color = FaceColor, start = Offset(cx - eyeOffsetX * 1.2f, cy - eyeOffsetY * 2.1f + eyebrowOffset), end = Offset(cx - eyeOffsetX * 0.4f, cy - eyeOffsetY * 1.7f - eyebrowOffset), strokeWidth = bodyRadius * 0.05f, cap = StrokeCap.Round)
    drawLine(color = FaceColor, start = Offset(cx + eyeOffsetX * 0.4f, cy - eyeOffsetY * 2.2f - eyebrowOffset), end = Offset(cx + eyeOffsetX * 1.2f, cy - eyeOffsetY * 2.7f + eyebrowOffset), strokeWidth = bodyRadius * 0.05f, cap = StrokeCap.Round)
}

private fun DrawScope.drawHeadphones(cx: Float, cy: Float, bodyRadius: Float, eyeOffsetY: Float) {
    val padY = cy - bodyRadius * 0.25f
    val headphoneGradient = Brush.verticalGradient(colors = listOf(Color(0xFF00E5FF), Color(0xFF00838F)), startY = cy - bodyRadius * 1.15f, endY = padY + bodyRadius * 0.25f)

    drawArc(brush = headphoneGradient, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(cx - bodyRadius * 0.92f, cy - bodyRadius * 1.15f), size = Size(bodyRadius * 1.84f, bodyRadius * 1.9f), style = Stroke(width = bodyRadius * 0.15f))
    drawArc(color = Color.White, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(cx - bodyRadius * 0.88f, cy - bodyRadius * 1.1f), size = Size(bodyRadius * 1.76f, bodyRadius * 1.8f), style = Stroke(width = bodyRadius * 0.05f))
    drawRoundRect(brush = headphoneGradient, topLeft = Offset(cx - bodyRadius * 1.05f, padY - bodyRadius * 0.25f), size = Size(bodyRadius * 0.25f, bodyRadius * 0.5f), cornerRadius = CornerRadius(bodyRadius * 0.08f))
    drawRoundRect(color = Color.White, topLeft = Offset(cx - bodyRadius * 0.98f, padY - bodyRadius * 0.2f), size = Size(bodyRadius * 0.08f, bodyRadius * 0.4f), cornerRadius = CornerRadius(bodyRadius * 0.04f))
    drawRoundRect(brush = headphoneGradient, topLeft = Offset(cx + bodyRadius * 0.8f, padY - bodyRadius * 0.25f), size = Size(bodyRadius * 0.25f, bodyRadius * 0.5f), cornerRadius = CornerRadius(bodyRadius * 0.08f))
    drawRoundRect(color = Color.White, topLeft = Offset(cx + bodyRadius * 0.9f, padY - bodyRadius * 0.2f), size = Size(bodyRadius * 0.08f, bodyRadius * 0.4f), cornerRadius = CornerRadius(bodyRadius * 0.04f))
}

private fun DrawScope.drawAura(cx: Float, cy: Float, bodyRadius: Float, auraScale: Float) {
    drawCircle(color = Gold.copy(alpha = 0.45f), radius = (bodyRadius + 8f) * auraScale, center = Offset(cx, cy), style = Stroke(width = 3f))
    drawCircle(color = Gold.copy(alpha = 0.25f), radius = (bodyRadius + 18f) * auraScale, center = Offset(cx, cy), style = Stroke(width = 2f))
    drawCircle(color = Gold.copy(alpha = 0.65f), radius = bodyRadius * 0.05f, center = Offset(cx, cy - bodyRadius * 1.4f))
}

private fun DrawScope.drawThinkingMouth(cx: Float, cy: Float, bodyRadius: Float) {
    drawLine(color = FaceColor, start = Offset(cx - bodyRadius * 0.12f, cy + bodyRadius * 0.22f), end = Offset(cx + bodyRadius * 0.12f, cy + bodyRadius * 0.22f), strokeWidth = bodyRadius * 0.05f)
}

private fun DrawScope.drawCuteFoot(footCx: Float, footCy: Float, bodyRadius: Float, isLeft: Boolean, wiggle: Float) {
    val angle = if (isLeft) wiggle * 1.2f else -wiggle * 1.2f
    withTransform({ rotate(degrees = angle, pivot = Offset(footCx, footCy)) }) {
        val legLength = bodyRadius * 0.42f
        val toeLength = bodyRadius * 0.18f
        val stroke = bodyRadius * 0.045f

        drawLine(color = Color(0xFFFF9800), start = Offset(footCx, footCy - legLength), end = Offset(footCx, footCy), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = Color(0xFFFF9800), start = Offset(footCx, footCy), end = Offset(footCx - toeLength, footCy + toeLength * 0.45f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = Color(0xFFFF9800), start = Offset(footCx, footCy), end = Offset(footCx, footCy + toeLength * 0.75f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = Color(0xFFFF9800), start = Offset(footCx, footCy), end = Offset(footCx + toeLength, footCy + toeLength * 0.45f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color = Color(0xFFF57C00), radius = stroke * 0.35f, center = Offset(footCx - toeLength, footCy + toeLength * 0.45f))
        drawCircle(color = Color(0xFFF57C00), radius = stroke * 0.35f, center = Offset(footCx, footCy + toeLength * 0.75f))
        drawCircle(color = Color(0xFFF57C00), radius = stroke * 0.35f, center = Offset(footCx + toeLength, footCy + toeLength * 0.45f))
    }
}
private fun DrawScope.drawMascotBody(
    cx: Float,
    cy: Float,
    bodyRadius: Float,
    wingFlap: Float,
    hairSway: Float,
    footWiggle: Float,
    beakOpenAmount: Float = 0f,
    showBeak: Boolean = true,
    isReadingState: Boolean = false,
    drawWingsOnly: Boolean = false
) {
    val leftWingX = if (isReadingState) cx - bodyRadius * 0.58f else cx - bodyRadius * 0.56f
    val rightWingX = if (isReadingState) cx + bodyRadius * 0.58f else cx + bodyRadius * 0.56f
    
    val leftWingY = if (isReadingState) cy + bodyRadius * 0.48f else cy + bodyRadius * 0.38f
    val rightWingY = if (isReadingState) cy + bodyRadius * 0.48f else cy + bodyRadius * 0.38f

    val leftWingGradient = Brush.linearGradient(colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F)), start = Offset(cx - bodyRadius * 1.5f, cy - bodyRadius * 0.4f), end = Offset(cx - bodyRadius * 0.8f, cy + bodyRadius * 0.3f))
    val rightWingGradient = Brush.linearGradient(colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F)), start = Offset(cx + bodyRadius * 0.8f, cy - bodyRadius * 0.4f), end = Offset(cx + bodyRadius * 1.5f, cy + bodyRadius * 0.3f))

    if (!drawWingsOnly) {
        val bodyGradient = Brush.linearGradient(colors = listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFFF6F00)), start = Offset(cx - bodyRadius, cy - bodyRadius), end = Offset(cx + bodyRadius, cy + bodyRadius))
        drawOval(color = Color.Black.copy(alpha = 0.12f), topLeft = Offset(cx - bodyRadius * 0.8f, cy + bodyRadius * 0.95f), size = Size(bodyRadius * 1.6f, bodyRadius * 0.35f))
        
        val bodyPath = Path().apply {
            moveTo(cx, cy - bodyRadius * 1.20f)
            cubicTo(cx + bodyRadius * 0.55f, cy - bodyRadius * 1.18f, cx + bodyRadius * 0.82f, cy - bodyRadius * 0.55f, cx + bodyRadius * 0.78f, cy - bodyRadius * 0.05f)
            cubicTo(cx + bodyRadius * 0.95f, cy + bodyRadius * 0.45f, cx + bodyRadius * 0.90f, cy + bodyRadius * 0.95f, cx + bodyRadius * 0.45f, cy + bodyRadius * 1.18f)
            cubicTo(cx + bodyRadius * 0.20f, cy + bodyRadius * 1.28f, cx - bodyRadius * 0.20f, cy + bodyRadius * 1.28f, cx - bodyRadius * 0.45f, cy + bodyRadius * 1.18f)
            cubicTo(cx - bodyRadius * 0.90f, cy + bodyRadius * 0.95f, cx - bodyRadius * 0.95f, cy + bodyRadius * 0.45f, cx - bodyRadius * 0.78f, cy - bodyRadius * 0.05f)
            cubicTo(cx - bodyRadius * 0.82f, cy - bodyRadius * 0.55f, cx - bodyRadius * 0.55f, cy - bodyRadius * 1.18f, cx, cy - bodyRadius * 1.20f)
            close()
        }

        withTransform({ scale(1.15f, 1.15f, pivot = Offset(cx, cy)) }) {
            drawPath(path = bodyPath, color = Color(0xFFFFD54F).copy(alpha = 0.15f))
        }
        drawPath(path = bodyPath, brush = bodyGradient)
        drawPath(path = bodyPath, color = Color(0xFFE65100).copy(alpha = 0.8f), style = Stroke(width = 2.5f))

        val featherColor = Color(0xFFE65100).copy(alpha = 0.25f)
        val featherStrokeWidth = bodyRadius * 0.04f
        repeat(5) { i ->
            drawArc(color = featherColor, startAngle = 25f, sweepAngle = 130f, useCenter = false, topLeft = Offset(cx - bodyRadius * 0.38f + i * bodyRadius * 0.19f, cy + bodyRadius * 0.18f), size = Size(bodyRadius * 0.22f, bodyRadius * 0.14f), style = Stroke(width = featherStrokeWidth, cap = StrokeCap.Round))
        }

        drawOval(color = Color.White.copy(alpha = 0.45f), topLeft = Offset(cx - bodyRadius * 0.55f, cy - bodyRadius * 0.75f), size = Size(bodyRadius * 0.25f, bodyRadius * 0.60f))
        drawOval(color = Color.White.copy(alpha = 0.12f), topLeft = Offset(cx - bodyRadius * 0.75f, cy - bodyRadius * 0.9f), size = Size(bodyRadius * 1.5f, bodyRadius * 1.8f))

        drawCuteFoot(cx - bodyRadius * 0.34f, cy + bodyRadius * 1.30f, bodyRadius, true, footWiggle)
        drawCuteFoot(cx + bodyRadius * 0.34f, cy + bodyRadius * 1.30f, bodyRadius, false, footWiggle)
        drawCircle(brush = Brush.radialGradient(colors = listOf(Color(0xFFFF8A80).copy(alpha = 0.9f), Color(0xFFFF8A80).copy(alpha = 0f)), center = Offset(cx - bodyRadius * 0.55f, cy + bodyRadius * 0.08f), radius = bodyRadius * 0.18f), radius = bodyRadius * 0.18f, center = Offset(cx - bodyRadius * 0.55f, cy + bodyRadius * 0.08f))
        drawCircle(brush = Brush.radialGradient(colors = listOf(Color(0xFFFF8A80).copy(alpha = 0.9f), Color(0xFFFF8A80).copy(alpha = 0f)), center = Offset(cx + bodyRadius * 0.55f, cy + bodyRadius * 0.08f), radius = bodyRadius * 0.18f), radius = bodyRadius * 0.18f, center = Offset(cx + bodyRadius * 0.55f, cy + bodyRadius * 0.08f))

        withTransform({ rotate(degrees = hairSway * 0.25f, pivot = Offset(cx, cy - bodyRadius * 1.05f)) }) {
            val tuft = Path().apply {
                moveTo(cx, cy - bodyRadius * 1.05f)
                quadraticTo(cx - bodyRadius * 0.18f, cy - bodyRadius * 1.42f, cx - bodyRadius * 0.04f, cy - bodyRadius * 1.30f)
                quadraticTo(cx, cy - bodyRadius * 1.52f, cx + bodyRadius * 0.04f, cy - bodyRadius * 1.30f)
                quadraticTo(cx + bodyRadius * 0.18f, cy - bodyRadius * 1.42f, cx, cy - bodyRadius * 1.05f)
                close()
            }
            drawPath(path = tuft, color = Color(0xFFE65100))
            drawPath(path = tuft, color = Color(0xFFBF360C), style = Stroke(width = bodyRadius * 0.02f))
        }

        if (showBeak) {
            val beakGap = bodyRadius * 0.10f * beakOpenAmount
            val upperBeak = Path().apply {
                moveTo(cx - bodyRadius * 0.15f, cy + bodyRadius * 0.08f)
                quadraticTo(cx, cy, cx + bodyRadius * 0.15f, cy + bodyRadius * 0.08f)
                quadraticTo(cx, cy + bodyRadius * 0.18f, cx - bodyRadius * 0.15f, cy + bodyRadius * 0.08f)
                close()
            }
            val lowerBeak = Path().apply {
                moveTo(cx - bodyRadius * 0.12f, cy + bodyRadius * 0.16f + beakGap)
                quadraticTo(cx, cy + bodyRadius * 0.34f + beakGap, cx + bodyRadius * 0.12f, cy + bodyRadius * 0.16f + beakGap)
                quadraticTo(cx, cy + bodyRadius * 0.24f + beakGap, cx - bodyRadius * 0.12f, cy + bodyRadius * 0.16f + beakGap)
                close()
            }

            if (beakOpenAmount > 0f) {
                drawOval(color = Color(0xFF5D4037), topLeft = Offset(cx - bodyRadius * 0.06f, cy + bodyRadius * 0.14f), size = Size(bodyRadius * 0.12f, maxOf(bodyRadius * 0.03f, beakGap * 1.5f)))
            }
            drawPath(path = upperBeak, brush = Brush.verticalGradient(colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000))))
            drawPath(path = lowerBeak, brush = Brush.verticalGradient(colors = listOf(Color(0xFFFFB74D), Color(0xFFF57C00))))
            drawOval(color = Color.White.copy(alpha = 0.35f), topLeft = Offset(cx - bodyRadius * 0.07f, cy + bodyRadius * 0.05f), size = Size(bodyRadius * 0.08f, bodyRadius * 0.04f))
            drawPath(path = upperBeak, color = Color(0xFFE65100), style = Stroke(width = 2f))
            drawPath(path = lowerBeak, color = Color(0xFFE65100), style = Stroke(width = 2f))
        }
    }

    // FIX: Only draw background left wing if the mascot is NOT reading
    if (!isReadingState && !drawWingsOnly) {
        withTransform({
            rotate(
                degrees = -6f + wingFlap,
                pivot = Offset(leftWingX, leftWingY)
            )
        }) {
            val path = createWingPath(leftWingX, leftWingY, bodyRadius, true, false)
            drawPath(path = path, brush = leftWingGradient)
            repeat(3) { i ->
                drawLine(
                    color = Color(0xFFF5B041).copy(alpha = 0.45f),
                    start = Offset(leftWingX - bodyRadius * 0.12f, leftWingY + bodyRadius * 0.08f),
                    end = Offset(leftWingX - bodyRadius * (0.35f + i * 0.10f), leftWingY + bodyRadius * (0.18f + i * 0.05f)),
                    strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                )
            }
            drawPath(path = path, color = Color(0xFFF5B041), style = Stroke(2f))
        }
    }

        // Render left wing layout
    if (!isReadingState || drawWingsOnly) {
        withTransform({
            rotate(
                degrees = if (isReadingState) 15f else -6f + wingFlap,
                pivot = Offset(leftWingX, leftWingY)
            )
        }) {
            val path = createWingPath(leftWingX, leftWingY, bodyRadius, true, isReadingState)
            drawPath(path = path, brush = leftWingGradient)
            
            if (isReadingState) {
                // Vertical feather accents to match the downward sweeping reading hands
                repeat(2) { i ->
                    drawLine(
                        color = Color(0xFFF5B041).copy(alpha = 0.45f),
                        start = Offset(leftWingX - bodyRadius * 0.05f + (i * 10f), leftWingY + bodyRadius * 0.15f),
                        end = Offset(leftWingX - bodyRadius * 0.12f + (i * 10f), leftWingY + bodyRadius * 0.35f),
                        strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                    )
                }
            } else {
                // Normal horizontal wing lines for non-reading modes
                repeat(3) { i ->
                    drawLine(
                        color = Color(0xFFF5B041).copy(alpha = 0.45f),
                        start = Offset(leftWingX - bodyRadius * 0.12f, leftWingY + bodyRadius * 0.08f),
                        end = Offset(leftWingX - bodyRadius * (0.35f + i * 0.10f), leftWingY + bodyRadius * (0.18f + i * 0.05f)),
                        strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                    )
                }
            }
            drawPath(path = path, color = Color(0xFFF5B041), style = Stroke(2f))
        }
    }

    // Render right wing layout
    if (!isReadingState || drawWingsOnly) {
        withTransform({
            rotate(
                degrees = if (isReadingState) -15f else 6f - wingFlap,
                pivot = Offset(rightWingX, rightWingY)
            )
        }) {
            val path = createWingPath(rightWingX, rightWingY, bodyRadius, false, isReadingState)
            drawPath(path = path, brush = rightWingGradient)
            
            if (isReadingState) {
                // Vertical feather accents to match the downward sweeping reading hands
                repeat(2) { i ->
                    drawLine(
                        color = Color(0xFFF5B041).copy(alpha = 0.45f),
                        start = Offset(rightWingX + bodyRadius * 0.05f - (i * 10f), rightWingY + bodyRadius * 0.15f),
                        end = Offset(rightWingX + bodyRadius * 0.12f - (i * 10f), rightWingY + bodyRadius * 0.35f),
                        strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                    )
                }
            } else {
                // Normal horizontal wing lines for non-reading modes
                repeat(3) { i ->
                    drawLine(
                        color = Color(0xFFF5B041).copy(alpha = 0.45f),
                        start = Offset(rightWingX + bodyRadius * 0.12f, rightWingY + bodyRadius * 0.08f),
                        end = Offset(rightWingX + bodyRadius * (0.35f + i * 0.10f), rightWingY + bodyRadius * (0.18f + i * 0.05f)),
                        strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                    )
                }
            }
            drawPath(path = path, color = Color(0xFFF5B041), style = Stroke(2f))
        }
    }
}

private fun DrawScope.drawZ(x: Float, y: Float, size: Float, alpha: Float, strokeW: Float) {
    drawLine(color = Color(0xFF90CAF9).copy(alpha = alpha), start = Offset(x - size / 2f, y - size / 2f), end = Offset(x + size / 2f, y - size / 2f), strokeWidth = strokeW, cap = StrokeCap.Round)
    drawLine(color = Color(0xFF90CAF9).copy(alpha = alpha), start = Offset(x + size / 2f, y - size / 2f), end = Offset(x - size / 2f, y + size / 2f), strokeWidth = strokeW, cap = StrokeCap.Round)
    drawLine(color = Color(0xFF90CAF9).copy(alpha = alpha), start = Offset(x - size / 2f, y + size / 2f), end = Offset(x + size / 2f, y + size / 2f), strokeWidth = strokeW, cap = StrokeCap.Round)
}

private fun DrawScope.drawSleepingEyes(cx: Float, cy: Float, eyeOffsetX: Float, eyeOffsetY: Float, eyeRadius: Float, strokeWidth: Float) {
    fun drawEye(centerX: Float) {
        drawArc(color = Color.Black, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(centerX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.4f), size = Size(eyeRadius * 2f, eyeRadius * 1.4f), style = Stroke(width = strokeWidth))
    }
    drawEye(cx - eyeOffsetX)
    drawEye(cx + eyeOffsetX)
}
 private fun createWingPath(
    wingX: Float,
    wingY: Float,
    bodyRadius: Float,
    isLeft: Boolean,
    isReadingState: Boolean = false
): Path {
    val dir = if (isLeft) -1f else 1f
    return Path().apply {
        moveTo(wingX, wingY)
        
        if (isReadingState) {
            // Extended control paths inward (`dir * bodyRadius * 0.48f`) to grasp far across onto the book face
            cubicTo(
                wingX + dir * bodyRadius * 0.48f, wingY + bodyRadius * 0.08f,
                wingX + dir * bodyRadius * 0.45f, wingY + bodyRadius * 0.38f,
                wingX - dir * bodyRadius * 0.18f, wingY + bodyRadius * 0.40f
            )
            quadraticTo(
                wingX - dir * bodyRadius * 0.38f, wingY + bodyRadius * 0.32f,
                wingX - dir * bodyRadius * 0.24f, wingY + bodyRadius * 0.10f
            )
            quadraticTo(
                wingX - dir * bodyRadius * 0.08f, wingY + bodyRadius * 0.02f,
                wingX, wingY
            )
        } else {
            cubicTo(
                wingX + dir * bodyRadius * 0.25f, wingY - bodyRadius * 0.18f,
                wingX + dir * bodyRadius * 0.55f, wingY - bodyRadius * 0.10f,
                wingX + dir * bodyRadius * 0.70f, wingY + bodyRadius * 0.10f
            )
            quadraticTo(
                wingX + dir * bodyRadius * 0.78f, wingY + bodyRadius * 0.22f,
                wingX + dir * bodyRadius * 0.60f, wingY + bodyRadius * 0.34f
            )
            quadraticTo(
                wingX + dir * bodyRadius * 0.25f, wingY + bodyRadius * 0.42f,
                wingX, wingY
            )
        }
        close()
    }
}

private fun DrawScope.drawBook(
    cx: Float,
    cy: Float,
    bodyRadius: Float,
    bookOpen: Float
) {
    val width = bodyRadius * 1.15f 
    val height = bodyRadius * 0.46f 
    val top = cy + bodyRadius * 0.32f 

    val perspective = bodyRadius * 0.16f 
    val bend = bodyRadius * 0.11f 
    val spineWidth = bodyRadius * 0.06f

    val topLeftX = cx - width / 2f
    val topRightX = cx + width / 2f
    
    val bottomLeftX = topLeftX + perspective
    val bottomRightX = topRightX - perspective

    val coverExtensionX = bodyRadius * 0.025f
    val coverExtensionY = bodyRadius * 0.018f

    val cover = Path().apply {
        moveTo(topLeftX - coverExtensionX, top - coverExtensionY)
        quadraticTo(cx, top + bend, topRightX + coverExtensionX, top - coverExtensionY) 
        lineTo(bottomRightX + coverExtensionX, top + height + coverExtensionY)
        quadraticTo(cx, top + height + bend, bottomLeftX - coverExtensionX, top + height + coverExtensionY)
        close()
    }
    drawPath(
        path = cover,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF721C24), Color(0xFF4A0E17), Color(0xFF2D050B))
        )
    )

    val pageEdgeThickness = bodyRadius * 0.015f
    val pageBlock = Path().apply {
        moveTo(topLeftX, top + pageEdgeThickness)
        quadraticTo(cx, top + bend + pageEdgeThickness, topRightX, top + pageEdgeThickness)
        lineTo(bottomRightX, top + height + pageEdgeThickness)
        quadraticTo(cx, top + height + bend + pageEdgeThickness, bottomLeftX, top + height + pageEdgeThickness)
        close()
    }
    drawPath(path = pageBlock, color = Color(0xFF550A11))

    val pageInsetX = bodyRadius * 0.01f
    val pageInsetY = bodyRadius * 0.005f
    
    val pages = Path().apply {
        moveTo(topLeftX + pageInsetX, top + pageInsetY)
        quadraticTo(cx, top + bend + pageInsetY, topRightX - pageInsetX, top + pageInsetY)
        lineTo(bottomRightX - pageInsetX, top + height - pageInsetY)
        quadraticTo(cx, top + height + bend - pageInsetY, bottomLeftX + pageInsetX, top + height - pageInsetY)
        close()
    }
    drawPath(
        path = pages,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFA62B34), Color(0xFF631118), Color(0xFF3D050A))
        )
    )

    val spinePath = Path().apply {
        moveTo(cx - spineWidth / 2f, top + bend)
        lineTo(cx + spineWidth / 2f, top + bend)
        lineTo(cx + (spineWidth * 0.7f) / 2f, top + height + bend)
        lineTo(cx - (spineWidth * 0.7f) / 2f, top + height + bend)
        close()
    }
    drawPath(path = spinePath, color = Color(0xFF1F0307))

    drawPath(path = cover, color = Color(0xFFE5C158), style = Stroke(width = 1.8f))

    drawOval(
        color = Color(0xFFE5C158),
        topLeft = Offset(cx - (bodyRadius * 0.05f), top + (height * 0.42f) + (bend * 0.5f)),
        size = Size(bodyRadius * 0.10f, bodyRadius * 0.045f)
    )

    drawPath(
        path = Path().apply {
            moveTo(cx, top + bend)
            quadraticTo(cx - (width * 0.25f), top + (bend * 0.9f), topLeftX + pageInsetX, top + pageInsetY)
            lineTo(bottomLeftX + pageInsetX, top + height - pageInsetY)
            quadraticTo(cx - (width * 0.25f), top + height + (bend * 1.1f), cx, top + height + bend)
            close()
        },
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Black.copy(alpha = 0.0f), Color.Black.copy(alpha = 0.18f)),
            startX = topLeftX,
            endX = cx
        )
    )
}