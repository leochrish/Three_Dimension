package com.chris.threed

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
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
 * Extension modifier to create a 3D extrusion effect.
 *
 * @param elevation The depth/length of the 3D extrusion.
 * @param paint The [Brush] (solid color, gradient) used to paint the 3D body/walls.
 * @param shape The [Shape] of the UI element (required to define the extrusion contour).
 * @param degree The angle (in degrees) to offset the extrusion. 0 is North/Up, 90 is East/Right.
 */
fun Modifier.to3D(
    elevation: Dp,
    paint: Brush,
    shape: Shape,
    degree: Float = 0f,
) = this
    .graphicsLayer {
        val elevationPx = elevation.toPx()
        val radian = (degree - 90f) * (PI / 180f).toFloat()
        translationX = cos(radian) * elevationPx
        translationY = sin(radian) * elevationPx
    }
    .drawBehind {
        val elevationPx = elevation.toPx()
        val radian = (degree - 90f) * (PI / 180f).toFloat()

        val xOffset = cos(radian) * elevationPx
        val yOffset = sin(radian) * elevationPx

        val outline = shape.createOutline(size, layoutDirection, this)

        val wallPaint = Paint().apply {
            paint.applyTo(size, this, 1f)
            isAntiAlias = true
        }

        drawIntoCanvas { canvas ->
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

        val boxShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 80.dp)
                .to3D(
                    elevation = elevation,
                    degree = 135f,
                    paint = Brush.verticalGradient(
                        listOf(Color(0xFF3366FF), Color(0xFF1133AA))
                    ),
                    shape = boxShape
                )
                .background(Color(0xFF66CCFF), boxShape)
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
