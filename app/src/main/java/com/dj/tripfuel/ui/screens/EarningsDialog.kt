package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.GlassTextField
import com.dj.tripfuel.ui.theme.*

@Composable
fun EarningsDialog(
    fuelCost: Float,
    distanceKm: Float,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSaveEarnings: (amount: Float, platform: String) -> Unit
) {
    val platforms = listOf("Rapido", "Porter", "Uber Moto", "Swiggy", "Zomato", "Custom")
    var selectedPlatform by remember { mutableStateOf("Zomato") }
    var amountInput by remember { mutableStateOf("350") }

    val amountEntered = amountInput.toFloatOrNull() ?: 0.0f
    val netProfit = amountEntered - fuelCost
    val profitPerKm = if (distanceKm > 0) netProfit / distanceKm else 0.0f
    val fuelPercentage = if (amountEntered > 0) (fuelCost / amountEntered) * 100f else 0.0f

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFF121820),
            borderColor = GlassBorderDark
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Today's Earnings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Add Platform Selectors
            Text(
                text = "SELECT PLATFORM",
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                platforms.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { platform ->
                            val isSelected = selectedPlatform == platform
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) PrimaryGreen.copy(alpha = 0.2f) else Color(0x1AFFFFFF)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) PrimaryGreen else GlassBorderDark,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPlatform = platform },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = platform,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryGreen else TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = "Total Earnings Amount ($currencySymbol)",
                placeholder = "0.0",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Instant Auto-Calculation Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1AFFFFFF))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Estimated Net Profit:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                        Text(
                            text = "$currencySymbol${String.format("%.2f", netProfit)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (netProfit >= 0) ProfitGreen else ErrorRed
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Profit / KM:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                        Text(
                            text = "$currencySymbol${String.format("%.2f", profitPerKm)} / km",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fuel Cost Share:",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                        Text(
                            text = "${String.format("%.1f", fuelPercentage)}%",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = WarningYellow
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GlassButton(
                text = "SAVE EARNINGS",
                icon = Icons.Default.Check,
                onClick = {
                    if (amountEntered > 0) {
                        onSaveEarnings(amountEntered, selectedPlatform)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
