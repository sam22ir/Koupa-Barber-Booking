package com.koupa.barberbooking.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.koupa.barberbooking.presentation.auth.BarberGoogleSignInScreen
import com.koupa.barberbooking.presentation.auth.CreateShopStep1Screen
import com.koupa.barberbooking.presentation.auth.CreateShopStep2Screen
import com.koupa.barberbooking.presentation.auth.EditShopScreen
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.presentation.barber.BarberDashboardScreen
import com.koupa.barberbooking.presentation.barber.SlotManagementScreen
import com.koupa.barberbooking.presentation.booking.BookConfirmationScreen
import com.koupa.barberbooking.presentation.booking.BookDateScreen
import com.koupa.barberbooking.presentation.booking.BookTimeScreen
import com.koupa.barberbooking.presentation.customer.CustomerAppointmentsScreen
import com.koupa.barberbooking.presentation.customer.CustomerHomeScreen
import com.koupa.barberbooking.presentation.notifications.NotificationsScreen
import com.koupa.barberbooking.presentation.splash.SplashScreen

// Shared enter/exit transitions
private val screenEnter = fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 10 }
private val screenExit  = fadeOut(tween(220)) + slideOutHorizontally(tween(220)) { -it / 10 }
private val screenPopEnter  = fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -it / 10 }
private val screenPopExit   = fadeOut(tween(220)) + slideOutHorizontally(tween(220)) { it / 10 }

/**
 * All navigation destinations for Koupa app.
 */
object KoupaDestinations {
    const val SPLASH                  = "splash"
    const val CUSTOMER_HOME           = "customer_home"
    const val CUSTOMER_APPOINTMENTS   = "customer_appointments"
    const val BARBER_DASHBOARD        = "barber_dashboard"
    const val BARBER_SLOT_MANAGEMENT  = "slot_management"
    const val NOTIFICATIONS           = "notifications"

    // Barber onboarding
    const val BARBER_GOOGLE_SIGN_IN   = "barber_google_sign_in/{phoneNumber}"
    const val CREATE_SHOP_STEP1       = "create_shop_step1/{ownerId}/{googleUid}/{phoneNumber}"
    const val CREATE_SHOP_STEP2       = "create_shop_step2/{ownerId}/{googleUid}"
    const val EDIT_SHOP               = "edit_shop/{shopId}/{ownerId}"

    fun barberGoogleSignIn(phone: String) = "barber_google_sign_in/$phone"
    fun createShopStep1(ownerId: String, googleUid: String, phone: String) =
        "create_shop_step1/$ownerId/$googleUid/$phone"
    fun createShopStep2(ownerId: String, googleUid: String) =
        "create_shop_step2/$ownerId/$googleUid"
    fun editShop(shopId: String, ownerId: String) = "edit_shop/$shopId/$ownerId"

    // Booking flow
    const val BOOK_DATE    = "book_date/{shopId}"
    const val BOOK_TIME    = "book_time/{shopId}/{date}"
    const val BOOK_CONFIRM = "book_confirm/{shopId}/{date}/{time}"

    fun bookDate(shopId: String) = "book_date/$shopId"
    fun bookTime(shopId: String, date: String) = "book_time/$shopId/$date"
    fun bookConfirm(shopId: String, date: String, time: String) = "book_confirm/$shopId/$date/$time"
}

@Composable
fun KoupaNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = KoupaDestinations.SPLASH
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
        enterTransition  = { screenEnter },
        exitTransition   = { screenExit },
        popEnterTransition  = { screenPopEnter },
        popExitTransition   = { screenPopExit }
    ) {

        // ─── Splash ──────────────────────────────────────────────────────────
        composable(
            route = KoupaDestinations.SPLASH,
            enterTransition = { fadeIn(tween(500)) },
            exitTransition  = { fadeOut(tween(400)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(KoupaDestinations.CUSTOMER_HOME) {
                        popUpTo(KoupaDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ─── Customer Home ────────────────────────────────────────────────────
        composable(KoupaDestinations.CUSTOMER_HOME) {
            CustomerHomeScreen(
                onNavigateToAppointments = {
                    navController.navigate(KoupaDestinations.CUSTOMER_APPOINTMENTS)
                },
                onNavigateToAccount = {
                    // TODO: Account screen
                },
                onBookAppointment = { shopId ->
                    navController.navigate(KoupaDestinations.bookDate(shopId))
                }
            )
        }

        // ─── Customer Appointments ────────────────────────────────────────────
        composable(KoupaDestinations.CUSTOMER_APPOINTMENTS) {
            CustomerAppointmentsScreen(
                onNavigateToHome = {
                    navController.navigate(KoupaDestinations.CUSTOMER_HOME) {
                        popUpTo(KoupaDestinations.CUSTOMER_HOME) { inclusive = false }
                    }
                },
                onNavigateToAccount = {}
            )
        }

        // ─── Booking: Step 1 — Date ───────────────────────────────────────────
        composable(
            route = KoupaDestinations.BOOK_DATE,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            BookDateScreen(
                shopId   = shopId,
                onBack   = { navController.popBackStack() },
                onContinue = { date ->
                    navController.navigate(KoupaDestinations.bookTime(shopId, date))
                }
            )
        }

        // ─── Booking: Step 2 — Time ───────────────────────────────────────────
        composable(
            route = KoupaDestinations.BOOK_TIME,
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType },
                navArgument("date")   { type = NavType.StringType }
            )
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            val date   = backStack.arguments?.getString("date")   ?: ""
            BookTimeScreen(
                shopId       = shopId,
                selectedDate = date,
                onBack       = { navController.popBackStack() },
                onContinue   = { time ->
                    navController.navigate(KoupaDestinations.bookConfirm(shopId, date, time))
                }
            )
        }

        // ─── Booking: Step 3 — Confirmation ──────────────────────────────────
        composable(
            route = KoupaDestinations.BOOK_CONFIRM,
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType },
                navArgument("date")   { type = NavType.StringType },
                navArgument("time")   { type = NavType.StringType }
            )
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            val date   = backStack.arguments?.getString("date")   ?: ""
            val time   = backStack.arguments?.getString("time")   ?: ""
            BookConfirmationScreen(
                shopId       = shopId,
                selectedDate = date,
                selectedTime = time,
                onBack       = { navController.popBackStack() },
                onConfirm    = {
                    navController.navigate(KoupaDestinations.CUSTOMER_HOME) {
                        popUpTo(KoupaDestinations.CUSTOMER_HOME) { inclusive = false }
                    }
                }
            )
        }

        // ─── Barber Dashboard ─────────────────────────────────────────────────
        composable(KoupaDestinations.BARBER_DASHBOARD) {
            BarberDashboardScreen(
                onNavigateToAppointments   = {
                    navController.navigate(KoupaDestinations.CUSTOMER_APPOINTMENTS)
                },
                onNavigateToNotifications  = {
                    navController.navigate(KoupaDestinations.NOTIFICATIONS)
                },
                onNavigateToProfile        = {}, // Future: profile screen
                onNavigateToSlotManagement = {
                    navController.navigate(KoupaDestinations.BARBER_SLOT_MANAGEMENT)
                }
            )
        }

        // ─── Barber Slot Management ───────────────────────────────────────────
        composable(KoupaDestinations.BARBER_SLOT_MANAGEMENT) {
            SlotManagementScreen(
                onBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(KoupaDestinations.BARBER_DASHBOARD) {
                        popUpTo(KoupaDestinations.BARBER_DASHBOARD) { inclusive = false }
                    }
                },
                onNavigateToNotifications = {
                    navController.navigate(KoupaDestinations.NOTIFICATIONS)
                },
                onNavigateToProfile = {}
            )
        }

        // ─── Notifications ────────────────────────────────────────────────────
        composable(KoupaDestinations.NOTIFICATIONS) {
            NotificationsScreen(
                onNavigateToHome = {
                    navController.navigate(KoupaDestinations.BARBER_DASHBOARD) {
                        popUpTo(KoupaDestinations.BARBER_DASHBOARD) { inclusive = false }
                    }
                },
                onNavigateToAppointments = {},
                onNavigateToProfile      = {}
            )
        }
        // ─── Barber Google Sign-In ────────────────────────────────────────────
        composable(
            route = KoupaDestinations.BARBER_GOOGLE_SIGN_IN,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { back ->
            val phone = back.arguments?.getString("phoneNumber") ?: ""
            BarberGoogleSignInScreen(
                phoneNumber    = phone,
                onSignInSuccess = { googleUid, _ ->
                    // ownerId placeholder — resolved after phone auth saved userId
                    navController.navigate(KoupaDestinations.createShopStep1("pending", googleUid, phone))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Create Shop Step 1 ───────────────────────────────────────────────
        composable(
            route = KoupaDestinations.CREATE_SHOP_STEP1,
            arguments = listOf(
                navArgument("ownerId")    { type = NavType.StringType },
                navArgument("googleUid") { type = NavType.StringType },
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { back ->
            val ownerId   = back.arguments?.getString("ownerId")    ?: ""
            val googleUid = back.arguments?.getString("googleUid")  ?: ""
            CreateShopStep1Screen(
                onBack = { navController.popBackStack() },
                onNext = {
                    navController.navigate(KoupaDestinations.createShopStep2(ownerId, googleUid))
                }
            )
        }

        // ─── Create Shop Step 2 ───────────────────────────────────────────────
        composable(
            route = KoupaDestinations.CREATE_SHOP_STEP2,
            arguments = listOf(
                navArgument("ownerId")    { type = NavType.StringType },
                navArgument("googleUid") { type = NavType.StringType }
            )
        ) { back ->
            val ownerId   = back.arguments?.getString("ownerId")    ?: ""
            val googleUid = back.arguments?.getString("googleUid")  ?: ""
            CreateShopStep2Screen(
                ownerId    = ownerId,
                googleUid  = googleUid,
                isEditMode = false,
                onBack     = { navController.popBackStack() },
                onSuccess  = {
                    navController.navigate(KoupaDestinations.BARBER_DASHBOARD) {
                        popUpTo(KoupaDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // ─── Edit Shop ────────────────────────────────────────────────────────
        composable(
            route = KoupaDestinations.EDIT_SHOP,
            arguments = listOf(
                navArgument("shopId")  { type = NavType.StringType },
                navArgument("ownerId") { type = NavType.StringType }
            )
        ) { back ->
            val shopId  = back.arguments?.getString("shopId")  ?: ""
            val ownerId = back.arguments?.getString("ownerId") ?: ""
            // Minimal BarberShop stub — viewModel.loadShopForEdit() populates all fields
            val shopStub = BarberShop(id = shopId, ownerId = ownerId, shopName = "", city = "", wilayaCode = null)
            EditShopScreen(
                shop    = shopStub,
                onBack  = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
