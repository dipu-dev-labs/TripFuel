package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.Bike
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.GlassTextField
import com.dj.tripfuel.ui.theme.*
import java.util.UUID

@Composable
fun BikeManagementScreen(
    repository: TripFuelRepository
) {
    var bikes by remember { mutableStateOf(repository.getBikes()) }
    var showAddBikeDialog by remember { mutableStateOf(false) }

    var newBikeName by remember { mutableStateOf("") }
    var newBikeMileage by remember { mutableStateOf("60") }
    var newFuelType by remember { mutableStateOf("Petrol") }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bike Garage",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Manage bikes and switch active vehicle for trip calculations.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            GlassButton(
                text = "+ ADD NEW BIKE",
                icon = Icons.Default.Add,
                onClick = { showAddBikeDialog = true },
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(bikes) { bike ->
                    BikeCard(
                        bike = bike,
                        onSetDefault = {
                            val updated = bikes.map { b ->
                                b.copy(isDefault = b.id == bike.id)
                            }
                            bikes = updated
                            repository.saveBikes(updated)

                            // Update user settings mileage
                            val currentSettings = repository.getSettings()
                            repository.saveSettings(
                                currentSettings.copy(
                                    bikeName = bike.nickname,
                                    bikeMileage = bike.mileage,
                                    activeBikeId = bike.id
                                )
                            )
                        }
                    )
                }
            }
        }

        // Add Bike Dialog Modal
        if (showAddBikeDialog) {
            Dialog(onDismissRequest = { showAddBikeDialog = false }) {
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
                        Text(
                            text = "Add New Bike",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        IconButton(onClick = { showAddBikeDialog = false }) {
                            Icon(Icons.Default.Close, null, tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassTextField(
                        value = newBikeName,
                        onValueChange = { newBikeName = it },
                        label = "Bike Nickname / Model",
                        placeholder = "e.g. Hero Splendor Plus"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassTextField(
                        value = newBikeMileage,
                        onValueChange = { newBikeMileage = it },
                        label = "Mileage (km/L or km/kWh)",
                        placeholder = "60",
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    GlassButton(
                        text = "SAVE TO GARAGE",
                        icon = Icons.Default.Check,
                        onClick = {
                            if (newBikeName.isNotBlank()) {
                                val mileage = newBikeMileage.toFloatOrNull() ?: 60f
                                val newBike = Bike(
                                    id = "bike_${UUID.randomUUID().toString().take(6)}",
                                    nickname = newBikeName,
                                    mileage = mileage,
                                    fuelType = newFuelType,
                                    isDefault = bikes.isEmpty()
                                )
                                repository.addBike(newBike)
                                bikes = repository.getBikes()
                                showAddBikeDialog = false
                                newBikeName = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun BikeCard(
    bike: Bike,
    onSetDefault: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (bike.isDefault) Color(0x2600E676) else CardSurfaceDark,
        borderColor = if (bike.isDefault) PrimaryGreen else GlassBorderDark
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (bike.isDefault) PrimaryGreen.copy(alpha = 0.2f) else Color(0x1AFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TwoWheeler, null, tint = if (bike.isDefault) PrimaryGreen else TextPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(bike.nickname, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text("${bike.mileage.toInt()} km/L • ${bike.fuelType}", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 13.sp))
                }
            }

            if (bike.isDefault) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("ACTIVE", style = MaterialTheme.typography.labelSmall.copy(color = BackgroundDark, fontWeight = FontWeight.Bold))
                }
            } else {
                TextButton(onClick = onSetDefault) {
                    Text("SET ACTIVE", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryTeal))
                }
            }
        }
    }
}
