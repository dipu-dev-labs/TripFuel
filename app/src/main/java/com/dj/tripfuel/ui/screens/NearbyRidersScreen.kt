package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.model.NearbyRiderModel
import com.dj.tripfuel.tracking.NearbyRiderRadarManager
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.OpenStreetMapRadarView
import com.dj.tripfuel.ui.theme.*

@Composable
fun NearbyRidersScreen(
    radarManager: NearbyRiderRadarManager,
    onBackClick: () -> Unit
) {
    val nearbyRiders by radarManager.nearbyRiders.collectAsState()
    val isRadarEnabled by radarManager.isRadarEnabled.collectAsState()

    var selectedPlatformFilter by remember { mutableStateOf("All") }
    val platforms = listOf("All", "Zomato", "Rapido", "Swiggy", "Uber Moto")

    val filteredRiders = remember(nearbyRiders, selectedPlatformFilter) {
        if (selectedPlatformFilter == "All") nearbyRiders
        else nearbyRiders.filter { it.platform == selectedPlatformFilter }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "OpenStreetMap Radar",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Text(
                            text = "Nearby Riders & Proximity Alerts",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = TextSecondary)
                        )
                    }
                }

                Switch(
                    checked = isRadarEnabled,
                    onCheckedChange = { radarManager.toggleRadar(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BackgroundDark,
                        checkedTrackColor = PrimaryGreen
                    )
                )
            }

            // OpenStreetMap Canvas Radar View Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                OpenStreetMapRadarView(
                    nearbyRiders = filteredRiders,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC0B0F14))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Radar, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${filteredRiders.size} RIDERS IN RANGE",
                            style = MaterialTheme.typography.labelSmall.copy(color = PrimaryGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Platform Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    platforms.forEach { platform ->
                        val isSelected = selectedPlatformFilter == platform
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryGreen.copy(alpha = 0.25f) else Color(0x1AFFFFFF))
                                .clickable { selectedPlatformFilter = platform },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = platform,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PrimaryGreen else TextSecondary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Proximity Alert Banner info
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x1A00E676),
                    borderColor = PrimaryGreen
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Proximity alerts active: Notification rings when a rider crosses within 100m.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = TextPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nearby Rider List
                Text(
                    text = "ACTIVE NEARBY RIDERS",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredRiders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No riders detected in 1km range right now.", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredRiders) { rider ->
                            NearbyRiderCard(rider = rider)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyRiderCard(rider: NearbyRiderModel) {
    val badgeColor = when (rider.platform) {
        "Zomato" -> Color(0xFFFF5252)
        "Rapido" -> WarningYellow
        "Swiggy" -> Color(0xFFFF9800)
        "Uber Moto" -> SecondaryTeal
        else -> PrimaryGreen
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = badgeColor, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(rider.riderName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text(rider.platform, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = badgeColor, fontWeight = FontWeight.SemiBold))
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1AFFFFFF))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${rider.distanceMeters.toInt()}m away",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
