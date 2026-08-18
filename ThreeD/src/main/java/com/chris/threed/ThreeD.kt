package com.chris.threed

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Extension modifier to create a 3D extrusion effect with a soft shadow and colored glow.
 * Optimized with [drawWithCache] to minimize per-frame allocations.
 */
fun Modifier.to3D(
    elevation: () -> Dp,
    paint: () -> Brush,
    shape: Shape,
    degree: Float = 0f,
    shadowColor: Color = Color.Black.copy(alpha = 0.5f),
    glowAlpha: Float = 0.7f,
) = this
    .graphicsLayer {
        val elevationPx = elevation().toPx()
        val radian = (degree - 90f) * (PI / 180f).toFloat()
        translationX = cos(radian) * elevationPx
        translationY = sin(radian) * elevationPx
    }
    .drawWithCache {
        // 1. PRE-CALCULATE GEOMETRY
        val outline = shape.createOutline(size, layoutDirection, this)
        val androidPath = when (outline) {
            is Outline.Generic -> outline.path.asAndroidPath()
            is Outline.Rectangle -> android.graphics.Path().apply {
                addRect(outline.rect.left, outline.rect.top, outline.rect.right, outline.rect.bottom, android.graphics.Path.Direction.CW)
            }
            is Outline.Rounded -> android.graphics.Path().apply {
                val rr = outline.roundRect
                addRoundRect(
                    rr.left, rr.top, rr.right, rr.bottom,
                    floatArrayOf(
                        rr.topLeftCornerRadius.x, rr.topLeftCornerRadius.y,
                        rr.topRightCornerRadius.x, rr.topRightCornerRadius.y,
                        rr.bottomRightCornerRadius.x, rr.bottomRightCornerRadius.y,
                        rr.bottomLeftCornerRadius.x, rr.bottomLeftCornerRadius.y
                    ),
                    android.graphics.Path.Direction.CW
                )
            }
        }

        // 2. PRE-ALLOCATE PAINTS
        val wallPaint = Paint().apply { isAntiAlias = true }
        // Pre-create the Compose wrapper for the glow paint to allow Brush application
        val composeGlowPaint = Paint().apply { isAntiAlias = true }
        val androidGlowPaint = composeGlowPaint.asFrameworkPaint().apply {
            style = android.graphics.Paint.Style.FILL
        }
        val shadowPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = shadowColor.toArgb()
        }

        onDrawBehind {
            val elevationPx = elevation().toPx()
            if (elevationPx < 1f) return@onDrawBehind

            val radian = (degree - 90f) * (PI / 180f).toFloat()
            val xOffset = cos(radian) * elevationPx
            val yOffset = sin(radian) * elevationPx

            drawIntoCanvas { canvas ->
                // --- 3. DRAW COLORED GLOW (BLOOM) ---
                // Apply the brush directly to our pre-allocated paint
                paint().applyTo(size, composeGlowPaint, glowAlpha)
                androidGlowPaint.maskFilter = BlurMaskFilter(elevationPx * 2.5f, BlurMaskFilter.Blur.OUTER)
                canvas.nativeCanvas.drawPath(androidPath, androidGlowPaint)

                // --- 4. DRAW MAIN CAST SHADOW (AT BASE) ---
                shadowPaint.setShadowLayer(elevationPx * 2.0f, 0f, 0f, shadowColor.toArgb())
                canvas.withSave {
                    canvas.translate(-xOffset, -yOffset)
                    canvas.nativeCanvas.drawPath(androidPath, shadowPaint)
                }

                // --- 5. DRAW 3D WALLS (OPTIMIZED STEPS) ---
                paint().applyTo(size, wallPaint, 1f)
                val stepSize = 3f // px for performance
                val steps = (elevationPx / stepSize).roundToInt().coerceAtLeast(1)
                
                for (i in 1..steps) {
                    val fraction = i.toFloat() / steps
                    val tx = -xOffset * fraction
                    val ty = -yOffset * fraction

                    canvas.withSave {
                        canvas.translate(tx, ty)
                        canvas.drawOutline(outline, wallPaint)
                    }
                }
            }
        }
    }

/**
 * Convenience overload for static values.
 */
fun Modifier.to3D(
    elevation: Dp,
    paint: Brush,
    shape: Shape,
    degree: Float = 0f,
    shadowColor: Color = Color.Black.copy(alpha = 0.5f),
    glowAlpha: Float = 0.7f,
) = to3D(
    elevation = { elevation },
    paint = { paint },
    shape = shape,
    degree = degree,
    shadowColor = shadowColor,
    glowAlpha = glowAlpha
)

@Composable
fun rememberAnimatedRgbBrush(): () -> Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "rgb")
    val phaseState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    return remember(phaseState) {
        {
            Brush.linearGradient(
                colors = listOf(
                    Color.Red.copy(alpha = 0.5f),
                    Color.Yellow.copy(alpha = 0.5f),
                    Color.Green.copy(0.5f),
                    Color.Cyan.copy(alpha = 0.5f),
                    Color.Blue.copy(alpha = 0.5f),
                    Color.Magenta.copy(alpha = 0.5f),
                    Color.Red.copy(alpha = 0.5f)
                ),
                start = Offset(phaseState.value, 0f),
                end = Offset(phaseState.value + 500f, 500f),
                tileMode = TileMode.Repeated
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Test3DExtrusion() {
    Box(
        modifier = Modifier
            .size(400.dp)
            .background(Color.White)
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val elevationState = animateDpAsState(
            targetValue = if (isPressed) 2.dp else 12.dp,
            label = "elevation"
        )

        val rgbBrushProvider = rememberAnimatedRgbBrush()

        val boxShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 80.dp)
                .to3D(
                    elevation = { elevationState.value },
                    paint = rgbBrushProvider,
                    shape = boxShape,
                    degree = 135f
                )
                .background(Color(0xFF222222), boxShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (isPressed) "CLICKED!" else "CLICK ME",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}
