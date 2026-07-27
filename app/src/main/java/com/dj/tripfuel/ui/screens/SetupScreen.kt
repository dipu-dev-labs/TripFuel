package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.UserSettings
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.components.GlassTextField
import com.dj.tripfuel.ui.theme.*

@Composable
fun SetupScreen(
    repository: TripFuelRepository,
    onSetupComplete: () -> Unit
) {
    val existingSettings = remember { repository.getSettings() }

    var riderName by remember { mutableStateOf(existingSettings.riderName) }
    var bikeName by remember { mutableStateOf(existingSettings.bikeName) }
    var mileageText by remember { mutableStateOf(existingSettings.bikeMileage.toString()) }
    var petrolPriceText by remember { mutableStateOf(existingSettings.petrolPrice.toString()) }
    var currencySymbol by remember { mutableStateOf(existingSettings.currencySymbol) }
    var distanceUnit by remember { mutableStateOf(existingSettings.distanceUnit) }

    var gpsAllowed by remember { mutableStateOf(true) }
    var bgLocationAllowed by remember { mutableStateOf(true) }
    var notifAllowed by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color(0xFF0C141C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "First Time Setup",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Configure your bike & fuel defaults for accurate tracking.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Rider & Bike Info
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RIDER & BIKE PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryTeal,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                GlassTextField(
                    value = riderName,
                    onValueChange = { riderName = it },
                    label = "Rider Name",
                    placeholder = "Dipu"
                )

                Spacer(modifier = Modifier.height(12.dp))

                GlassTextField(
                    value = bikeName,
                    onValueChange = { bikeName = it },
                    label = "Bike Name / Model",
                    placeholder = "Honda Shine 125"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mileage & Petrol Price
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "FUEL & ECONOMY DEFAULTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GlassTextField(
                        value = mileageText,
                        onValueChange = { mileageText = it },
                        label = "Mileage (km/L)",
                        placeholder = "55",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    GlassTextField(
                        value = petrolPriceText,
                        onValueChange = { petrolPriceText = it },
                        label = "Petrol Price (₹/L)",
                        placeholder = "104.5",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Permissions setup
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "APP PERMISSIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                PermissionRow(
                    title = "GPS Location",
                    subtitle = "Required for live distance tracking",
                    checked = gpsAllowed,
                    onCheckedChange = { gpsAllowed = it }
                )

                PermissionRow(
                    title = "Background Location",
                    subtitle = "Tracks distance when phone screen is locked",
                    checked = bgLocationAllowed,
                    onCheckedChange = { bgLocationAllowed = it }
                )

                PermissionRow(
                    title = "Daily Notifications",
                    subtitle = "Morning & night shift earnings reminders",
                    checked = notifAllowed,
                    onCheckedChange = { notifAllowed = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            GlassButton(
                text = "SAVE & START USING TRIPFUEL",
                icon = Icons.Default.Check,
                onClick = {
                    val mileage = mileageText.toFloatOrNull() ?: 55.0f
                    val price = petrolPriceText.toFloatOrNull() ?: 104.5f

                    val updatedSettings = UserSettings(
                        riderName = riderName.ifBlank { "Dipu" },
                        bikeName = bikeName.ifBlank { "Honda Shine" },
                        bikeMileage = mileage,
                        petrolPrice = price,
                        currencySymbol = currencySymbol.ifBlank { "₹" },
                        distanceUnit = distanceUnit.ifBlank { "km" },
                        isDarkMode = true,
                        isSetupCompleted = true,
                        activeBikeId = "bike_1"
                    )

                    repository.saveSettings(updatedSettings)
                    onSetupComplete()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BackgroundDark,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Color(0x33FFFFFF)
            )
        )
    }
}
