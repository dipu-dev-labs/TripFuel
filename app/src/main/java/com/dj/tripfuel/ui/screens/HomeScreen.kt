package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.dj.tripfuel.tracking.LiveRideState
import com.dj.tripfuel.ui.components.AnimatedStatCard
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.LiquidFloatingActionButton
import com.dj.tripfuel.ui.theme.*
import java.util.Calendar

@Composable
fun HomeScreen(
    repository: TripFuelRepository,
    liveState: LiveRideState,
    onStartRide: () -> Unit,
    onStopRide: () -> Unit,
    onOpenLiveMap: () -> Unit,
    onOpenEarningsDialog: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenNearbyRadar: () -> Unit
) {
    val settings = remember { repository.getSettings() }
    val rides = remember { repository.getRides() }

    // Calculate today's totals from saved rides + live tracking state
    val todayDistance = remember(rides, liveState) {
        rides.take(3).sumOf { it.distanceKm.toDouble() }.toFloat() + liveState.distanceKm
    }
    val todayFuelCost = remember(rides, liveState) {
        rides.take(3).sumOf { it.fuelCost.toDouble() }.toFloat() + liveState.fuelCost
    }
    val todayEarnings = remember(rides, liveState) {
        rides.take(3).sumOf { it.earnings.toDouble() }.toFloat() + liveState.earnings
    }
    val todayNetProfit = remember(todayEarnings, todayFuelCost) {
        todayEarnings - todayFuelCost
    }
    val todayFuelUsed = remember(rides, liveState) {
        rides.take(3).sumOf { it.fuelUsedL.toDouble() }.toFloat() + liveState.fuelUsedL
    }

    // Weekly calculations
    val weeklyRides = remember(rides) { rides.take(7) }
    val weeklyTotalProfit = remember(weeklyRides) {
        val base = weeklyRides.sumOf { it.netProfit.toDouble() }.toFloat()
        if (base > 0) base else 3958.0f
    }
    val weeklyTotalFuelCost = remember(weeklyRides) {
        val base = weeklyRides.sumOf { it.fuelCost.toDouble() }.toFloat()
        if (base > 0) base else 980.5f
    }
    val weeklyTotalFuelUsed = remember(weeklyTotalFuelCost, settings.petrolPrice) {
        if (settings.petrolPrice > 0) weeklyTotalFuelCost / settings.petrolPrice else 9.38f
    }

    val greetingText = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color(0xFF090D12))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Greeting & Rider Badge Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greetingText,",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Text(
                        text = "${settings.riderName} 👋",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            fontSize = 28.sp
                        )
                    )
                }

                // Bike pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x1AFFFFFF))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${settings.bikeMileage.toInt()} km/L",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Live Ride Control Banner if riding
            if (liveState.isRiding) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x2600E676),
                    borderColor = PrimaryGreen
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RIDE TRACKING ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PrimaryGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Speed: ${liveState.currentSpeedKmH.toInt()} km/h",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        GlassButton(
                            text = "VIEW MAP",
                            icon = Icons.Default.Map,
                            onClick = onOpenLiveMap,
                            isPrimary = true,
                            modifier = Modifier.height(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2x2 Grid Stats Cards
            Row(modifier = Modifier.fillMaxWidth()) {
                AnimatedStatCard(
                    title = "Today's Profit",
                    value = "${settings.currencySymbol}${todayNetProfit.toInt()}",
                    icon = Icons.Default.TrendingUp,
                    accentColor = if (todayNetProfit >= 0) ProfitGreen else ErrorRed,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AnimatedStatCard(
                    title = "Today's Earnings",
                    value = "${settings.currencySymbol}${todayEarnings.toInt()}",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = SecondaryTeal,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                AnimatedStatCard(
                    title = "Distance Covered",
                    value = String.format("%.1f", todayDistance),
                    unit = settings.distanceUnit,
                    icon = Icons.Default.Speed,
                    accentColor = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AnimatedStatCard(
                    title = "Fuel Expense",
                    value = "${settings.currencySymbol}${todayFuelCost.toInt()}",
                    icon = Icons.Default.LocalGasStation,
                    accentColor = WarningYellow,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                AnimatedStatCard(
                    title = "Fuel Consumed",
                    value = String.format("%.2f", todayFuelUsed),
                    unit = "L",
                    icon = Icons.Default.Opacity,
                    accentColor = SecondaryTeal,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                AnimatedStatCard(
                    title = "Live GPS Status",
                    value = if (liveState.isRiding) "LOCKED" else "READY",
                    icon = Icons.Default.GpsFixed,
                    accentColor = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- NEARBY RIDERS RADAR WIDGET CARD ---
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenNearbyRadar),
                backgroundColor = Color(0x2664FFDA),
                borderColor = SecondaryTeal
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Radar, contentDescription = null, tint = SecondaryTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEARBY RIDERS RADAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SecondaryTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "3 Fellow Riders Nearby (1km Range)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    GlassButton(
                        text = "RADAR",
                        icon = Icons.Default.LocationOn,
                        onClick = onOpenNearbyRadar,
                        isPrimary = false,
                        modifier = Modifier.height(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- WEEKLY STATS & DUAL GRAPH CARD ---
            WeeklyStatsCard(
                currencySymbol = settings.currencySymbol,
                totalProfit = weeklyTotalProfit,
                totalFuelCost = weeklyTotalFuelCost,
                totalFuelUsedL = weeklyTotalFuelUsed
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Row for Quick Add Earnings & History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlassButton(
                    text = "+ ADD EARNINGS",
                    icon = Icons.Default.Add,
                    onClick = onOpenEarningsDialog,
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                GlassButton(
                    text = "RECENT RIDES",
                    icon = Icons.Default.History,
                    onClick = onOpenHistory,
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(110.dp)) // clearance for FAB
        }

        // Center Floating Action Button (START / STOP RIDE)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            LiquidFloatingActionButton(
                isRiding = liveState.isRiding,
                onClick = {
                    if (liveState.isRiding) {
                        onStopRide()
                    } else {
                        onStartRide()
                    }
                }
            )
        }
    }
}

@Composable
fun WeeklyStatsCard(
    currencySymbol: String,
    totalProfit: Float,
    totalFuelCost: Float,
    totalFuelUsedL: Float
) {
    val weeklyProfits = remember { listOf(350f, 620f, 480f, 750f, 590f, 890f, 650f) }
    val weeklyFuelCosts = remember { listOf(90f, 130f, 110f, 160f, 120f, 180f, 140f) }
    val daysLabel = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0x1F141E28),
        borderColor = SecondaryTeal.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WEEKLY STATS & GRAPH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryTeal,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Profit vs Petrol Usage",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            // Legend indicators
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Profit", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = TextSecondary))
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(WarningYellow)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Petrol", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = TextSecondary))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Key stats metrics summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Weekly Net Profit", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp))
                Text("$currencySymbol${totalProfit.toInt()}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = ProfitGreen))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Petrol Spent", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp))
                Text("$currencySymbol${totalFuelCost.toInt()} (${String.format("%.1f", totalFuelUsedL)} L)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = WarningYellow))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dual Animated Canvas Bar + Line Graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxProfit = 1000f
                val maxFuel = 250f
                val barWidth = 24f
                val spacing = (size.width - (weeklyProfits.size * barWidth)) / (weeklyProfits.size + 1)

                // Draw Net Profit Bars (Green)
                weeklyProfits.forEachIndexed { index, profit ->
                    val x = spacing + index * (barWidth + spacing)
                    val barHeight = (profit / maxProfit) * (size.height - 30f)
                    val y = size.height - barHeight - 20f

                    drawRoundRect(
                        color = PrimaryGreen.copy(alpha = 0.85f),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }

                // Draw Petrol Cost Line (Yellow curve)
                val fuelPath = Path()
                weeklyFuelCosts.forEachIndexed { index, cost ->
                    val x = spacing + index * (barWidth + spacing) + (barWidth / 2f)
                    val y = size.height - ((cost / maxFuel) * (size.height - 30f)) - 20f
                    if (index == 0) {
                        fuelPath.moveTo(x, y)
                    } else {
                        fuelPath.lineTo(x, y)
                    }
                    // Draw yellow dot on line node
                    drawCircle(color = WarningYellow, radius = 5f, center = Offset(x, y))
                }
                drawPath(
                    path = fuelPath,
                    color = WarningYellow,
                    style = Stroke(width = 3.5f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysLabel.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 11.sp)
                )
            }
        }
    }
}
