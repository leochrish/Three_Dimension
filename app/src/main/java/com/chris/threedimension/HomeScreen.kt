package com.chris.threedimension

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chris.threed.rememberAnimatedRgbBrush
import com.chris.threed.to3D

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF141A36))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = "3D Compose Samples",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // 1. Animated RGB Button
        SampleThreeDButton(
            text = "RGB BUTTON",
            useRgb = true
        )

        // 2. Solid Color 3D Card
        SampleThreeDCard()

        // 3. 3D Icon Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SampleThreeDIconButton(Icons.Default.Favorite, Color.Red)
            SampleThreeDIconButton(Icons.Default.Home, Color(0xFF2196F3))
            SampleThreeDIconButton(Icons.Default.Settings, Color.Gray)
        }

        // 4. 3D Profile Image
        SampleThreeDProfile()

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SampleThreeDButton(
    text: String,
    useRgb: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(
        targetValue = if (isPressed) 0.dp else 10.dp,
        label = "button_elevation"
    )

    val staticPaint = Brush.verticalGradient(listOf(Color(0xFFFF9800), Color(0xFFE65100)))
    val rgbBrushProvider = if (useRgb) rememberAnimatedRgbBrush() else null

    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .width(240.dp)
            .height(60.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = { rgbBrushProvider?.invoke() ?: staticPaint },
                shape = shape,
                degree = 45f
            )
            .background(if (useRgb) Color(0xFF111111) else Color(0xFFFFB74D), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SampleThreeDCard() {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .to3D(
                elevation = 8.dp,
                degree = 135f,
                paint = SolidColor(Color(0xFFBBBBBB)),
                shape = shape
            )
            .background(Color.White, shape)
            .padding(20.dp)
    ) {
        Column {
            Text(
                "3D SURFACE",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This card uses a solid color extrusion to create a clean, physical look.",
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun SampleThreeDIconButton(icon: ImageVector, color: Color) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(
        targetValue = if (isPressed) 0.dp else 6.dp,
        label = "icon_elevation"
    )

    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .size(56.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = { SolidColor(color.copy(alpha = 0.7f)) },
                shape = shape,
                degree = 45f
            )
            .background(Color.White, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color)
    }
}

@Composable
fun SampleThreeDProfile() {
    val shape = CircleShape
    val rgbBrushProvider = rememberAnimatedRgbBrush()
    
    Box(
        modifier = Modifier
            .size(120.dp)
            .to3D(
                elevation = { 12.dp },
                paint = rgbBrushProvider,
                shape = shape,
                degree = 225f
            )
            .background(Color.LightGray, shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
