package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.tracking.LiveRideState
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.OpenStreetMapComponent
import com.dj.tripfuel.ui.theme.*

@Composable
fun LiveTrackingScreen(
    settings: UserSettings,
    liveState: LiveRideState,
    onStopRideClick: () -> Unit,
    onToggleBatterySaver: () -> Unit,
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

    val currentLat = liveState.routePoints.lastOrNull()?.latitude ?: 19.0760
    val currentLng = liveState.routePoints.lastOrNull()?.longitude ?: 72.8777

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // --- REAL OPENSTREETMAP VECTOR MAP VIEW ---
        OpenStreetMapComponent(
            currentLat = currentLat,
            currentLng = currentLng,
            routePoints = liveState.routePoints,
            modifier = Modifier.fillMaxSize()
        )

        // --- TOP HUD GLASS BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassCard(
                modifier = Modifier.weight(1f),
                backgroundColor = Color(0xCC0B0F14),
                borderColor = PrimaryGreen
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "REAL GPS TRACKING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = durationText,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    // Battery saver toggle
                    IconButton(
                        onClick = onToggleBatterySaver,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (liveState.isBatterySaver) PrimaryGreen.copy(alpha = 0.25f) else Color(0x1AFFFFFF))
                    ) {
                        Icon(
                            imageVector = if (liveState.isBatterySaver) Icons.Default.BatterySaver else Icons.Default.BatteryStd,
                            contentDescription = "Battery Saver",
                            tint = if (liveState.isBatterySaver) PrimaryGreen else TextPrimary
                        )
                    }
                }
            }
        }

        // --- BOTTOM LIVE HUD PANEL ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xDC0B0F14),
                borderColor = PrimaryGreen
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SPEED", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
                        Text(
                            text = "${liveState.currentSpeedKmH.toInt()}",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 32.sp)
                        )
                        Text("km/h", style = MaterialTheme.typography.labelSmall.copy(color = PrimaryGreen, fontSize = 10.sp))
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0x33FFFFFF))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DISTANCE", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
                        Text(
                            text = String.format("%.2f", liveState.distanceKm),
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 32.sp)
                        )
                        Text("km", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryTeal, fontSize = 10.sp))
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(40.dp)
                            .background(Color(0x33FFFFFF))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FUEL COST", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp))
                        Text(
                            text = "${settings.currencySymbol}${liveState.fuelCost.toInt()}",
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black, color = WarningYellow, fontSize = 32.sp)
                        )
                        Text("${String.format("%.2f", liveState.fuelUsedL)} L", style = MaterialTheme.typography.labelSmall.copy(color = WarningYellow, fontSize = 10.sp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // STOP RIDE BUTTON
                GlassButton(
                    text = "FINISH RIDE",
                    icon = Icons.Default.Stop,
                    onClick = onStopRideClick,
                    isPrimary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}
