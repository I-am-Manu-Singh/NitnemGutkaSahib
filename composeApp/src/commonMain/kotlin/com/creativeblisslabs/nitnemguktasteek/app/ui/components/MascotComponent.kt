package com.creativeblisslabs.nitnemguktasteek.app.ui.components

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
    Idle, Happy, Thinking, Meditating, Reading, Listening, Sleeping, Oops, Celebrating,
    Reflecting, AmritVela, Simran, Namaste, Walking, Flying, FlyingPanning
}

@Composable
fun MascotBani(
    state: MascotState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascotTransition")
    
    val mouthOpen by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(220),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mouth"
    )
    
    val readingBob by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "readingBob"
    )
    
    val thinkingTilt by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thinkingTilt"
    )
val floatAnim by infiniteTransition.animateFloat(
    initialValue = -3f, // changed from -15f
    targetValue = 3f,   // changed from 15f
    animationSpec = infiniteRepeatable(
        animation = tween(2200, easing = EaseInOutSine),
        repeatMode = RepeatMode.Reverse
    ),
    label = "float"
)
    val driftX by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "driftX"
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
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )
    
    val hairSway by infiniteTransition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hair"
    )

    val footWiggle by infiniteTransition.animateFloat(
        initialValue = -13f,
        targetValue = 13f,
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
        initialValue = -8f,
        targetValue = 8f,
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
        initialValue = -25f,
        targetValue = 25f,
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

    val walkCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "walkCycle"
    )
    Canvas(
        modifier = modifier.graphicsLayer {
            translationY = when (state) {
                MascotState.Reading -> floatAnim + readingBob
                MascotState.Walking -> -(sin(walkCycle * 2 * Math.PI).toFloat().coerceIn(-1f, 1f) * 4f).coerceAtLeast(-4f) // Gentle forward/backward body bob up-down
                MascotState.Flying -> 0f  // Parent Box handles vertical movement
                else -> floatAnim
            }

            when (state) {
                MascotState.Sleeping -> {
                    scaleX = breathingScale
                    scaleY = breathingScale
                }
                MascotState.Celebrating -> {
                    scaleX = 1f + (breathingScale - 1f) * 1.5f
                    scaleY = 1f + (breathingScale - 1f) * 1.5f
                }
                MascotState.Thinking, MascotState.Oops -> {
                    rotationZ = thinkingTilt // Applies that gentle caring head tilt
                }
                else -> {
                    scaleX = 1f + (breathingScale - 1f) * 0.4f
                    scaleY = 1f + (breathingScale - 1f) * 0.4f
                }
            }
        }
    ){
        val currentMouthOpen = mouthOpen
        val currentWingFlap = wingFlap
        val currentHairSway = hairSway
        val currentFootWiggle = footWiggle
        val currentBlink = blinkProgress
        val currentPupil = pupilOffset
        val currentReadingPupil = readingPupil
        val currentBookOpen = bookOpen
        val currentAuraScale = auraScale
        val currentZzz = zzzAnim
        val currentEyebrow = eyebrowOffset
        val currentWalkCycle = walkCycle

        val cx = size.width / 2f
        val cy = size.height / 2f

        val bodyRadius = size.minDimension * 0.35f
        val eyeRadius = bodyRadius * 0.12f
        val eyeOffsetX = bodyRadius * 0.42f
        val eyeOffsetY = bodyRadius * 0.28f

        val wingOffset = when (state) {
            MascotState.Reading -> -25f
            MascotState.Happy, MascotState.Celebrating -> currentWingFlap // Active flapping for celebration & happiness
            MascotState.Flying -> currentWingFlap * 1.4f                  // Vigorous flapping while airborne
            MascotState.FlyingPanning -> currentWingFlap * 2.4f           // Rapid energetic flapping during active 3D navigation
            MascotState.Listening -> currentWingFlap * 0.4f
            MascotState.Reflecting -> -15f // Tucked wings for deep thought
            MascotState.Walking -> currentWingFlap * 0.6f
            else -> currentWingFlap * 0.35f // Subtle idle flapping to prevent "still" look
        }
        
        if (state == MascotState.FlyingPanning) {
            // ── EXACT REPLICA AUTHENTIC 3D FLIGHT POSE (MATCHING FRONT PROFILE 100%) ──
            val sideWingFlap = currentWingFlap * 1.8f

            // 1. Back Wing (Behind Body)
            val leftWingX = cx - bodyRadius * 0.50f
            val leftWingY = cy + bodyRadius * 0.22f
            withTransform({
                rotate(degrees = -12f + sideWingFlap, pivot = Offset(leftWingX, leftWingY))
            }) {
                val backWingPath = createWingPath(leftWingX, leftWingY, bodyRadius * 1.15f, true, false)
                drawPath(path = backWingPath, brush = Brush.linearGradient(listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFFA000))))
                drawPath(path = backWingPath, color = Color(0xFFF5B041), style = Stroke(2f))
            }

            // 2. Trailing Cute Feet at exact base — elegant natural flight dangle
            val footSwing = sin(currentWingFlap * 0.12f) * 12f
            drawCuteFoot(cx - bodyRadius * 0.22f, cy + bodyRadius * 1.35f, bodyRadius * 0.95f, true, -24f + footSwing)
            drawCuteFoot(cx + bodyRadius * 0.08f, cy + bodyRadius * 1.35f, bodyRadius * 0.95f, false, -16f + footSwing)

            // 3. Exact 1:1 Body Path (Identical to Front Body Geometry)
            val bodyGradient = Brush.linearGradient(
                colors = listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFFF6F00)),
                start = Offset(cx - bodyRadius, cy - bodyRadius),
                end = Offset(cx + bodyRadius, cy + bodyRadius)
            )
            val bodyPath = Path().apply {
                moveTo(cx, cy - bodyRadius * 1.18f)
                // Forward/chest side — fuller, more rounded puff
                cubicTo(cx + bodyRadius * 0.60f, cy - bodyRadius * 1.16f, cx + bodyRadius * 0.90f, cy - bodyRadius * 0.50f, cx + bodyRadius * 0.82f, cy + bodyRadius * 0.05f)
                // Bottom forward curve
                cubicTo(cx + bodyRadius * 0.98f, cy + bodyRadius * 0.50f, cx + bodyRadius * 0.88f, cy + bodyRadius * 0.98f, cx + bodyRadius * 0.40f, cy + bodyRadius * 1.18f)
                // Tail base — slightly narrower than front
                cubicTo(cx + bodyRadius * 0.15f, cy + bodyRadius * 1.26f, cx - bodyRadius * 0.25f, cy + bodyRadius * 1.20f, cx - bodyRadius * 0.50f, cy + bodyRadius * 1.08f)
                // Back/tail side — tapered and sleeker
                cubicTo(cx - bodyRadius * 0.88f, cy + bodyRadius * 0.80f, cx - bodyRadius * 0.85f, cy + bodyRadius * 0.28f, cx - bodyRadius * 0.70f, cy - bodyRadius * 0.12f)
                cubicTo(cx - bodyRadius * 0.76f, cy - bodyRadius * 0.60f, cx - bodyRadius * 0.48f, cy - bodyRadius * 1.16f, cx, cy - bodyRadius * 1.18f)
                close()
            }

            withTransform({ scale(1.15f, 1.15f, pivot = Offset(cx, cy)) }) {
                drawPath(path = bodyPath, color = Color(0xFFFFD54F).copy(alpha = 0.15f))
            }
            drawPath(path = bodyPath, brush = bodyGradient)
            drawPath(path = bodyPath, color = Color(0xFFE65100).copy(alpha = 0.8f), style = Stroke(width = 2.5f))

            // Signature chest feathers — shifted to the forward/right side of profile
            val featherColor = Color(0xFFE65100).copy(alpha = 0.25f)
            val featherStrokeWidth = bodyRadius * 0.04f
            repeat(4) { i ->
                drawArc(color = featherColor, startAngle = 25f, sweepAngle = 130f, useCenter = false, topLeft = Offset(cx + bodyRadius * 0.05f + i * bodyRadius * 0.18f, cy + bodyRadius * 0.10f), size = Size(bodyRadius * 0.20f, bodyRadius * 0.13f), style = Stroke(width = featherStrokeWidth, cap = StrokeCap.Round))
            }

            // White Highlights — on the forward-facing right side of profile
            drawOval(color = Color.White.copy(alpha = 0.45f), topLeft = Offset(cx + bodyRadius * 0.18f, cy - bodyRadius * 0.75f), size = Size(bodyRadius * 0.22f, bodyRadius * 0.55f))
            drawOval(color = Color.White.copy(alpha = 0.10f), topLeft = Offset(cx - bodyRadius * 0.20f, cy - bodyRadius * 0.9f), size = Size(bodyRadius * 1.2f, bodyRadius * 1.6f))

            // Pre-compute eye center for shared use by cheek and beak — higher and closer to body outline
            val eyeCenter = Offset(cx + bodyRadius * 0.52f, cy - bodyRadius * 0.38f)

            // Blushing Cheek — single cheek on the forward-facing side, just below and behind the eye
            val cheekCenter = Offset(cx + bodyRadius * 0.38f, eyeCenter.y + eyeRadius * 1.6f)
            drawCircle(brush = Brush.radialGradient(colors = listOf(Color(0xFFFF8A80).copy(alpha = 0.85f), Color(0xFFFF8A80).copy(alpha = 0f)), center = cheekCenter, radius = bodyRadius * 0.18f), radius = bodyRadius * 0.18f, center = cheekCenter)

            // 4. Hair Tuft — shifted to the crown of the side-profile head (top-right)
            val tuftBaseX = cx + bodyRadius * 0.10f
            val tuftBaseY = cy - bodyRadius * 1.15f
            withTransform({
                rotate(degrees = -8f + currentHairSway * 0.30f, pivot = Offset(tuftBaseX, tuftBaseY))
            }) {
                val tuft = Path().apply {
                    moveTo(tuftBaseX, tuftBaseY)
                    quadraticTo(tuftBaseX - bodyRadius * 0.15f, tuftBaseY - bodyRadius * 0.40f, tuftBaseX - bodyRadius * 0.04f, tuftBaseY - bodyRadius * 0.28f)
                    quadraticTo(tuftBaseX + bodyRadius * 0.04f, tuftBaseY - bodyRadius * 0.50f, tuftBaseX + bodyRadius * 0.14f, tuftBaseY - bodyRadius * 0.28f)
                    quadraticTo(tuftBaseX + bodyRadius * 0.22f, tuftBaseY - bodyRadius * 0.38f, tuftBaseX, tuftBaseY)
                    close()
                }
                drawPath(path = tuft, color = Color(0xFFE65100))
                drawPath(path = tuft, color = Color(0xFFBF360C), style = Stroke(width = bodyRadius * 0.02f))
            }

            // 5. Single eye on the forward-facing side of the profile — perfectly level
            val eyeHeight = eyeRadius * 2f * currentBlink
            drawOval(color = Color.White, topLeft = Offset(eyeCenter.x - eyeRadius, eyeCenter.y - eyeHeight / 2f), size = Size(eyeRadius * 2f, eyeHeight.coerceAtLeast(2f)))
            if (currentBlink > 0.25f) {
                drawCircle(color = FaceColor, radius = eyeRadius * 0.50f, center = Offset(eyeCenter.x + eyeRadius * 0.20f, eyeCenter.y))
                drawCircle(color = Color.White, radius = eyeRadius * 0.20f, center = Offset(eyeCenter.x + eyeRadius * 0.30f, eyeCenter.y - eyeRadius * 0.15f))
            } else {
                drawLine(color = FaceColor, start = Offset(eyeCenter.x - eyeRadius, eyeCenter.y), end = Offset(eyeCenter.x + eyeRadius, eyeCenter.y), strokeWidth = eyeRadius * 0.18f, cap = StrokeCap.Round)
            }

            // 6. Side beak — base at body edge, tip clearly outside, positioned just a little lower
            val beakBaseX = cx + bodyRadius * 0.68f  // at right body wall
            val beakTipX  = cx + bodyRadius * 1.08f  // protruding well beyond body
            val beakMidY  = eyeCenter.y + eyeRadius * 1.85f  // positioned just a little lower below the eye
            val beakGap   = bodyRadius * 0.055f
            val upperBeak = Path().apply {
                moveTo(beakBaseX, beakMidY - bodyRadius * 0.08f)
                lineTo(beakTipX,  beakMidY)
                lineTo(beakBaseX, beakMidY + bodyRadius * 0.08f)
                close()
            }
            val lowerBeak = Path().apply {
                moveTo(beakBaseX, beakMidY + bodyRadius * 0.08f + beakGap)
                lineTo(beakTipX - bodyRadius * 0.14f, beakMidY + bodyRadius * 0.13f + beakGap)
                lineTo(beakBaseX, beakMidY + bodyRadius * 0.17f + beakGap)
                close()
            }
            drawPath(path = upperBeak, brush = Brush.verticalGradient(colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000)), startY = beakMidY - bodyRadius * 0.08f, endY = beakMidY + bodyRadius * 0.08f))
            drawPath(path = lowerBeak, brush = Brush.verticalGradient(colors = listOf(Color(0xFFFFB74D), Color(0xFFF57C00)), startY = beakMidY + bodyRadius * 0.08f, endY = beakMidY + bodyRadius * 0.20f))
            drawPath(path = upperBeak, color = Color(0xFFE65100), style = Stroke(width = 2.2f))
            drawPath(path = lowerBeak, color = Color(0xFFE65100), style = Stroke(width = 2.2f))

            // 7. Fore Wing (In Front of Body flapping with energy)
            val rightWingX = cx + bodyRadius * 0.42f
            val rightWingY = cy + bodyRadius * 0.22f
            withTransform({
                rotate(degrees = 12f - sideWingFlap, pivot = Offset(rightWingX, rightWingY))
            }) {
                val foreWingPath = createWingPath(rightWingX, rightWingY, bodyRadius * 1.25f, false, false)
                drawPath(path = foreWingPath, brush = Brush.linearGradient(listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFFB300))))
                repeat(3) { i ->
                    drawLine(
                        color = Color(0xFFF5B041).copy(alpha = 0.45f),
                        start = Offset(rightWingX + bodyRadius * 0.10f, rightWingY + bodyRadius * 0.08f),
                        end = Offset(rightWingX + bodyRadius * (0.30f + i * 0.10f), rightWingY + bodyRadius * (0.15f + i * 0.05f)),
                        strokeWidth = bodyRadius * 0.015f,
                        cap = StrokeCap.Round
                    )
                }
                drawPath(path = foreWingPath, color = Color(0xFFF5B041), style = Stroke(2.2f))
            }
        } else {
            drawMascotBody(
                cx = cx,
                cy = cy, 
                bodyRadius = bodyRadius,
                wingFlap = wingOffset,
                hairSway = currentHairSway,
                footWiggle = if (state == MascotState.Walking) 0f else currentFootWiggle,
                walkCycle = if (state == MascotState.Walking) currentWalkCycle else null,
                beakOpenAmount = when (state) {
                    MascotState.Listening, MascotState.Thinking -> 0.4f
                    MascotState.Happy -> 0.25f
                    MascotState.Celebrating -> 0.45f
                    MascotState.Reading -> 0.15f
                    MascotState.Idle -> currentMouthOpen * 1.1f
                    MascotState.Walking -> 0.1f
                    else -> 0f
                },
                showBeak = state != MascotState.Sleeping,
                isReadingState = state == MascotState.Reading,
                drawWingsOnly = false,
                isReflectingState = state == MascotState.Reflecting,
                hasHeadCovering = state == MascotState.AmritVela || state == MascotState.Namaste
            )
        }

        when (state) {
            MascotState.FlyingPanning -> {
                // Handled with full authentic body parts in side profile branch above
            }
            MascotState.Idle, MascotState.Walking, MascotState.Flying -> {
                drawOpenEyes(cx, cy, eyeOffsetX, if (state == MascotState.Walking || state == MascotState.Flying) 0.6f else currentPupil, eyeOffsetY, eyeRadius, currentBlink)
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

                val z1Progress = (currentZzz + 0.0f) % 1f
                val z2Progress = (currentZzz + 0.33f) % 1f
                val z3Progress = (currentZzz + 0.66f) % 1f
                val startX = cx + bodyRadius * 0.55f
                val startY = cy - bodyRadius * 0.45f
                val travel = bodyRadius * 0.8f
                val sway = bodyRadius * 0.08f

                drawZ(startX + sin(z1Progress * PI.toFloat() * 2f) * sway, startY - z1Progress * travel, bodyRadius * 0.30f, (1f - z1Progress) * 0.9f, bodyRadius * 0.06f)
                drawZ(startX + sin(z2Progress * PI.toFloat() * 2f) * sway, startY - z2Progress * travel, bodyRadius * 0.38f, (1f - z2Progress) * 0.9f, bodyRadius * 0.08f)
                drawZ(startX + sin(z3Progress * PI.toFloat() * 2f) * sway, startY - z3Progress * travel, bodyRadius * 0.46f, (1f - z3Progress) * 0.9f, bodyRadius * 0.10f)
            }
            MascotState.Reading -> {
                drawOpenEyes(cx, cy, eyeOffsetX, currentReadingPupil, eyeOffsetY, eyeRadius, currentBlink, 1f)
                drawThinkingEyebrows(cx, cy, eyeOffsetX, eyeOffsetY, bodyRadius, currentEyebrow * 0.5f)
                drawBook(cx, cy, bodyRadius, currentBookOpen)
                
                drawMascotBody(
                    cx = cx, cy = cy, bodyRadius = bodyRadius, wingFlap = wingOffset,
                    hairSway = currentHairSway, footWiggle = currentFootWiggle, beakOpenAmount = 0.15f,
                    showBeak = true, isReadingState = true, drawWingsOnly = true
                )
            }
            MascotState.Listening -> {
                drawHeadphones(cx, cy, bodyRadius, eyeOffsetY)
                drawClosedEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.10f)
            }
            MascotState.Thinking -> {
                drawOpenEyes(cx, cy, eyeOffsetX, currentPupil, eyeOffsetY, eyeRadius, currentBlink)
                drawThinkingEyebrows(cx, cy, eyeOffsetX, eyeOffsetY, bodyRadius, currentEyebrow)
                drawThinkingMouth(cx, cy, bodyRadius)
            }
            MascotState.Meditating -> {
                drawClosedEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.08f)
                drawAura(cx, cy, bodyRadius, currentAuraScale)
                drawArc(
                    color = FaceColor, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(cx - bodyRadius * 0.08f, cy + bodyRadius * 0.28f),
                    size = Size(bodyRadius * 0.16f, bodyRadius * 0.06f),
                    style = Stroke(width = bodyRadius * 0.025f, cap = StrokeCap.Round)
                )
            }
            MascotState.Oops -> {
                // 1. Soft, caring, understanding curved eyes (sympathetic half-closed arches)
                drawCaringEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.08f)

                // 2. Gentle comforting eyebrow tilt showing empathy
                drawThinkingEyebrows(cx, cy, eyeOffsetX, eyeOffsetY, bodyRadius, currentEyebrow * 0.4f)

                // 3. Warm, reassuring, slight comforting smile
                drawArc(
                    color = FaceColor,
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - bodyRadius * 0.08f, cy + bodyRadius * 0.26f),
                    size = Size(bodyRadius * 0.16f, bodyRadius * 0.06f),
                    style = Stroke(width = bodyRadius * 0.025f, cap = StrokeCap.Round)
                )
            }
          MascotState.Celebrating -> {
                drawHappyEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.10f)
                
                // 1. Natural, subtle 8-point fireworks positioned gracefully ABOVE the mascot's head
                val fireworkProgress = (currentAuraScale - 0.95f) / 0.13f 
                val fireworkCenters = listOf(
                    Offset(cx - bodyRadius * 0.4f, cy - bodyRadius * 1.4f),
                    Offset(cx + bodyRadius * 0.4f, cy - bodyRadius * 1.5f),
                    Offset(cx, cy - bodyRadius * 1.7f)
                )

                fireworkCenters.forEachIndexed { fIndex, center ->
                    val burstRadius = bodyRadius * (0.12f + fireworkProgress * 0.28f)
                    // Softer visibility (multiplied by 0.45f for a more natural, subtle glow)
                    val alpha = ((1f - fireworkProgress) * 0.45f).coerceIn(0f, 1f)
                    
                    repeat(8) { i ->
                        val angle = i * (2.0 * PI / 8.0) + (fIndex * 0.4)
                        val cos = kotlin.math.cos(angle).toFloat()
                        val sin = kotlin.math.sin(angle).toFloat()
                        
                        val startDist = burstRadius * 0.2f
                        val endDist = burstRadius
                        
                        drawLine(
                            color = when ((i + fIndex) % 5) {
                                0 -> Gold
                                1 -> Color(0xFFFF3366)
                                2 -> Color(0xFF00E5FF)
                                3 -> Color(0xFF76FF03)
                                else -> Color(0xFFFF9100)
                            }.copy(alpha = alpha),
                            start = Offset(center.x + startDist * cos, center.y + startDist * sin),
                            end = Offset(center.x + endDist * cos, center.y + endDist * sin),
                            strokeWidth = bodyRadius * 0.025f,
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(
                        color = Color.White.copy(alpha = alpha * 0.6f),
                        radius = bodyRadius * 0.04f * (1f - fireworkProgress * 0.5f),
                        center = center
                    )
                }

                // 2. Continuous Falling Confetti (Driven by currentZzz / Restarting loop so it never retracts upwards)
                val confettiColors = listOf(
                    Color(0xFFFF1744).copy(alpha = 0.8f), 
                    Color(0xFF00E5FF).copy(alpha = 0.8f), 
                    Color(0xFFFFEA00).copy(alpha = 0.8f), 
                    Color(0xFF76FF03).copy(alpha = 0.8f), 
                    Color(0xFFD500F9).copy(alpha = 0.8f), 
                    Color(0xFFFF9100).copy(alpha = 0.8f)
                )
                
                // Using currentZzz (Restart animation spec 0->1) to ensure continuous one-way downward flow
                repeat(12) { i ->
                    val progress = ((currentZzz + (i * 0.08f)) % 1.0f)
                    val xSpread = (i * 38f) % (size.width * 0.7f) - (size.width * 0.35f)
                    val yPos = (progress * size.height * 0.85f) - (size.height * 0.1f)
                    val rotationAngle = progress * 720f * (if (i % 2 == 0) 1f else -1.2f)
                    
                    val confettiColor = confettiColors[i % confettiColors.size]
                    
                    withTransform({
                        rotate(rotationAngle, pivot = Offset(cx + xSpread, cy + yPos))
                    }) {
                        if (i % 3 == 0) {
                            drawRoundRect(
                                color = confettiColor,
                                topLeft = Offset(cx + xSpread, cy + yPos),
                                size = Size(bodyRadius * 0.05f, bodyRadius * 0.12f),
                                cornerRadius = CornerRadius(bodyRadius * 0.02f)
                            )
                        } else {
                            drawRect(
                                color = confettiColor,
                                topLeft = Offset(cx + xSpread, cy + yPos),
                                size = Size(bodyRadius * 0.07f, bodyRadius * 0.07f)
                            )
                        }
                    }
                }
                
                // Joyful open-mouth expression
                drawArc(
                    color = FaceColor, 
                    startAngle = 10f, 
                    sweepAngle = 160f, 
                    useCenter = true, 
                    topLeft = Offset(cx - bodyRadius * 0.14f, cy + bodyRadius * 0.22f),
                    size = Size(bodyRadius * 0.28f, bodyRadius * 0.14f),
                    style = Stroke(width = bodyRadius * 0.035f, cap = StrokeCap.Round)
                )
            }
            MascotState.Reflecting -> {
                // Deep contemplation state with soft downward gaze & gentle expression
                drawOpenEyes(cx, cy, eyeOffsetX, -0.2f, eyeOffsetY + eyeRadius * 0.1f, eyeRadius, currentBlink, -0.5f)
                drawThinkingEyebrows(cx, cy, eyeOffsetX, eyeOffsetY, bodyRadius, currentEyebrow * 0.3f)
                drawThinkingMouth(cx, cy, bodyRadius)
            }
            MascotState.AmritVela, MascotState.Namaste -> {
                // 1. Multi-layered mystical sunrise dawn aura with warm golden-orange gradients
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD54F).copy(alpha = 0.5f),
                            Color(0xFFFF8F00).copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = bodyRadius * 1.8f * currentAuraScale
                    ),
                    radius = bodyRadius * 1.8f * currentAuraScale,
                    center = Offset(cx, cy)
                )

                // 2. Radiating morning rays (divine dawn glow lines)
                repeat(8) { i ->
                    val angle = i * (2.0 * kotlin.math.PI / 8.0) + (currentAuraScale * 0.2f)
                    val cos = kotlin.math.cos(angle).toFloat()
                    val sin = kotlin.math.sin(angle).toFloat()
                    val rayInner = bodyRadius * 1.15f
                    val rayOuter = bodyRadius * (1.5f + (i % 2) * 0.2f)

                    drawLine(
                        color = Color(0xFFFFF9C4).copy(alpha = 0.45f),
                        start = Offset(cx + rayInner * cos, cy + rayInner * sin),
                        end = Offset(cx + rayOuter * cos, cy + rayOuter * sin),
                        strokeWidth = bodyRadius * 0.035f,
                        cap = StrokeCap.Round
                    )
                }

                // 3. Floating divine morning dew/sparkle particles
                repeat(6) { i ->
                    val progress = ((currentAuraScale * (i + 1) * 1.8f) % 1.0f)
                    val angle = i * (2.0 * kotlin.math.PI / 6.0) + progress
                    val distance = bodyRadius * (1.1f + progress * 0.5f)
                    val alpha = (1f - progress).coerceIn(0f, 1f)

                    drawCircle(
                        color = Color(0xFFFFF59D).copy(alpha = alpha * 0.9f),
                        radius = bodyRadius * 0.05f * (1.2f - progress * 0.4f),
                        center = Offset(
                            cx + kotlin.math.cos(angle).toFloat() * distance,
                            cy - bodyRadius * 0.2f + kotlin.math.sin(angle).toFloat() * distance
                        )
                    )
                }

                // 4. Authentic Sikh Patka (Properly anchored on the head crown)
                drawPatkaHeadwear(cx, cy, bodyRadius)

                // 5. Meditative prayer eyes & smile (Now visible below the forehead wrap)
                if (state == MascotState.Namaste) {
                    drawHappyEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.08f)
                    drawSmile(cx, cy, bodyRadius * 0.26f, bodyRadius * 0.10f, bodyRadius * 0.025f)
                } else {
                    drawClosedEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.08f)
                }

                // 6. Joined Prayer Wings (Namaste / Anjali Mudra pose in front of chest)
                drawNamasteHands(cx, cy, bodyRadius)
            }
            MascotState.Simran -> {
                // Rotating sacred geometric mandala rings for deep meditative focus
                val currentBreathing = breathingScale
                val mandalaRotation = (currentBreathing - 0.97f) * 120f
                withTransform({ rotate(mandalaRotation, Offset(cx, cy)) }) {
                    repeat(8) { i ->
                        val angle = i * (2 * PI / 8)
                        drawCircle(
                            color = Gold.copy(alpha = 0.15f),
                            radius = bodyRadius * 1.4f * currentBreathing,
                            center = Offset(
                                cx + kotlin.math.cos(angle).toFloat() * bodyRadius * 0.12f, 
                                cy + kotlin.math.sin(angle).toFloat() * bodyRadius * 0.12f
                            ),
                            style = Stroke(width = 3.5f)
                        )
                    }
                }
                drawClosedEyes(cx, cy, eyeOffsetX, eyeOffsetY, eyeRadius, bodyRadius * 0.08f)
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
    pupilOffsetY: Float = 0f
) {
    val leftEye = Offset(cx - eyeOffsetX, cy - eyeOffsetY)
    val rightEye = Offset(cx + eyeOffsetX, cy - eyeOffsetY)
    val eyeHeight = eyeRadius * 2f * blinkProgress

    drawOval(color = Color.White, topLeft = Offset(leftEye.x - eyeRadius, leftEye.y - eyeHeight / 2f), size = Size(eyeRadius * 2f, eyeHeight.coerceAtLeast(2f)))
    drawOval(color = Color.White, topLeft = Offset(rightEye.x - eyeRadius, rightEye.y - eyeHeight / 2f), size = Size(eyeRadius * 2f, eyeHeight.coerceAtLeast(2f)))

    if (blinkProgress > 0.25f) {
        val actualPupilOffsetX = pupilOffset * eyeRadius * 0.25f
        val actualPupilOffsetY = pupilOffsetY * eyeRadius * 0.40f 

        drawCircle(
            color = FaceColor,
            radius = eyeRadius * 0.45f,
            center = Offset(leftEye.x + actualPupilOffsetX, leftEye.y + actualPupilOffsetY)
        )

        drawCircle(
            color = FaceColor,
            radius = eyeRadius * 0.45f,
            center = Offset(rightEye.x + actualPupilOffsetX, rightEye.y + actualPupilOffsetY)
        )

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
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Gold.copy(alpha = 0.35f), Gold.copy(alpha = 0.12f), Color.Transparent),
            center = Offset(cx, cy),
            radius = (bodyRadius * 2.2f) * auraScale
        ),
        radius = (bodyRadius * 2.2f) * auraScale,
        center = Offset(cx, cy)
    )
    
    for (i in 0 until 8) {
        val angle = (i * 2 * PI / 8).toFloat()
        val cos = kotlin.math.cos(angle)
        val sin = kotlin.math.sin(angle)
        val startRad = (bodyRadius * 1.15f) * auraScale
        val endRad = (bodyRadius * 1.45f) * auraScale
        drawLine(
            color = Gold.copy(alpha = 0.55f),
            start = Offset(cx + startRad * cos, cy + startRad * sin),
            end = Offset(cx + endRad * cos, cy + endRad * sin),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }

    drawCircle(color = Gold.copy(alpha = 0.75f), radius = bodyRadius * 0.06f, center = Offset(cx, cy - bodyRadius * 1.45f))
}

private fun DrawScope.drawThinkingMouth(cx: Float, cy: Float, bodyRadius: Float) {
    drawLine(color = FaceColor, start = Offset(cx - bodyRadius * 0.12f, cy + bodyRadius * 0.22f), end = Offset(cx + bodyRadius * 0.12f, cy + bodyRadius * 0.22f), strokeWidth = bodyRadius * 0.05f)
}

private fun DrawScope.drawCuteFoot(footCx: Float, footCy: Float, bodyRadius: Float, isLeft: Boolean, wiggle: Float, isWalking: Boolean = false, walkPhase: Float = 0f) {
    // Natural biomimetic foot motion: combine smooth forward/backward swing with vertical step lift
    val angle = if (isWalking) {
        // Natural leg swing arc (±18 degrees max)
        wiggle * 0.72f
    } else {
        if (isLeft) wiggle * 1.2f else -wiggle * 1.2f
    }

    // Step lift: when walking forward (positive swing), lift the foot up slightly
    val stepLift = if (isWalking) {
        val liftFactor = sin(walkPhase * 2 * PI).toFloat()
        if (isLeft) {
            if (liftFactor > 0f) -liftFactor * bodyRadius * 0.12f else 0f
        } else {
            val rightLift = sin(walkPhase * 2 * PI + PI).toFloat()
            if (rightLift > 0f) -rightLift * bodyRadius * 0.12f else 0f
        }
    } else 0f

    val effectiveFootCy = footCy + stepLift

    withTransform({ rotate(degrees = angle, pivot = Offset(footCx, effectiveFootCy - bodyRadius * 0.35f)) }) {
        val legLength = bodyRadius * 0.38f 
        val toeLength = bodyRadius * 0.16f
        val stroke = bodyRadius * 0.065f

        // Leg shaft with smooth rounded joints
        drawLine(
            color = Color(0xFFFF9800),
            start = Offset(footCx, effectiveFootCy - legLength),
            end = Offset(footCx, effectiveFootCy),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        // 3 organic curved toes spreading gently forward
        val toeBend = if (isWalking && stepLift < 0f) 0.8f else 0.4f
        drawLine(color = Color(0xFFFF9800), start = Offset(footCx, effectiveFootCy), end = Offset(footCx - toeLength, effectiveFootCy + toeLength * toeBend), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = Color(0xFFFF9800), start = Offset(footCx, effectiveFootCy), end = Offset(footCx, effectiveFootCy + toeLength * (toeBend + 0.25f)), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color = Color(0xFFFF9800), start = Offset(footCx, effectiveFootCy), end = Offset(footCx + toeLength, effectiveFootCy + toeLength * toeBend), strokeWidth = stroke, cap = StrokeCap.Round)
        
        // Cute toe joint accent dots
        drawCircle(color = Color(0xFFF57C00), radius = stroke * 0.35f, center = Offset(footCx - toeLength, effectiveFootCy + toeLength * toeBend))
        drawCircle(color = Color(0xFFF57C00), radius = stroke * 0.35f, center = Offset(footCx, effectiveFootCy + toeLength * (toeBend + 0.25f)))
        drawCircle(color = Color(0xFFF57C00), radius = stroke * 0.35f, center = Offset(footCx + toeLength, effectiveFootCy + toeLength * toeBend))
    }
}
private fun DrawScope.drawMascotBody(
    cx: Float,
    cy: Float,
    bodyRadius: Float,
    wingFlap: Float,
    hairSway: Float,
    footWiggle: Float,
    walkCycle: Float? = null,
    beakOpenAmount: Float = 0f,
    showBeak: Boolean = true,
    isReadingState: Boolean = false,
    drawWingsOnly: Boolean = false,
    isReflectingState: Boolean = false,
    hasHeadCovering: Boolean = false
) {
    val leftWingX = if (isReadingState) cx - bodyRadius * 0.52f else cx - bodyRadius * 0.50f
    val rightWingX = if (isReadingState) cx + bodyRadius * 0.52f else cx + bodyRadius * 0.50f
    
    val leftWingY = if (isReadingState) cy + bodyRadius * 0.45f else cy + bodyRadius * 0.22f
    val rightWingY = if (isReadingState) cy + bodyRadius * 0.45f else cy + bodyRadius * 0.22f

    val leftWingGradient = Brush.linearGradient(colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F)), start = Offset(cx - bodyRadius * 1.4f, cy - bodyRadius * 0.4f), end = Offset(cx - bodyRadius * 0.6f, cy + bodyRadius * 0.3f))
    val rightWingGradient = Brush.linearGradient(colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F)), start = Offset(cx + bodyRadius * 0.6f, cy - bodyRadius * 0.4f), end = Offset(cx + bodyRadius * 1.4f, cy + bodyRadius * 0.3f))

    // 1. DRAW WINGS FIRST
    if (!isReadingState && !isReflectingState && !drawWingsOnly) {
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
                    start = Offset(leftWingX - bodyRadius * 0.10f, leftWingY + bodyRadius * 0.08f),
                    end = Offset(leftWingX - bodyRadius * (0.30f + i * 0.10f), leftWingY + bodyRadius * (0.15f + i * 0.05f)),
                    strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                )
            }
            drawPath(path = path, color = Color(0xFFF5B041), style = Stroke(2f))
        }
    }

    if (!isReadingState || drawWingsOnly) {
        withTransform({
            if (isReflectingState) {
                rotate(degrees = -30f, pivot = Offset(cx - bodyRadius * 0.38f, cy + bodyRadius * 0.2f))
            } else {
                rotate(degrees = -6f + wingFlap, pivot = Offset(leftWingX, leftWingY))
            }
        }) {
            val path = if (isReflectingState) {
                createWingPath(cx - bodyRadius * 0.38f, cy + bodyRadius * 0.2f, bodyRadius, true, false)
            } else {
                createWingPath(leftWingX, leftWingY, bodyRadius, true, isReadingState)
            }
            drawPath(path = path, brush = leftWingGradient)
            
            if (!isReflectingState) {
                if (isReadingState) {
                    repeat(2) { i ->
                        drawLine(
                            color = Color(0xFFF5B041).copy(alpha = 0.45f),
                            start = Offset(leftWingX - bodyRadius * 0.04f + (i * 9f), leftWingY + bodyRadius * 0.12f),
                            end = Offset(leftWingX - bodyRadius * 0.10f + (i * 9f), leftWingY + bodyRadius * 0.30f),
                            strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                        )
                    }
                } else {
                    repeat(3) { i ->
                        drawLine(
                            color = Color(0xFFF5B041).copy(alpha = 0.45f),
                            start = Offset(leftWingX - bodyRadius * 0.10f, leftWingY + bodyRadius * 0.08f),
                            end = Offset(leftWingX - bodyRadius * (0.30f + i * 0.10f), leftWingY + bodyRadius * (0.15f + i * 0.05f)),
                            strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                        )
                    }
                }
            }
            drawPath(path = path, color = Color(0xFFF5B041), style = Stroke(2f))
        }
    }

    if (!isReflectingState && (!isReadingState || drawWingsOnly)) {
        withTransform({
            rotate(
                degrees = if (isReadingState) -15f else 6f - wingFlap,
                pivot = Offset(rightWingX, rightWingY)
            )
        }) {
            val path = createWingPath(rightWingX, rightWingY, bodyRadius, false, isReadingState)
            drawPath(path = path, brush = rightWingGradient)
            
            if (isReadingState) {
                repeat(2) { i ->
                    drawLine(
                        color = Color(0xFFF5B041).copy(alpha = 0.45f),
                        start = Offset(rightWingX + bodyRadius * 0.04f - (i * 9f), rightWingY + bodyRadius * 0.12f),
                        end = Offset(rightWingX + bodyRadius * 0.10f - (i * 9f), rightWingY + bodyRadius * 0.30f),
                        strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                    )
                }
            } else {
                repeat(3) { i ->
                    drawLine(
                        color = Color(0xFFF5B041).copy(alpha = 0.45f),
                        start = Offset(rightWingX + bodyRadius * 0.10f, rightWingY + bodyRadius * 0.08f),
                        end = Offset(rightWingX + bodyRadius * (0.30f + i * 0.10f), rightWingY + bodyRadius * (0.15f + i * 0.05f)),
                        strokeWidth = bodyRadius * 0.015f, cap = StrokeCap.Round
                    )
                }
            }
            drawPath(path = path, color = Color(0xFFF5B041), style = Stroke(2f))
        }
    }

    // 2. DRAW MAIN BODY
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

        // 3. DRAW FEET AFTER BODY (Positioned correctly at the bottom base with natural biomechanic step lift)
        if (walkCycle != null) {
            // Reduced amplitude (10f) and gentler foot swing for natural forward-walk feel
            val leftWalkWiggle = sin(walkCycle * 2 * PI).toFloat() * 10f
            val rightWalkWiggle = sin(walkCycle * 2 * PI + PI).toFloat() * 10f
            drawCuteFoot(cx - bodyRadius * 0.28f, cy + bodyRadius * 1.25f, bodyRadius, true, leftWalkWiggle, isWalking = true, walkPhase = walkCycle)
            drawCuteFoot(cx + bodyRadius * 0.28f, cy + bodyRadius * 1.25f, bodyRadius, false, rightWalkWiggle, isWalking = true, walkPhase = walkCycle)
        } else {
            drawCuteFoot(cx - bodyRadius * 0.28f, cy + bodyRadius * 1.25f, bodyRadius, true, footWiggle, isWalking = false)
            drawCuteFoot(cx + bodyRadius * 0.28f, cy + bodyRadius * 1.25f, bodyRadius, false, footWiggle, isWalking = false)
        }
        val featherColor = Color(0xFFE65100).copy(alpha = 0.25f)
        val featherStrokeWidth = bodyRadius * 0.04f
        repeat(5) { i ->
            drawArc(color = featherColor, startAngle = 25f, sweepAngle = 130f, useCenter = false, topLeft = Offset(cx - bodyRadius * 0.38f + i * bodyRadius * 0.19f, cy + bodyRadius * 0.18f), size = Size(bodyRadius * 0.22f, bodyRadius * 0.14f), style = Stroke(width = featherStrokeWidth, cap = StrokeCap.Round))
        }

        drawOval(color = Color.White.copy(alpha = 0.45f), topLeft = Offset(cx - bodyRadius * 0.55f, cy - bodyRadius * 0.75f), size = Size(bodyRadius * 0.25f, bodyRadius * 0.60f))
        drawOval(color = Color.White.copy(alpha = 0.12f), topLeft = Offset(cx - bodyRadius * 0.75f, cy - bodyRadius * 0.9f), size = Size(bodyRadius * 1.5f, bodyRadius * 1.8f))

        drawCircle(brush = Brush.radialGradient(colors = listOf(Color(0xFFFF8A80).copy(alpha = 0.9f), Color(0xFFFF8A80).copy(alpha = 0f)), center = Offset(cx - bodyRadius * 0.55f, cy + bodyRadius * 0.08f), radius = bodyRadius * 0.18f), radius = bodyRadius * 0.18f, center = Offset(cx - bodyRadius * 0.55f, cy + bodyRadius * 0.08f))
        drawCircle(brush = Brush.radialGradient(colors = listOf(Color(0xFFFF8A80).copy(alpha = 0.9f), Color(0xFFFF8A80).copy(alpha = 0f)), center = Offset(cx + bodyRadius * 0.55f, cy + bodyRadius * 0.08f), radius = bodyRadius * 0.18f), radius = bodyRadius * 0.18f, center = Offset(cx + bodyRadius * 0.55f, cy + bodyRadius * 0.08f))

        if (!hasHeadCovering) {
            withTransform({ rotate(degrees = hairSway * 0.25f, pivot = Offset(cx, cy - bodyRadius * 1.15f)) }) {
                val tuft = Path().apply {
                    moveTo(cx, cy - bodyRadius * 1.15f)
                    quadraticTo(cx - bodyRadius * 0.18f, cy - bodyRadius * 1.52f, cx - bodyRadius * 0.04f, cy - bodyRadius * 1.40f)
                    quadraticTo(cx, cy - bodyRadius * 1.62f, cx + bodyRadius * 0.04f, cy - bodyRadius * 1.40f)
                    quadraticTo(cx + bodyRadius * 0.18f, cy - bodyRadius * 1.52f, cx, cy - bodyRadius * 1.15f)
                    close()
                }
                drawPath(path = tuft, color = Color(0xFFE65100))
                drawPath(path = tuft, color = Color(0xFFBF360C), style = Stroke(width = bodyRadius * 0.02f))
            }
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
    val width = bodyRadius * 1.15f * bookOpen
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
private fun DrawScope.drawCaringEyes(
    cx: Float,
    cy: Float,
    eyeOffsetX: Float,
    eyeOffsetY: Float,
    eyeRadius: Float,
    strokeWidth: Float
) {
    // Soft downward-curving arcs conveying empathy and gentle reassurance
    drawArc(
        color = FaceColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.5f),
        size = Size(eyeRadius * 2f, eyeRadius * 1.2f),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
    drawArc(
        color = FaceColor,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx + eyeOffsetX - eyeRadius, cy - eyeOffsetY - eyeRadius * 0.5f),
        size = Size(eyeRadius * 2f, eyeRadius * 1.2f),
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

/**
 * Authentic Sikh Patka with snug anatomical head fitting:
 * - Crown dome wrapping naturally over the upper head/skull
 * - Contoured front brow band with V-tuck fold lines
 * - Traditional top Joora (knot) seated firmly on crown with wrap ties
 * - Rich royal saffron/kesri gradient with cloth fold highlights
 */

private fun DrawScope.drawPatkaHeadwear(cx: Float, cy: Float, bodyRadius: Float) {
    val patkaColorTop = Color(0xFFFFB300)    // Saffron Gold
    val patkaColorMid = Color(0xFFFF8F00)    // Warm Kesri
    val patkaColorDark = Color(0xFFE65100)   // Deep Ochre Fold Shadow
    val patkaBorder = Color(0xFFBF360C)      // Trim
    val patkaHighlight = Color(0xFFFFF59D)   // Top Light sheen

    // -------------------------------------------------------------
    // 1. TOP JOORA (Hair bun knot resting flush on top of skull crown)
    // -------------------------------------------------------------
    val jooraCx = cx
    val jooraCy = cy - bodyRadius * 1.08f
    val jooraRadiusX = bodyRadius * 0.22f
    val jooraRadiusY = bodyRadius * 0.18f

    val jooraPath = Path().apply {
        moveTo(jooraCx - jooraRadiusX, jooraCy + jooraRadiusY * 0.3f)
        cubicTo(
            jooraCx - jooraRadiusX * 1.15f, jooraCy - jooraRadiusY * 0.8f,
            jooraCx - jooraRadiusX * 0.5f, jooraCy - jooraRadiusY * 1.3f,
            jooraCx, jooraCy - jooraRadiusY * 1.35f
        )
        cubicTo(
            jooraCx + jooraRadiusX * 0.5f, jooraCy - jooraRadiusY * 1.3f,
            jooraCx + jooraRadiusX * 1.15f, jooraCy - jooraRadiusY * 0.8f,
            jooraCx + jooraRadiusX, jooraCy + jooraRadiusY * 0.3f
        )
        quadraticTo(
            jooraCx, jooraCy + jooraRadiusY * 0.7f,
            jooraCx - jooraRadiusX, jooraCy + jooraRadiusY * 0.3f
        )
        close()
    }

    drawPath(
        path = jooraPath,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF176), patkaColorTop, patkaColorMid, patkaColorDark),
            center = Offset(jooraCx - jooraRadiusX * 0.15f, jooraCy - jooraRadiusY * 0.5f),
            radius = jooraRadiusX * 1.4f
        )
    )
    drawPath(
        path = jooraPath,
        color = patkaBorder,
        style = Stroke(width = bodyRadius * 0.02f)
    )

    // Joora knot highlight sheen
    drawArc(
        color = patkaHighlight.copy(alpha = 0.75f),
        startAngle = 190f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(jooraCx - jooraRadiusX * 0.7f, jooraCy - jooraRadiusY * 1.1f),
        size = Size(jooraRadiusX * 1.4f, jooraRadiusY * 1.1f),
        style = Stroke(width = bodyRadius * 0.025f, cap = StrokeCap.Round)
    )

    // Joora Base Tie Band (Dori wrap securing joora to head)
    val tiePath = Path().apply {
        moveTo(jooraCx - jooraRadiusX * 1.05f, jooraCy + jooraRadiusY * 0.35f)
        quadraticTo(jooraCx, jooraCy + jooraRadiusY * 0.75f, jooraCx + jooraRadiusX * 1.05f, jooraCy + jooraRadiusY * 0.35f)
        quadraticTo(jooraCx, jooraCy + jooraRadiusY * 0.10f, jooraCx - jooraRadiusX * 1.05f, jooraCy + jooraRadiusY * 0.35f)
        close()
    }
    drawPath(path = tiePath, brush = Brush.verticalGradient(listOf(patkaColorMid, patkaColorDark)))
    drawPath(path = tiePath, color = patkaBorder, style = Stroke(width = bodyRadius * 0.016f))

    // -------------------------------------------------------------
    // 2. PATKA HEAD CAP (Restricted to crown and forehead above eyes)
    // -------------------------------------------------------------
    // Brow line stays between cy - 0.38f and cy - 0.46f, safely above eye centers
    val patkaPath = Path().apply {
        // Left temple anchor
        moveTo(cx - bodyRadius * 0.88f, cy - bodyRadius * 0.38f)

        // Up along left skull curvature to top crown
        cubicTo(
            cx - bodyRadius * 0.95f, cy - bodyRadius * 0.75f,
            cx - bodyRadius * 0.60f, cy - bodyRadius * 1.05f,
            cx, cy - bodyRadius * 1.05f
        )

        // Down along right skull curvature to right temple
        cubicTo(
            cx + bodyRadius * 0.60f, cy - bodyRadius * 1.05f,
            cx + bodyRadius * 0.95f, cy - bodyRadius * 0.75f,
            cx + bodyRadius * 0.88f, cy - bodyRadius * 0.38f
        )

        // Front forehead wrap (subtle upward V-dip resting right at brow line)
        quadraticTo(
            cx + bodyRadius * 0.45f, cy - bodyRadius * 0.44f,
            cx, cy - bodyRadius * 0.46f
        )
        quadraticTo(
            cx - bodyRadius * 0.45f, cy - bodyRadius * 0.44f,
            cx - bodyRadius * 0.88f, cy - bodyRadius * 0.38f
        )
        close()
    }

    drawPath(
        path = patkaPath,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF59D), patkaColorTop, patkaColorMid, patkaColorDark),
            center = Offset(cx - bodyRadius * 0.20f, cy - bodyRadius * 0.80f),
            radius = bodyRadius * 1.1f
        )
    )

    // Cloth tension folds along the skull
    val fold1 = Path().apply {
        moveTo(cx - bodyRadius * 0.70f, cy - bodyRadius * 0.55f)
        quadraticTo(cx, cy - bodyRadius * 0.65f, cx + bodyRadius * 0.70f, cy - bodyRadius * 0.55f)
    }
    val fold2 = Path().apply {
        moveTo(cx - bodyRadius * 0.60f, cy - bodyRadius * 0.80f)
        quadraticTo(cx, cy - bodyRadius * 0.92f, cx + bodyRadius * 0.60f, cy - bodyRadius * 0.80f)
    }

    drawPath(path = fold1, color = patkaHighlight.copy(alpha = 0.5f), style = Stroke(width = bodyRadius * 0.024f, cap = StrokeCap.Round))
    drawPath(path = fold2, color = patkaColorDark.copy(alpha = 0.4f), style = Stroke(width = bodyRadius * 0.020f, cap = StrokeCap.Round))

    // Front forehead brow band (Patti band trim across brow line)
    val browBand = Path().apply {
        moveTo(cx - bodyRadius * 0.88f, cy - bodyRadius * 0.38f)
        quadraticTo(cx, cy - bodyRadius * 0.46f, cx + bodyRadius * 0.88f, cy - bodyRadius * 0.38f)
        quadraticTo(cx + bodyRadius * 0.80f, cy - bodyRadius * 0.50f, cx, cy - bodyRadius * 0.54f)
        quadraticTo(cx - bodyRadius * 0.80f, cy - bodyRadius * 0.50f, cx - bodyRadius * 0.88f, cy - bodyRadius * 0.38f)
        close()
    }
    drawPath(path = browBand, color = patkaColorDark.copy(alpha = 0.25f))
    drawPath(path = browBand, color = patkaBorder.copy(alpha = 0.55f), style = Stroke(width = bodyRadius * 0.016f))

    // Crisp outer contour outline
    drawPath(path = patkaPath, color = patkaBorder, style = Stroke(width = bodyRadius * 0.026f))

    // -------------------------------------------------------------
    // 3. SIDE TIES (Larh / Strings tucked neatly by the temples/ears)
    // -------------------------------------------------------------
    val leftTie = Path().apply {
        moveTo(cx - bodyRadius * 0.86f, cy - bodyRadius * 0.38f)
        quadraticTo(
            cx - bodyRadius * 0.96f, cy - bodyRadius * 0.20f,
            cx - bodyRadius * 0.92f, cy - bodyRadius * 0.02f
        )
    }
    val rightTie = Path().apply {
        moveTo(cx + bodyRadius * 0.86f, cy - bodyRadius * 0.38f)
        quadraticTo(
            cx + bodyRadius * 0.96f, cy - bodyRadius * 0.20f,
            cx + bodyRadius * 0.92f, cy - bodyRadius * 0.02f
        )
    }

    drawPath(path = leftTie, color = patkaColorDark, style = Stroke(width = bodyRadius * 0.032f, cap = StrokeCap.Round))
    drawPath(path = leftTie, color = patkaColorMid, style = Stroke(width = bodyRadius * 0.018f, cap = StrokeCap.Round))
    drawPath(path = rightTie, color = patkaColorDark, style = Stroke(width = bodyRadius * 0.032f, cap = StrokeCap.Round))
    drawPath(path = rightTie, color = patkaColorMid, style = Stroke(width = bodyRadius * 0.018f, cap = StrokeCap.Round))
}

/**
 * Joined Namaste Prayer Wings / Hands in front of chest (Anjali Mudra)
 */
private fun DrawScope.drawNamasteHands(cx: Float, cy: Float, bodyRadius: Float) {
    val handY = cy + bodyRadius * 0.32f
    val handWidth = bodyRadius * 0.28f
    val handHeight = bodyRadius * 0.42f

    // Soft warm golden gradient for prayer hands
    val prayerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFFB300)),
        startY = handY - handHeight * 0.5f,
        endY = handY + handHeight * 0.5f
    )

    // Left folded wing
    val leftHand = Path().apply {
        moveTo(cx, handY - handHeight * 0.5f)
        cubicTo(
            cx - handWidth * 0.8f, handY - handHeight * 0.2f,
            cx - handWidth, handY + handHeight * 0.2f,
            cx - handWidth * 0.3f, handY + handHeight * 0.5f
        )
        quadraticTo(cx, handY + handHeight * 0.4f, cx, handY - handHeight * 0.5f)
        close()
    }

    // Right folded wing
    val rightHand = Path().apply {
        moveTo(cx, handY - handHeight * 0.5f)
        cubicTo(
            cx + handWidth * 0.8f, handY - handHeight * 0.2f,
            cx + handWidth, handY + handHeight * 0.2f,
            cx + handWidth * 0.3f, handY + handHeight * 0.5f
        )
        quadraticTo(cx, handY + handHeight * 0.4f, cx, handY - handHeight * 0.5f)
        close()
    }

    // Center seam shadow
    drawPath(path = leftHand, brush = prayerGradient)
    drawPath(path = rightHand, brush = prayerGradient)
    drawPath(path = leftHand, color = Color(0xFFF5B041), style = Stroke(width = 2.2f))
    drawPath(path = rightHand, color = Color(0xFFF5B041), style = Stroke(width = 2.2f))

    // Center prayer seam dividing the two folded hands
    drawLine(
        color = Color(0xFFE65100).copy(alpha = 0.7f),
        start = Offset(cx, handY - handHeight * 0.48f),
        end = Offset(cx, handY + handHeight * 0.45f),
        strokeWidth = bodyRadius * 0.02f,
        cap = StrokeCap.Round
    )

    // Divine prayer glow aura around hands
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Gold.copy(alpha = 0.4f), Color.Transparent),
            center = Offset(cx, handY),
            radius = handWidth * 1.5f
        ),
        radius = handWidth * 1.5f,
        center = Offset(cx, handY)
    )
}