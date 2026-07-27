package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.FuelLog
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.GlassTextField
import com.dj.tripfuel.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FuelLogScreen(
    repository: TripFuelRepository,
    settings: UserSettings
) {
    var fuelLogs by remember { mutableStateOf(repository.getFuelLogs()) }
    var showAddDialog by remember { mutableStateOf(false) }

    var amountPaidInput by remember { mutableStateOf("500") }
    var pricePerLInput by remember { mutableStateOf(settings.petrolPrice.toString()) }
    var stationNameInput by remember { mutableStateOf("HP Fuel Station") }
    var notesInput by remember { mutableStateOf("") }

    val avgPrice = remember(fuelLogs) {
        if (fuelLogs.isNotEmpty()) fuelLogs.map { it.petrolPrice }.average().toFloat() else settings.petrolPrice
    }

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
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Fuel Purchase Log",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Log petrol refills to keep track of average fuel expenditure.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Avg price banner
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AVERAGE PETROL PRICE", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text("${settings.currencySymbol}${String.format("%.2f", avgPrice)} / L", style = MaterialTheme.typography.titleLarge.copy(color = WarningYellow, fontWeight = FontWeight.Bold))
                    }
                    Icon(Icons.Default.LocalGasStation, null, tint = WarningYellow, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassButton(
                text = "+ ADD FUEL REFILL",
                icon = Icons.Default.Add,
                onClick = { showAddDialog = true },
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(fuelLogs) { log ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.stationName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                Text(log.dateFormatted, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${settings.currencySymbol}${log.amountPaid.toInt()}", style = MaterialTheme.typography.titleLarge.copy(color = WarningYellow, fontWeight = FontWeight.Bold))
                                Text("${String.format("%.2f", log.litres)} Litres", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF101720),
                    borderColor = GlassBorderDark
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Add Fuel Refill", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                        IconButton(onClick = { showAddDialog = false }) {
                            Icon(Icons.Default.Close, null, tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    GlassTextField(
                        value = amountPaidInput,
                        onValueChange = { amountPaidInput = it },
                        label = "Amount Paid (${settings.currencySymbol})",
                        placeholder = "500",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GlassTextField(
                        value = pricePerLInput,
                        onValueChange = { pricePerLInput = it },
                        label = "Price per Litre (${settings.currencySymbol})",
                        placeholder = "104.5",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GlassTextField(
                        value = stationNameInput,
                        onValueChange = { stationNameInput = it },
                        label = "Station Name",
                        placeholder = "Indian Oil"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    GlassButton(
                        text = "SAVE FUEL LOG",
                        icon = Icons.Default.Check,
                        onClick = {
                            val amount = amountPaidInput.toFloatOrNull() ?: 500f
                            val price = pricePerLInput.toFloatOrNull() ?: settings.petrolPrice
                            val litres = if (price > 0) amount / price else 0f
                            val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                            val newLog = FuelLog(
                                id = "fuel_${UUID.randomUUID().toString().take(6)}",
                                dateFormatted = df.format(Date()),
                                amountPaid = amount,
                                litres = litres,
                                petrolPrice = price,
                                stationName = stationNameInput.ifBlank { "Petrol Pump" },
                                notes = notesInput
                            )
                            repository.addFuelLog(newLog)
                            fuelLogs = repository.getFuelLogs()
                            showAddDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
