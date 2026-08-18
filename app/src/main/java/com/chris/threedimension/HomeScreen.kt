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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Terminal
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
import com.chris.threed.rememberAnimatedGradientBrush
import com.chris.threed.to3D

// Professional "Fresh & Neat" Dark Palette
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val AzureLight = Color(0xFF38BDF8)
val IndigoLight = Color(0xFF818CF8)
val EmeraldLight = Color(0xFF34D399)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Three Dimension",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-1.5).sp
            )
            Text(
                text = "FRESH • NEAT • DYNAMIC",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AzureLight,
                letterSpacing = 4.sp
            )
        }

        // 1. Premium Glass-style Feature Card
        GlassFeatureCard()

        // 2. Neat Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NeatActionButton(Icons.Default.Terminal, EmeraldLight)
            NeatActionButton(Icons.Default.Fingerprint, AzureLight)
            NeatActionButton(Icons.Default.AutoAwesome, IndigoLight)
        }

        // 3. Smooth Flow Card
        SmoothFlowCard()

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun GlassFeatureCard() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(if (isPressed) 2.dp else 16.dp, label = "glass")
    
    // Azure to Indigo Gradient - Fresh and professional
    val azureIndigoBrush = rememberAnimatedGradientBrush(
        colors = listOf(AzureLight, IndigoLight, AzureLight),
        durationMillis = 4000
    )
    
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = azureIndigoBrush,
                shape = shape,
                degree = 135f,
                shadowColor = Color.Black.copy(alpha = 0.6f)
            )
            .background(Slate800, shape)
            .clickable(interactionSource, null) { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = AzureLight,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "ADVANCED LAYERING",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 1.sp
            )
            Text(
                "Click to explore depth configurations",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun NeatActionButton(icon: ImageVector, accentColor: Color) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(if (isPressed) 0.dp else 12.dp, label = "neat_btn")
    val shape = CircleShape

    Box(
        modifier = Modifier
            .size(80.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = { SolidColor(accentColor.copy(alpha = 0.8f)) },
                shape = shape,
                degree = 45f
            )
            .background(Slate800, shape)
            .clickable(interactionSource, null) { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun SmoothFlowCard() {
    val shape = RoundedCornerShape(topStart = 48.dp, bottomEnd = 48.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevationState = animateDpAsState(if (isPressed) 4.dp else 14.dp, label = "smooth")
    
    val auroraBrush = rememberAnimatedGradientBrush(
        colors = listOf(EmeraldLight, AzureLight, IndigoLight, EmeraldLight),
        durationMillis = 6000
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .to3D(
                elevation = { elevationState.value },
                paint = auroraBrush,
                shape = shape,
                degree = 225f,
                glowAlpha = 0.4f
            )
            .background(Slate800, shape)
            .clickable(interactionSource, null) { }
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "SMOOTH FLOW",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    "Status: Optimized",
                    color = EmeraldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
