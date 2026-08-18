package com.chris.threed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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
) = this.drawBehind {
    val elevationPx = elevation.toPx()

    // Convert degree (where 0 is up) to radians used by standard math
    // Compose/Standard math uses 0 for right, so we shift -90.
    val radian = (degree - 90f) * (PI / 180f).toFloat()

    // Polar to Cartesian coordinate conversion
    val xOffset = cos(radian) * elevationPx
    val yOffset = sin(radian) * elevationPx

    // Calculate the geometry of the shape based on the composable's size
    val outline = shape.createOutline(size, layoutDirection, this)

    // Pre-create the paint object for performance within the draw scope
    val wallPaint = Paint().apply {
        paint.applyTo(size, this, 1f) // Applies the brush to the paint
        isAntiAlias = true
    }

    drawIntoCanvas { canvas ->
        // --- DRAW THE BODY / CONNECTING WALLS ---

        // We iterate drawing the outline N times with increasing offsets
        // to create smooth connecting walls. This creates a solid geometric volume.
        val steps = elevationPx.roundToInt()

        // Limit steps to prevent extreme rendering costs for huge elevations
        val drawSteps = if (steps < 1) 1 else if (steps > 200) 200 else steps

        for (step in 1..drawSteps) {
            // Calculate the fractional translation for this step
            val fraction = step.toFloat() / drawSteps
            val tx = xOffset * fraction
            val ty = yOffset * fraction

            canvas.withSave {
                // Translate the canvas slightly
                canvas.translate(tx, ty)

                // Draw the outline using the wall paint.
                // This repeatedly "smears" the shape across the path, creating the 3D depth.
                canvas.drawOutline(outline, wallPaint)
            }
        }
    }
}


// --- Usage Examples ---

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
        // Example 1: 3D Text on a box
        val boxShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .size(width = 160.dp, height = 100.dp)
                // APPLYING 3D EFFECT
                .to3D(
                    elevation = 20.dp,
                    degree = 125f, // Southeast
                    // Blue-to-DarkBlue Gradient body
                    paint = Brush.verticalGradient(listOf(Color(0xFF3366FF), Color(0xFF1133AA))),
                    shape = boxShape
                )
                .background(Color(0xFF66CCFF), boxShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "3D BOX",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}