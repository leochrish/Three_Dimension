package com.chris.threed

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
import androidx.compose.ui.draw.drawBehind
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
 * Extension modifier to create a 3D extrusion effect with a soft shadow.
 *
 * @param elevation The depth/length of the 3D extrusion.
 * @param paint The [Brush] used to paint the 3D body/walls.
 * @param shape The [Shape] of the UI element.
 * @param degree The angle (in degrees) to offset the extrusion.
 * @param shadowColor The color of the shadow.
 */
fun Modifier.to3D(
    elevation: Dp,
    paint: Brush,
    shape: Shape,
    degree: Float = 0f,
    shadowColor: Color = Color.Black.copy(alpha = 0.3f),
) = this
    .graphicsLayer {
        val elevationPx = elevation.toPx()
        val radian = (degree - 90f) * (PI / 180f).toFloat()
        translationX = cos(radian) * elevationPx
        translationY = sin(radian) * elevationPx
    }
    .drawBehind {
        val elevationPx = elevation.toPx()
        if (elevationPx < 1f) return@drawBehind

        val radian = (degree - 90f) * (PI / 180f).toFloat()
        val xOffset = cos(radian) * elevationPx
        val yOffset = sin(radian) * elevationPx

        val outline = shape.createOutline(size, layoutDirection, this)

        drawIntoCanvas { canvas ->
            // --- 1. DRAW SHADOW ---
            // The shadow is drawn at the base position (before 3D translation)
            val shadowPaint = android.graphics.Paint().apply {
                color = shadowColor.toArgb()
                isAntiAlias = true
                setShadowLayer(
                    elevationPx * 1.2f, // shadow blur radius
                    0f, 0f,             // shadow offset
                    shadowColor.toArgb()
                )
            }

            canvas.withSave {
                // Move back to origin relative to translated graphicsLayer
                canvas.translate(-xOffset, -yOffset)
                
                val path = when (outline) {
                    is Outline.Generic -> outline.path.asAndroidPath()
                    is Outline.Rectangle -> android.graphics.Path().apply {
                        addRect(
                            outline.rect.left, outline.rect.top,
                            outline.rect.right, outline.rect.bottom,
                            android.graphics.Path.Direction.CW
                        )
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
                canvas.nativeCanvas.drawPath(path, shadowPaint)
            }

            // --- 2. DRAW CONNECTING WALLS ---
            val wallPaint = Paint().apply {
                paint.applyTo(size, this, 1f)
                isAntiAlias = true
            }

            val steps = elevationPx.roundToInt()
            val drawSteps = if (steps < 1) 1 else if (steps > 200) 200 else steps

            for (step in 1..drawSteps) {
                val fraction = step.toFloat() / drawSteps
                val tx = -xOffset * fraction
                val ty = -yOffset * fraction

                canvas.withSave {
                    canvas.translate(tx, ty)
                    canvas.drawOutline(outline, wallPaint)
                }
            }
        }
    }

@Composable
fun rememberAnimatedRgbBrush(): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "rgb")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    return Brush.linearGradient(
        colors = listOf(
            Color.Red.copy(alpha = 0.5f),
            Color.Yellow.copy(alpha = 0.5f),
            Color.Green.copy(0.5f),
            Color.Cyan.copy(alpha = 0.5f),
            Color.Blue.copy(alpha = 0.5f),
            Color.Magenta.copy(alpha = 0.5f),
            Color.Red.copy(alpha = 0.5f)
        ),
        start = Offset(phase, 0f),
        end = Offset(phase + 500f, 500f),
        tileMode = TileMode.Repeated
    )
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

        val elevation by animateDpAsState(
            targetValue = if (isPressed) 2.dp else 12.dp,
            label = "elevation"
        )

        val rgbBrush = rememberAnimatedRgbBrush()

        val boxShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 80.dp)
                .to3D(
                    elevation = elevation,
                    degree = 135f,
                    paint = rgbBrush,
                    shape = boxShape
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
