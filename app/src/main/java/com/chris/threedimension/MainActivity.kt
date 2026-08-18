package com.chris.threedimension

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chris.threed.to3D
import com.chris.threedimension.ui.theme.ThreeDimensionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreeDimensionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThreeDimensionTheme {
        Greeting("Android")
    }
}