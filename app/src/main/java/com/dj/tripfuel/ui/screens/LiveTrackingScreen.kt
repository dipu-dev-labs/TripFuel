package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.tracking.LiveRideState
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.theme.*

@Composable
fun LiveTrackingScreen(
    settings: UserSettings,
    liveState: LiveRideState,
    onStopRideClick: () -> Unit,
    onToggleBatterySaver: () -> Unit,
    onToggleSimulationMode: () -> Unit,
    onBackClick: () -> Unit
) {
    val durationText = remember(liveState.durationSec) {
        val hours = liveState.durationSec / 3600
        val mins = (liveState.durationSec % 3600) / 60
        val secs = liveState.durationSec % 60
        if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, mins, secs)
        } else {
            String.format("%02d:%02d", mins, secs)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // --- ANIMATED GPS MAP CANVAS VIEW ---
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height

            // Draw dark futuristic map background grid
            val gridStep = 80f
            var x = 0f
            while (x < width) {
                drawLine(
                    color = Color(0x0EFFFFFF),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridStep
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = Color(0x0EFFFFFF),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // Draw simulated road lines / navigation trail
            val routePath = Path()
            val center = Offset(width / 2f, height / 2.2f)
            routePath.moveTo(center.x - 180f, center.y + 350f)
            routePath.cubicTo(
                center.x - 120f, center.y + 150f,
                center.x + 140f, center.y + 50f,
                center.x, center.y
            )

            // Draw trail glow
            drawPath(
                path = routePath,
                color = if (liveState.isSimulationMode) SecondaryTeal.copy(alpha = 0.25f) else PrimaryGreen.copy(alpha = 0.25f),
                style = Stroke(width = 16f)
            )
            // Draw main route stroke
            drawPath(
                path = routePath,
                color = if (liveState.isSimulationMode) SecondaryTeal else PrimaryGreen,
                style = Stroke(width = 6f)
            )

            // Draw current rider position pulse marker
            drawCircle(
                color = SecondaryTeal.copy(alpha = 0.35f),
                radius = 36f,
                center = center
            )
            drawCircle(
                color = PrimaryGreen,
                radius = 14f,
                center = center
            )
            drawCircle(
                color = BackgroundDark,
                radius = 6f,
                center = center
            )
        }

        // --- TOP FROSTED GLASS HUD CARD ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x260F1722),
                borderColor = if (liveState.isSimulationMode) SecondaryTeal else PrimaryGreen
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (liveState.isSimulationMode) SecondaryTeal else PrimaryGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (liveState.isSimulationMode) "DEMO SIMULATOR ACTIVE" else "REAL GPS TRACKING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (liveState.isSimulationMode) SecondaryTeal else PrimaryGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = if (liveState.isSimulationMode) "Simulating indoor rider movement" else "Accurate ±${liveState.gpsAccuracyMeters}m (Stationary if not moving)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Demo Simulation Toggle Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (liveState.isSimulationMode) SecondaryTeal.copy(alpha = 0.25f) else Color(0x1AFFFFFF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable(onClick = onToggleSimulationMode)
                        ) {
                            Text(
                                text = if (liveState.isSimulationMode) "DEMO ON" else "DEMO OFF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = if (liveState.isSimulationMode) SecondaryTeal else TextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Battery Saver Toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (liveState.isBatterySaver) WarningYellow.copy(alpha = 0.2f) else Color(0x1AFFFFFF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            IconButton(onClick = onToggleBatterySaver, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = if (liveState.isBatterySaver) Icons.Default.BatterySaver else Icons.Default.BatteryFull,
                                    contentDescription = null,
                                    tint = if (liveState.isBatterySaver) WarningYellow else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- BOTTOM STATS OVERLAY HUD CARD ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xCC0B0F14),
                borderColor = GlassBorderDark
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Speedometer Large Display
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "SPEED",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${liveState.currentSpeedKmH.toInt()}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryGreen
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "km/h",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }

                    // Duration display
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "RIDE TIME",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = GlassBorderDark, thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Stats breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricMiniItem(
                        label = "Distance",
                        value = String.format("%.2f km", liveState.distanceKm),
                        color = TextPrimary
                    )
                    MetricMiniItem(
                        label = "Fuel Used",
                        value = String.format("%.2f L", liveState.fuelUsedL),
                        color = SecondaryTeal
                    )
                    MetricMiniItem(
                        label = "Fuel Cost",
                        value = "${settings.currencySymbol}${liveState.fuelCost.toInt()}",
                        color = WarningYellow
                    )
                    MetricMiniItem(
                        label = "Net Profit",
                        value = "${settings.currencySymbol}${liveState.netProfit.toInt()}",
                        color = if (liveState.netProfit >= 0) ProfitGreen else ErrorRed
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stop Ride Button
                GlassButton(
                    text = "STOP RIDE & SAVE SUMMARY",
                    icon = Icons.Default.Stop,
                    onClick = onStopRideClick,
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MetricMiniItem(
    label: String,
    value: String,
    color: Color
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = TextSecondary
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}
