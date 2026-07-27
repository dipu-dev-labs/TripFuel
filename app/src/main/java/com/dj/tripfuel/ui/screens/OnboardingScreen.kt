package com.dj.tripfuel.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dj.tripfuel.ui.components.GlassButton
import com.dj.tripfuel.ui.components.GlassCard
import com.dj.tripfuel.ui.theme.*

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val highlightColor: Color
)

@Composable
fun OnboardingScreen(
    onOnboardingFinished: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Track Every Kilometer",
                description = "High-precision GPS engine records your real delivery distance with sub-meter accuracy while riding.",
                icon = Icons.Default.Speed,
                highlightColor = PrimaryGreen
            ),
            OnboardingPage(
                title = "Know Real Fuel Expenses",
                description = "Automated continuous fuel cost calculation based on your bike's exact mileage and current petrol price.",
                icon = Icons.Default.LocalGasStation,
                highlightColor = SecondaryTeal
            ),
            OnboardingPage(
                title = "Auto-Calculate Daily Profit",
                description = "Input Rapido, Zomato, Swiggy, Uber Moto, or Porter earnings and get instant Net Profit & Profit per KM.",
                icon = Icons.Default.AttachMoney,
                highlightColor = PrimaryGreen
            ),
            OnboardingPage(
                title = "Ready to Earn Smarter",
                description = "Maximize your daily income, optimize petrol usage, and keep full control of your rider business.",
                icon = Icons.Default.TwoWheeler,
                highlightColor = AccentGreen
            )
        )
    }

    var currentPageIndex by remember { mutableIntStateOf(0) }
    val currentPage = pages[currentPageIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, Color(0xFF0F1820))
                )
            )
            .padding(24.dp)
    ) {
        // Skip Button
        if (currentPageIndex < pages.size - 1) {
            TextButton(
                onClick = onOnboardingFinished,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = "SKIP",
                    style = MaterialTheme.typography.labelLarge.copy(color = TextSecondary)
                )
            }
        }

        // Center Content Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { width -> width / 2 } togetherWith
                            fadeOut() + slideOutHorizontally { width -> -width / 2 }
                },
                label = "onboardingSlide"
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(page.highlightColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = null,
                            tint = page.highlightColor,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = TextPrimary
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = page.description,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextSecondary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (index == currentPageIndex) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentPageIndex) PrimaryGreen else GlassBorderDark
                            )
                    )
                }
            }
        }

        // Next / Get Started Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            GlassButton(
                text = if (currentPageIndex == pages.size - 1) "GET STARTED" else "NEXT",
                icon = Icons.Default.ArrowForward,
                onClick = {
                    if (currentPageIndex < pages.size - 1) {
                        currentPageIndex++
                    } else {
                        onOnboardingFinished()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
