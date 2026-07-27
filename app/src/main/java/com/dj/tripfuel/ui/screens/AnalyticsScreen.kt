package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.theme.*

@Composable
fun AnalyticsScreen(
    repository: TripFuelRepository,
    settings: UserSettings
) {
    val rides = remember { repository.getRides() }

    val totalProfit = remember(rides) { rides.sumOf { it.netProfit.toDouble() }.toFloat() }
    val totalDistance = remember(rides) { rides.sumOf { it.distanceKm.toDouble() }.toFloat() }
    val totalFuelCost = remember(rides) { rides.sumOf { it.fuelCost.toDouble() }.toFloat() }

    val avgDistance = remember(rides, totalDistance) { if (rides.isNotEmpty()) totalDistance / rides.size else 0f }
    val avgFuelCost = remember(rides, totalFuelCost) { if (rides.isNotEmpty()) totalFuelCost / rides.size else 0f }

    val weeklyData = remember { listOf(350f, 620f, 480f, 750f, 590f, 890f, 650f) }
    val daysLabel = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color(0xFF0A0F15))
                )
            )
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Analytics & Insights",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Track earnings trends, fuel costs, and profitability ratios.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Key Highlights Row
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x1F00E676),
                borderColor = PrimaryGreen
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("MOST PROFITABLE DAY", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryTeal))
                        Text("Saturday (₹890.00)", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    }
                    Icon(Icons.Default.Star, null, tint = WarningYellow, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekly Profit Bar Chart Canvas
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("WEEKLY PROFIT TREND", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Icon(Icons.Default.BarChart, null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxVal = 1000f
                        val barWidth = 28f
                        val spacing = (size.width - (weeklyData.size * barWidth)) / (weeklyData.size + 1)

                        weeklyData.forEachIndexed { index, value ->
                            val x = spacing + index * (barWidth + spacing)
                            val barHeight = (value / maxVal) * (size.height - 40f)
                            val y = size.height - barHeight - 20f

                            drawRoundRect(
                                color = PrimaryGreen,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(10f, 10f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    daysLabel.forEach { day ->
                        Text(text = day, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Monthly Earnings Line Chart Canvas
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MONTHLY EARNINGS VS FUEL COST", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Icon(Icons.Default.ShowChart, null, tint = SecondaryTeal, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val pathEarnings = Path()
                        pathEarnings.moveTo(20f, size.height - 40f)
                        pathEarnings.cubicTo(100f, size.height - 120f, 250f, size.height - 20f, size.width - 20f, size.height - 130f)

                        drawPath(pathEarnings, color = SecondaryTeal, style = Stroke(width = 6f))

                        val pathFuel = Path()
                        pathFuel.moveTo(20f, size.height - 20f)
                        pathFuel.cubicTo(100f, size.height - 40f, 250f, size.height - 30f, size.width - 20f, size.height - 50f)

                        drawPath(pathFuel, color = WarningYellow, style = Stroke(width = 4f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Averages Breakdown
            Row(modifier = Modifier.fillMaxWidth()) {
                GlassCard(modifier = Modifier.weight(1f)) {
                    Text("AVG FUEL COST / RIDE", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${settings.currencySymbol}${String.format("%.1f", avgFuelCost)}", style = MaterialTheme.typography.titleLarge.copy(color = WarningYellow, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.width(12.dp))
                GlassCard(modifier = Modifier.weight(1f)) {
                    Text("AVG DISTANCE / RIDE", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${String.format("%.1f", avgDistance)} km", style = MaterialTheme.typography.titleLarge.copy(color = PrimaryGreen, fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
