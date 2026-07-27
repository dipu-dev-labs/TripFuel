package com.dj.tripfuel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.data.TripFuelRepository
import com.dj.tripfuel.model.RideSession
import com.dj.tripfuel.tracking.RideTrackingManager
import com.dj.tripfuel.ui.screens.*
import com.dj.tripfuel.ui.theme.BackgroundDark
import com.dj.tripfuel.ui.theme.GlassBorderDark
import com.dj.tripfuel.ui.theme.PrimaryGreen
import com.dj.tripfuel.ui.theme.TextPrimary
import com.dj.tripfuel.ui.theme.TextSecondary
import com.dj.tripfuel.ui.theme.TripFuelTheme

enum class AppDestination {
    SPLASH,
    ONBOARDING,
    SETUP,
    MAIN_SHELL,
    LIVE_MAP,
    RIDE_SUMMARY
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: TripFuelRepository
    private val trackingManager = RideTrackingManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = TripFuelRepository(applicationContext)
        com.dj.tripfuel.tracking.TripFuelServiceBridge.liveStateFlow = trackingManager.liveState

        setContent {
            TripFuelTheme {
                val settings = remember { mutableStateOf(repository.getSettings()) }
                val liveRideState by trackingManager.liveState.collectAsState()

                // Determine initial destination
                var currentDestination by remember {
                    mutableStateOf(AppDestination.SPLASH)
                }

                var selectedTab by remember { mutableIntStateOf(0) }
                var showEarningsDialog by remember { mutableStateOf(false) }
                var completedRideSummary by remember { mutableStateOf<RideSession?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentDestination) {
                            AppDestination.SPLASH -> {
                                SplashScreen(
                                    onSplashFinished = {
                                        if (!settings.value.isSetupCompleted) {
                                            currentDestination = AppDestination.ONBOARDING
                                        } else {
                                            currentDestination = AppDestination.MAIN_SHELL
                                        }
                                    }
                                )
                            }

                            AppDestination.ONBOARDING -> {
                                OnboardingScreen(
                                    onOnboardingFinished = {
                                        currentDestination = AppDestination.SETUP
                                    }
                                )
                            }

                            AppDestination.SETUP -> {
                                SetupScreen(
                                    repository = repository,
                                    onSetupComplete = {
                                        settings.value = repository.getSettings()
                                        currentDestination = AppDestination.MAIN_SHELL
                                    }
                                )
                            }

                            AppDestination.MAIN_SHELL -> {
                                Scaffold(
                                    bottomBar = {
                                        FrostedGlassBottomNav(
                                            selectedTab = selectedTab,
                                            onTabSelected = { selectedTab = it }
                                        )
                                    },
                                    containerColor = BackgroundDark
                                ) { innerPadding ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(innerPadding)
                                    ) {
                                        when (selectedTab) {
                                            0 -> HomeScreen(
                                                repository = repository,
                                                liveState = liveRideState,
                                                onStartRide = {
                                                    trackingManager.startRide(
                                                        mileage = settings.value.bikeMileage,
                                                        petrolPrice = settings.value.petrolPrice
                                                    )
                                                    com.dj.tripfuel.tracking.TripFuelForegroundService.startService(applicationContext)
                                                    currentDestination = AppDestination.LIVE_MAP
                                                },
                                                onStopRide = {
                                                    val savedRide = trackingManager.stopRide()
                                                    com.dj.tripfuel.tracking.TripFuelForegroundService.stopService(applicationContext)
                                                    repository.addRide(savedRide)
                                                    completedRideSummary = savedRide
                                                    currentDestination = AppDestination.RIDE_SUMMARY
                                                },
                                                onOpenLiveMap = {
                                                    currentDestination = AppDestination.LIVE_MAP
                                                },
                                                onOpenEarningsDialog = {
                                                    showEarningsDialog = true
                                                },
                                                onOpenHistory = {
                                                    selectedTab = 1
                                                }
                                            )
                                            1 -> HistoryScreen(
                                                repository = repository,
                                                settings = settings.value
                                            )
                                            2 -> AnalyticsScreen(
                                                repository = repository,
                                                settings = settings.value
                                            )
                                            3 -> BikeManagementScreen(
                                                repository = repository
                                            )
                                            4 -> SettingsScreen(
                                                repository = repository,
                                                onSettingsUpdated = {
                                                    settings.value = repository.getSettings()
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            AppDestination.LIVE_MAP -> {
                                LiveTrackingScreen(
                                    settings = settings.value,
                                    liveState = liveRideState,
                                    onStopRideClick = {
                                        val savedRide = trackingManager.stopRide()
                                        com.dj.tripfuel.tracking.TripFuelForegroundService.stopService(applicationContext)
                                        repository.addRide(savedRide)
                                        completedRideSummary = savedRide
                                        currentDestination = AppDestination.RIDE_SUMMARY
                                    },
                                    onToggleBatterySaver = {
                                        trackingManager.toggleBatterySaver()
                                    },
                                    onToggleSimulationMode = {
                                        trackingManager.toggleSimulationMode()
                                    },
                                    onBackClick = {
                                        currentDestination = AppDestination.MAIN_SHELL
                                    }
                                )
                            }

                            AppDestination.RIDE_SUMMARY -> {
                                completedRideSummary?.let { ride ->
                                    RideSummaryScreen(
                                        ride = ride,
                                        settings = settings.value,
                                        onDoneClick = {
                                            currentDestination = AppDestination.MAIN_SHELL
                                        }
                                    )
                                }
                            }
                        }

                        // Dialog overlay for Add Earnings
                        if (showEarningsDialog) {
                            EarningsDialog(
                                fuelCost = liveRideState.fuelCost,
                                distanceKm = liveRideState.distanceKm,
                                currencySymbol = settings.value.currencySymbol,
                                onDismiss = { showEarningsDialog = false },
                                onSaveEarnings = { amount, platform ->
                                    if (liveRideState.isRiding) {
                                        trackingManager.addEarnings(amount, platform)
                                    } else {
                                        // Save standard ride log
                                        val newRide = RideSession(
                                            id = "ride_${System.currentTimeMillis()}",
                                            dateTimestamp = System.currentTimeMillis(),
                                            dateFormatted = "Today",
                                            distanceKm = 25.0f,
                                            durationSec = 3600,
                                            avgSpeedKmH = 25.0f,
                                            fuelUsedL = 25.0f / settings.value.bikeMileage,
                                            fuelCost = (25.0f / settings.value.bikeMileage) * settings.value.petrolPrice,
                                            earnings = amount,
                                            platform = platform,
                                            netProfit = amount - ((25.0f / settings.value.bikeMileage) * settings.value.petrolPrice)
                                        )
                                        repository.addRide(newRide)
                                    }
                                    showEarningsDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FrostedGlassBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val navItems = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("History", Icons.Default.History),
        NavItem("Analytics", Icons.Default.BarChart),
        NavItem("Garage", Icons.Default.TwoWheeler),
        NavItem("Settings", Icons.Default.Settings)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xE60F1620))
            .border(1.dp, GlassBorderDark, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) PrimaryGreen else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        color = if (isSelected) PrimaryGreen else TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private data class NavItem(val label: String, val icon: ImageVector)