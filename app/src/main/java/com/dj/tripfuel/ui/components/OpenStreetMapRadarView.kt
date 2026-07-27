package com.dj.tripfuel.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.dj.tripfuel.model.NearbyRiderModel
import com.dj.tripfuel.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OpenStreetMapRadarView(
    nearbyRiders: List<NearbyRiderModel>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(
        modifier = modifier
            .background(Color(0xFF090D12))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2.2f)

            // Draw OpenStreetMap Dark Grid Lines
            val gridStep = 70f
            var x = 0f
            while (x < width) {
                drawLine(
                    color = Color(0x0CFFFFFF),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridStep
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color(0x0CFFFFFF),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // Draw Concentric Radar Rings (250m, 500m, 1km scale)
            val ringRadii = listOf(100f, 200f, 320f)
            ringRadii.forEachIndexed { index, radius ->
                drawCircle(
                    color = PrimaryGreen.copy(alpha = 0.15f - (index * 0.03f)),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }

            // Draw Rotating Radar Beam Sweep
            val sweepRad = Math.toRadians(sweepAngle.toDouble())
            val lineEnd = Offset(
                (center.x + 320f * cos(sweepRad)).toFloat(),
                (center.y + 320f * sin(sweepRad)).toFloat()
            )
            drawLine(
                brush = Brush.radialGradient(
                    colors = listOf(SecondaryTeal.copy(alpha = 0.6f), Color.Transparent),
                    center = center,
                    radius = 320f
                ),
                start = center,
                end = lineEnd,
                strokeWidth = 3f
            )

            // Draw Self Rider Pulse Center Marker
            drawCircle(
                color = PrimaryGreen.copy(alpha = 0.3f),
                radius = 24f,
                center = center
            )
            drawCircle(
                color = PrimaryGreen,
                radius = 8f,
                center = center
            )

            // Draw Nearby Rider Pulse Markers on OpenStreetMap Radar
            nearbyRiders.forEach { rider ->
                // Map distance to Canvas scale
                val maxRadius = 320f
                val mappedRadius = ((rider.distanceMeters / 1000f) * maxRadius).coerceIn(40f, maxRadius)
                val angleRad = Math.toRadians((rider.headingDegrees % 360).toDouble())

                val riderPos = Offset(
                    (center.x + mappedRadius * cos(angleRad)).toFloat(),
                    (center.y + mappedRadius * sin(angleRad)).toFloat()
                )

                val badgeColor = when (rider.platform) {
                    "Zomato" -> Color(0xFFFF5252)
                    "Rapido" -> WarningYellow
                    "Swiggy" -> Color(0xFFFF9800)
                    "Uber Moto" -> SecondaryTeal
                    else -> PrimaryGreen
                }

                // Outer aura pulse
                drawCircle(
                    color = badgeColor.copy(alpha = 0.25f),
                    radius = 18f,
                    center = riderPos
                )
                // Solid rider dot
                drawCircle(
                    color = badgeColor,
                    radius = 8f,
                    center = riderPos
                )
            }
        }
    }
}
