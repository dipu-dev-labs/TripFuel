package com.dj.tripfuel.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.RideSession
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.theme.*

@Composable
fun HistoryScreen(
    repository: TripFuelRepository,
    settings: UserSettings
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("Daily") }
    val filters = listOf("Daily", "Weekly", "Monthly", "Yearly")
    var rides by remember { mutableStateOf(repository.getRides()) }

    var selectedRideDetail by remember { mutableStateOf<RideSession?>(null) }
    var showClearAllConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color(0xFF090E14))
                )
            )
            .padding(horizontal = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header with Title & Clear All Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ride History",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Detailed logs of all your past delivery trips.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }

                if (rides.isNotEmpty()) {
                    TextButton(onClick = { showClearAllConfirm = true }) {
                        Text(
                            text = "CLEAR ALL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ErrorRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) PrimaryGreen.copy(alpha = 0.25f) else Color(0x1AFFFFFF)
                            )
                            .clickable { selectedFilter = filter },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryGreen else TextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (rides.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No rides recorded yet.",
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Start a ride from Home screen to track your first trip!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = TextSecondary.copy(alpha = 0.6f))
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(rides) { ride ->
                        RideHistoryCard(
                            ride = ride,
                            currencySymbol = settings.currencySymbol,
                            onClick = { selectedRideDetail = ride }
                        )
                    }
                }
            }
        }

        // Detailed Modal Dialog on Tap
        selectedRideDetail?.let { ride ->
            Dialog(onDismissRequest = { selectedRideDetail = null }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF101620),
                    borderColor = GlassBorderDark
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ride Details",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        IconButton(onClick = { selectedRideDetail = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = ride.dateFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Platform", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(text = ride.platform, style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Net Profit", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text(
                                text = "${settings.currencySymbol}${String.format("%.2f", ride.netProfit)}",
                                style = MaterialTheme.typography.titleMedium.copy(color = ProfitGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = GlassBorderDark)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Distance:", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                        Text("${String.format("%.2f", ride.distanceKm)} km", style = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fuel Cost:", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                        Text("${settings.currencySymbol}${String.format("%.2f", ride.fuelCost)}", style = MaterialTheme.typography.bodyLarge.copy(color = WarningYellow))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Earnings:", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                        Text("${settings.currencySymbol}${String.format("%.2f", ride.earnings)}", style = MaterialTheme.typography.bodyLarge.copy(color = SecondaryTeal))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Delete Individual Ride Button
                    GlassButton(
                        text = "DELETE THIS RIDE",
                        icon = Icons.Default.Delete,
                        onClick = {
                            repository.deleteRide(ride.id)
                            rides = repository.getRides()
                            selectedRideDetail = null
                            Toast.makeText(context, "Ride deleted successfully!", Toast.LENGTH_SHORT).show()
                        },
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Clear All Confirmation Modal Dialog
        if (showClearAllConfirm) {
            AlertDialog(
                onDismissRequest = { showClearAllConfirm = false },
                title = {
                    Text(
                        text = "Clear All Ride History?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete all old ride logs and stack history? This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            repository.clearAllRides()
                            rides = repository.getRides()
                            showClearAllConfirm = false
                            Toast.makeText(context, "All ride history cleared!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("DELETE ALL", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllConfirm = false }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                },
                containerColor = Color(0xFF101620)
            )
        }
    }
}

@Composable
private fun RideHistoryCard(
    ride: RideSession,
    currencySymbol: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = ride.platform,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = ride.dateFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+$currencySymbol${ride.netProfit.toInt()}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ProfitGreen
                    )
                )
                Text(
                    text = "Net Profit",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Speed, null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${String.format("%.1f", ride.distanceKm)} km", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = TextPrimary))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalGasStation, null, tint = WarningYellow, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("$currencySymbol${ride.fuelCost.toInt()}", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, color = TextPrimary))
            }
        }
    }
}
