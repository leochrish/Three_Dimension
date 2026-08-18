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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chris.threed.rememberAnimatedRgbBrush
import com.chris.threed.to3D

// Modern Dark Theme Palette
val BgColor = Color(0xFF0F172A)
val SurfaceColor = Color(0xFF1E293B)
val AccentColor = Color(0xFF38BDF8)
val NeonPurple = Color(0xFFA855F7)
val NeonPink = Color(0xFFEC4899)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgColor)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        
        Text(
            text = "3D Showcase",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = (-1).sp
        )

        // 1. Cyberpunk Action Card (Rectangle with sharp corners)
        CyberpunkCard()

        // 2. Neon Launch Button (Circle with RGB glow)
        LaunchButton()

        // 3. Neumorphic Control Pad
        ControlPad()

        // 4. Status Indicator (Asymmetric Squircle)
        StatusCard()

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun CyberpunkCard() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(if (isPressed) 2.dp else 14.dp, label = "card")
    val rgbBrush = rememberAnimatedRgbBrush()
    val shape = RectangleShape

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = rgbBrush,
                shape = shape,
                degree = 135f
            )
            .background(SurfaceColor, shape)
            .clickable(interactionSource, null) { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = AccentColor,
                modifier = Modifier.size(48.dp)
            )
            Text(
                "SYSTEM OVERDRIVE",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 2.sp
            )
            Text(
                "TAP TO INITIALIZE",
                color = AccentColor.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LaunchButton() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(if (isPressed) 0.dp else 20.dp, label = "launch")
    val rgbBrush = rememberAnimatedRgbBrush()
    val shape = CircleShape

    Box(
        modifier = Modifier
            .size(120.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = rgbBrush,
                shape = shape,
                degree = 45f
            )
            .background(Color(0xFF111111), shape)
            .clickable(interactionSource, null) { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.RocketLaunch,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
fun ControlPad() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PadButton(Icons.Default.PlayArrow, AccentColor)
        PadButton(Icons.Default.Add, NeonPurple)
        PadButton(Icons.Default.Done, NeonPink)
    }
}

@Composable
fun PadButton(icon: ImageVector, color: Color) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(if (isPressed) 2.dp else 10.dp, label = "pad")
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .size(70.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = { SolidColor(color) },
                shape = shape,
                degree = 45f
            )
            .background(SurfaceColor, shape)
            .clickable(interactionSource, null) { },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun StatusCard() {
    val shape = RoundedCornerShape(topStart = 40.dp, bottomEnd = 40.dp) // Asymmetric Squircle
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(if (isPressed) 4.dp else 12.dp, label = "status")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = { Brush.horizontalGradient(listOf(NeonPurple, NeonPink)) },
                shape = shape,
                degree = 225f
            )
            .background(SurfaceColor, shape)
            .clickable(interactionSource, null) { }
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Green, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("NETWORK STATUS", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                Text("OPTIMIZED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
