package com.dj.tripfuel.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
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
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.theme.*

@Composable
fun ReportsScreen(
    repository: TripFuelRepository,
    settings: UserSettings
) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf("Weekly") }
    val periods = listOf("Daily", "Weekly", "Monthly")

    val rides = remember { repository.getRides() }
    val totalRides = rides.size
    val totalProfit = remember(rides) { rides.sumOf { it.netProfit.toDouble() }.toFloat() }
    val totalDistance = remember(rides) { rides.sumOf { it.distanceKm.toDouble() }.toFloat() }
    val totalFuel = remember(rides) { rides.sumOf { it.fuelCost.toDouble() }.toFloat() }

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

            Text(
                text = "Reports & Export",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Generate rider tax statements, income reports, and CSV exports.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Period Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { period ->
                    val isSelected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) PrimaryGreen.copy(alpha = 0.25f) else Color(0x1AFFFFFF))
                            .clickable { selectedPeriod = period },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryGreen else TextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Generated Report Card Preview
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$selectedPeriod Summary Report", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Icon(Icons.Default.Description, null, tint = PrimaryGreen)
                }

                Spacer(modifier = Modifier.height(14.dp))

                ReportRow("Rider Name:", settings.riderName)
                ReportRow("Vehicle Model:", settings.bikeName)
                ReportRow("Total Deliveries Recorded:", "$totalRides rides")
                ReportRow("Total Distance Covered:", "${String.format("%.1f", totalDistance)} km")
                ReportRow("Total Fuel Expenditure:", "${settings.currencySymbol}${String.format("%.2f", totalFuel)}")
                ReportRow("Total Net Profit:", "${settings.currencySymbol}${String.format("%.2f", totalProfit)}")
            }

            Spacer(modifier = Modifier.height(24.dp))

            GlassButton(
                text = "EXPORT CSV / PDF REPORT",
                icon = Icons.Default.Share,
                onClick = {
                    val reportText = """
                        📋 TRIPFUEL $selectedPeriod REPORT
                        ---------------------------------
                        Rider: ${settings.riderName}
                        Bike: ${settings.bikeName} (${settings.bikeMileage} km/L)
                        
                        Total Trips: $totalRides
                        Distance Covered: ${String.format("%.1f", totalDistance)} km
                        Fuel Expense: ${settings.currencySymbol}${String.format("%.2f", totalFuel)}
                        Net Profit: ${settings.currencySymbol}${String.format("%.2f", totalProfit)}
                        
                        Generated via TripFuel 2026 App
                    """.trimIndent()

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, reportText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Export Report"))
                },
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
    }
}
