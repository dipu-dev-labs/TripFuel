package com.dj.tripfuel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.MaintenanceItem
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.theme.*

@Composable
fun MaintenanceScreen(
    repository: TripFuelRepository
) {
    var maintenanceItems by remember { mutableStateOf(repository.getMaintenance()) }

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
                text = "Bike Maintenance",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Service reminders for oil change, chain, brakes, tyres, PUC & insurance.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(maintenanceItems) { item ->
                    MaintenanceCard(
                        item = item,
                        onServiceDone = {
                            val updated = maintenanceItems.map { m ->
                                if (m.id == item.id) m.copy(lastServiceKm = m.currentKm) else m
                            }
                            maintenanceItems = updated
                            repository.saveMaintenance(updated)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MaintenanceCard(
    item: MaintenanceItem,
    onServiceDone: () -> Unit
) {
    val isDue = item.isDue

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isDue) WarningYellow.copy(alpha = 0.15f) else CardSurfaceDark,
        borderColor = if (isDue) WarningYellow else GlassBorderDark
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDue) WarningYellow.copy(alpha = 0.2f) else PrimaryGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDue) Icons.Default.Warning else Icons.Default.Build,
                        contentDescription = null,
                        tint = if (isDue) WarningYellow else PrimaryGreen
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(item.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text("Due: ${item.dueDateFormatted}", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 12.sp))
                }
            }

            if (isDue) {
                GlassButton(
                    text = "DONE",
                    icon = Icons.Default.CheckCircle,
                    onClick = onServiceDone,
                    isPrimary = true,
                    modifier = Modifier.height(36.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("OK", style = MaterialTheme.typography.labelSmall.copy(color = PrimaryGreen, fontWeight = FontWeight.Bold))
                }
            }
        }

        if (item.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.notes, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary.copy(alpha = 0.8f), fontSize = 12.sp))
        }
    }
}
