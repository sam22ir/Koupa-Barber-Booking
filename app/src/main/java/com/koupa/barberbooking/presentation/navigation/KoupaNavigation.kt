package com.koupa.barberbooking.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.koupa.barberbooking.presentation.auth.OtpVerificationScreen
import com.koupa.barberbooking.presentation.auth.PhoneEntryScreen
import com.koupa.barberbooking.presentation.auth.RoleSelectionScreen
import com.koupa.barberbooking.presentation.customer.CustomerHomeScreen
import com.koupa.barberbooking.presentation.barber.BarberDashboardScreen

/**
 * Navigation graph for Koupa app.
 * Handles auth flow and main app navigation.
 */
object KoupaDestinations {
    const val SPLASH = "splash"
    const val PHONE_ENTRY = "phone_entry"
    const val OTP_VERIFICATION = "otp_verification"
    const val ROLE_SELECTION = "role_selection"
    const val CUSTOMER_HOME = "customer_home"
    const val BARBER_DASHBOARD = "barber_dashboard"
}

@Composable
fun KoupaNavigation(
    navController: NavHostController,
    startDestination: String = KoupaDestinations.PHONE_ENTRY
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(KoupaDestinations.PHONE_ENTRY) {
            PhoneEntryScreen(
                onOtpSent = {
                    navController.navigate(KoupaDestinations.OTP_VERIFICATION)
                }
            )
        }

        composable(KoupaDestinations.OTP_VERIFICATION) {
            OtpVerificationScreen(
                onVerificationSuccess = {
                    navController.navigate(KoupaDestinations.CUSTOMER_HOME) {
                        popUpTo(KoupaDestinations.PHONE_ENTRY) { inclusive = true }
                    }
                },
                onNavigateToRoleSelection = {
                    navController.navigate(KoupaDestinations.ROLE_SELECTION)
                }
            )
        }

        composable(KoupaDestinations.ROLE_SELECTION) {
            RoleSelectionScreen(
                onComplete = {
                    navController.navigate(KoupaDestinations.CUSTOMER_HOME) {
                        popUpTo(KoupaDestinations.PHONE_ENTRY) { inclusive = true }
                    }
                }
            )
        }

        composable(KoupaDestinations.CUSTOMER_HOME) {
            CustomerHomeScreen()
        }

        composable(KoupaDestinations.BARBER_DASHBOARD) {
            BarberDashboardScreen()
        }
    }
}
