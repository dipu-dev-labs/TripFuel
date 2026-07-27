package com.dj.tripfuel.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var stage by remember { mutableStateOf(0) }

    val fuelScale by animateFloatAsState(
        targetValue = if (stage >= 1) 1.0f else 0.2f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fuelScale"
    )

    val bikeScale by animateFloatAsState(
        targetValue = if (stage >= 2) 1.0f else 0.0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "bikeScale"
    )

    val gpsAlpha by animateFloatAsState(
        targetValue = if (stage >= 3) 1.0f else 0.0f,
        animationSpec = tween(500),
        label = "gpsAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (stage >= 4) 1.0f else 0.0f,
        animationSpec = tween(600),
        label = "textAlpha"
    )

    LaunchedEffect(Unit) {
        delay(300)
        stage = 1 // Fuel drop
        delay(400)
        stage = 2 // Bike outline
        delay(400)
        stage = 3 // GPS pulse
        delay(400)
        stage = 4 // TripFuel text
        delay(800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF14241C),
                        BackgroundDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // GPS Pulse ring
                if (stage >= 3) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SecondaryTeal.copy(alpha = gpsAlpha),
                        modifier = Modifier
                            .size(130.dp)
                            .scale(1.1f)
                    )
                }

                // Bike & Fuel Drop animation container
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier
                            .size(44.dp)
                            .scale(fuelScale)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier
                            .size(54.dp)
                            .scale(bikeScale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha)
            ) {
                Text(
                    text = "TripFuel",
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 38.sp,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SMART FUEL & RIDE PROFIT ENGINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryTeal,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
