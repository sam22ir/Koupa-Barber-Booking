package com.koupa.barberbooking.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.koupa.barberbooking.data.local.UserPreferences
import com.koupa.barberbooking.ui.theme.*

/**
 * Language Selection Screen - First screen in onboarding flow
 * Allows user to select Arabic (default) or English
 */
@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (String) -> Unit,
    userPreferences: UserPreferences,
    viewModel: LanguageSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animation for selected card border
    val animatedBorderColor by animateColorAsState(
        targetValue = if (uiState.selectedLanguage == "ar") KoupaGold else if (uiState.selectedLanguage == "en") KoupaTeal else Color.Transparent,
        label = "borderColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        // Top App Bar with logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button placeholder
            Box(modifier = Modifier.size(48.dp))

            Text(
                text = "KOUPA",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = KoupaDarkSlate,
                    letterSpacing = 2.sp
                )
            )

            // Spacer for centering
            Box(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Editorial Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "اختر اللغة",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = KoupaDarkSlate,
                    fontSize = 36.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SELECT YOUR PREFERRED DIALECT",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = KoupaDarkSlate.copy(alpha = 0.7f),
                    letterSpacing = 0.2.em,
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Language Selection Cards - Bento Style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Arabic Card (Primary - Pre-selected)
            LanguageCard(
                modifier = Modifier.weight(1f),
                language = "ar",
                languageName = "العربية",
                subtitle = "Native Experience",
                isSelected = uiState.selectedLanguage == "ar",
                selectedColor = KoupaGold,
                icon = Icons.Default.Language,
                onClick = { viewModel.selectLanguage("ar") }
            )

            // English Card (Secondary)
            LanguageCard(
                modifier = Modifier.weight(1f),
                language = "en",
                languageName = "English",
                subtitle = "Global Standard",
                isSelected = uiState.selectedLanguage == "en",
                selectedColor = KoupaTeal,
                icon = Icons.Default.Translate,
                onClick = { viewModel.selectLanguage("en") }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Continue Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(KoupaGold)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(KoupaExtendedColors.InputBorder)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(KoupaExtendedColors.InputBorder)
                )
            }

            // Continue Button
            Button(
                onClick = {
                    viewModel.saveLanguage(userPreferences)
                    onLanguageSelected(uiState.selectedLanguage)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KoupaGold
                ),
                enabled = uiState.selectedLanguage.isNotEmpty()
            ) {
                Text(
                    text = if (uiState.selectedLanguage == "en") "Continue" else "استمرار",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun LanguageCard(
    modifier: Modifier = Modifier,
    language: String,
    languageName: String,
    subtitle: String,
    isSelected: Boolean,
    selectedColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (language == "ar") KoupaGold.copy(alpha = 0.15f) else KoupaTeal.copy(alpha = 0.1f)
        } else {
            Color.White
        },
        label = "cardBg"
    )

val borderColor by animateColorAsState(
    targetValue = if (isSelected) selectedColor else KoupaExtendedColors.InputBorder,
    label = "cardBorder"
)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isSelected) selectedColor.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.1f)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (language == "ar") KoupaGold else KoupaTeal,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Language Name
            Text(
                text = languageName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = KoupaDarkSlate,
                    fontSize = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (language == "ar") KoupaGold else KoupaTeal,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.15.em
                )
            )

            // Selection Checkmark
            if (isSelected) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(selectedColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}