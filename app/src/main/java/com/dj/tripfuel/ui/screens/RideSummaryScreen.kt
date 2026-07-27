package com.dj.tripfuel.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.model.RideSession
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.theme.*

@Composable
fun RideSummaryScreen(
    ride: RideSession,
    settings: UserSettings,
    onDoneClick: () -> Unit
) {
    val context = LocalContext.current

    val durationText = remember(ride.durationSec) {
        val mins = ride.durationSec / 60
        val secs = ride.durationSec % 60
        "${mins}m ${secs}s"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color(0xFF0C131B))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ride Summary",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = ride.dateFormatted,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = ride.platform,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Net Profit Banner Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x2600E676),
                borderColor = PrimaryGreen
            ) {
                Text(
                    text = "TOTAL NET PROFIT",
                    style = MaterialTheme.typography.labelSmall.copy(color = SecondaryTeal)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${settings.currencySymbol}${String.format("%.2f", ride.netProfit)}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = ProfitGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Route Preview Graphic Box
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TRIP ROUTE TIMELINE",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x1AFFFFFF))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path()
                        path.moveTo(60f, 150f)
                        path.cubicTo(180f, 40f, 320f, 220f, size.width - 60f, 80f)

                        drawPath(
                            path = path,
                            color = PrimaryGreen,
                            style = Stroke(width = 6f)
                        )
                        drawCircle(color = PrimaryGreen, radius = 12f, center = Offset(60f, 150f))
                        drawCircle(color = SecondaryTeal, radius = 12f, center = Offset(size.width - 60f, 80f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Performance Breakdown Grid
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PERFORMANCE METRICS",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
                Spacer(modifier = Modifier.height(12.dp))

                SummaryRowItem(label = "Total Distance", value = "${String.format("%.2f", ride.distanceKm)} km")
                SummaryRowItem(label = "Ride Duration", value = durationText)
                SummaryRowItem(label = "Average Speed", value = "${String.format("%.1f", ride.avgSpeedKmH)} km/h")
                SummaryRowItem(label = "Fuel Consumed", value = "${String.format("%.2f", ride.fuelUsedL)} L")
                SummaryRowItem(label = "Fuel Expense", value = "${settings.currencySymbol}${String.format("%.2f", ride.fuelCost)}")
                SummaryRowItem(label = "Total Earnings", value = "${settings.currencySymbol}${String.format("%.2f", ride.earnings)}")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons (Share & Done)
            Row(modifier = Modifier.fillMaxWidth()) {
                GlassButton(
                    text = "SHARE SUMMARY",
                    icon = Icons.Default.Share,
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "🚀 TripFuel Ride Summary\n\nDistance: ${ride.distanceKm} km\nDuration: $durationText\nFuel Cost: ${settings.currencySymbol}${ride.fuelCost}\nNet Profit: ${settings.currencySymbol}${ride.netProfit}\n\nTracked with TripFuel App!"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Ride Summary"))
                    },
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                GlassButton(
                    text = "DONE",
                    icon = Icons.Default.Check,
                    onClick = onDoneClick,
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}
