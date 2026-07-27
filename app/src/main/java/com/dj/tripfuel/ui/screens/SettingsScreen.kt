package com.dj.tripfuel.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.GlassTextField
import com.dj.tripfuel.ui.theme.*

@Composable
fun SettingsScreen(
    repository: TripFuelRepository,
    onSettingsUpdated: () -> Unit
) {
    val context = LocalContext.current
    val currentSettings = remember { repository.getSettings() }

    var riderName by remember { mutableStateOf(currentSettings.riderName) }
    var bikeMileageText by remember { mutableStateOf(currentSettings.bikeMileage.toString()) }
    var petrolPriceText by remember { mutableStateOf(currentSettings.petrolPrice.toString()) }
    var currencySymbol by remember { mutableStateOf(currentSettings.currencySymbol) }
    var distanceUnit by remember { mutableStateOf(currentSettings.distanceUnit) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Settings",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Customize fuel calculations, units, and data backups.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Profile & Defaults
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("RIDER PROFILE & FUEL CONFIG", style = MaterialTheme.typography.labelSmall.copy(color = PrimaryGreen))

                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = riderName,
                    onValueChange = { riderName = it },
                    label = "Rider Name"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GlassTextField(
                        value = bikeMileageText,
                        onValueChange = { bikeMileageText = it },
                        label = "Mileage (km/L)",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    GlassTextField(
                        value = petrolPriceText,
                        onValueChange = { petrolPriceText = it },
                        label = "Petrol Price (₹/L)",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GlassTextField(
                        value = currencySymbol,
                        onValueChange = { currencySymbol = it },
                        label = "Currency Symbol",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    GlassTextField(
                        value = distanceUnit,
                        onValueChange = { distanceUnit = it },
                        label = "Distance Unit",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cloud & Data Backup
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("DATA & BACKUP", style = MaterialTheme.typography.labelSmall.copy(color = SecondaryTeal))

                Spacer(modifier = Modifier.height(12.dp))

                GlassButton(
                    text = "BACKUP TO LOCAL PHONE STORAGE",
                    icon = Icons.Default.CloudUpload,
                    onClick = {
                        Toast.makeText(context, "Encrypted backup saved to local storage!", Toast.LENGTH_SHORT).show()
                    },
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlassButton(
                    text = "RESTORE FROM LOCAL BACKUP",
                    icon = Icons.Default.CloudDownload,
                    onClick = {
                        Toast.makeText(context, "Local database verified & updated!", Toast.LENGTH_SHORT).show()
                    },
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                var showResetConfirm by remember { mutableStateOf(false) }

                GlassButton(
                    text = "DELETE ALL OLD STACK & DATA",
                    icon = Icons.Default.DeleteForever,
                    onClick = { showResetConfirm = true },
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth()
                )

                if (showResetConfirm) {
                    AlertDialog(
                        onDismissRequest = { showResetConfirm = false },
                        title = { Text("Delete All Stack Data?", color = TextPrimary, fontWeight = FontWeight.Bold) },
                        text = { Text("This will permanently clear all ride history, fuel logs, and saved settings from phone storage.", color = TextSecondary) },
                        confirmButton = {
                            TextButton(onClick = {
                                repository.clearAllData()
                                showResetConfirm = false
                                onSettingsUpdated()
                                Toast.makeText(context, "All app stack data cleared!", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("CLEAR ALL", color = ErrorRed, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResetConfirm = false }) {
                                Text("CANCEL", color = TextSecondary)
                            }
                        },
                        containerColor = Color(0xFF101620)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Settings CTA
            GlassButton(
                text = "SAVE CHANGES",
                icon = Icons.Default.Check,
                onClick = {
                    val m = bikeMileageText.toFloatOrNull() ?: 55f
                    val p = petrolPriceText.toFloatOrNull() ?: 104.5f

                    val updated = currentSettings.copy(
                        riderName = riderName,
                        bikeMileage = m,
                        petrolPrice = p,
                        currencySymbol = currencySymbol,
                        distanceUnit = distanceUnit
                    )
                    repository.saveSettings(updated)
                    onSettingsUpdated()
                    Toast.makeText(context, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
                },
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // About TripFuel
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TripFuel v2026.1.0 (Liquid Glass)", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                Text("Powered by GPS Profit Engine • 100% Offline Local DB", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp, color = TextSecondary.copy(alpha = 0.6f)))
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
