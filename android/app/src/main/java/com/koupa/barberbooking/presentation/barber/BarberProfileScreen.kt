package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.koupa.barberbooking.presentation.components.BarberBottomNav
import com.koupa.barberbooking.presentation.components.BarberTab
import com.koupa.barberbooking.data.local.UserPreferences
import com.koupa.barberbooking.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale

@Composable
fun BarberProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToEditShop: (shopId: String, ownerId: String) -> Unit = { _, _ -> },
    onNavigateToWorkingHours: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    userPreferences: UserPreferences? = null
) {
    val phone = remember {
        FirebaseAuth.getInstance().currentUser?.phoneNumber ?: "حلاق كوبا"
    }
    
    val viewModel: BarberProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Trigger data load when ViewModel is ready
    LaunchedEffect(Unit) {
        viewModel.loadShopData()
    }

     Scaffold(
         bottomBar = {
             BarberBottomNav(
                 selectedTab = BarberTab.PROFILE,
                 onTabSelected = { tab ->
                     when (tab) {
                         BarberTab.HOME          -> onNavigateToHome()
                         BarberTab.APPOINTMENTS  -> onNavigateToAppointments()
                         BarberTab.NOTIFICATIONS -> onNavigateToNotifications()
                         BarberTab.PROFILE       -> {}
                     }
                 }
             )
         }
     ) { inner ->
         Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(inner)
                 .background(MaterialTheme.colorScheme.background)
         ) {
             // ── Premium Header ──────────────────────────────────────────────────
Box(
                  modifier = Modifier
                      .fillMaxWidth()
                      .background(TealGradient)
                      .padding(vertical = 32.dp, horizontal = 24.dp),
                 contentAlignment = Alignment.Center
             ) {
                 Column(horizontalAlignment = Alignment.CenterHorizontally) {
                     // Show loading state
                     if (uiState.isLoading) {
                         // Loading indicator
                         androidx.compose.material3.CircularProgressIndicator(
                             color = Color.White,
                             modifier = Modifier.size(24.dp)
                         )
                     } else {
                         // Show error state
                         uiState.error?.let { error ->
                             Text(
                                 text = error,
                                 color = Color.Red,
                                 style = MaterialTheme.typography.bodyMedium
                             )
                         } ?: uiState.shop?.let { shop ->
                             // Shop image with gold border
                             Box(
                                 modifier = Modifier
                                     .size(96.dp)
                                     .clip(CircleShape)
                                     .background(KoupaGold)
                                     .padding(4.dp),
                                 contentAlignment = Alignment.Center
                             ) {
                                 Box(
                                     modifier = Modifier
                                         .size(88.dp)
                                         .clip(CircleShape)
                                         .background(Color.White),
                                     contentAlignment = Alignment.Center
                                 ) {
                                     // Load shop image if available, otherwise show icon
                                     shop.profilePhotoUrl?.let { imageUrl ->
                                         AsyncImage(
                                             model = imageUrl,
                                             contentDescription = null,
                                             modifier = Modifier
                                                 .size(88.dp)
                                                 .clip(CircleShape),
                                             contentScale = ContentScale.Crop
                                         )
                                     } ?: Icon(
                                         imageVector = Icons.Default.ContentCut,
                                         contentDescription = null,
                                         tint = KoupaTeal,
                                         modifier = Modifier.size(48.dp)
                                     )
                                 }
                             }
                             Spacer(Modifier.height(16.dp))
                             Text(
                                 text = shop.shopName,
                                 style = MaterialTheme.typography.titleLarge.copy(
                                     color = Color.White,
                                     fontWeight = FontWeight.Bold,
                                     fontSize = 22.sp
                                 )
                             )
                             Spacer(Modifier.height(4.dp))
                             // Rating row
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.Center
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.Star,
                                     contentDescription = null,
                                     tint = KoupaGold,
                                     modifier = Modifier.size(18.dp)
                                 )
                                 Spacer(Modifier.width(4.dp))
                                 Text(
                                     text = "${shop.averageRating ?: 0.0}",
                                     style = MaterialTheme.typography.bodyMedium.copy(
                                         color = Color.White,
                                         fontWeight = FontWeight.SemiBold
                                     )
                                 )
                             }
                             Spacer(Modifier.height(4.dp))
                             Text(
                                 text = shop.address ?: "العنوان غير متوفر",
                                 style = MaterialTheme.typography.bodySmall.copy(
                                     color = Color.White.copy(alpha = 0.8f),
                                     fontWeight = FontWeight.Normal
                                 ),
                                 textAlign = TextAlign.Center
                             )
}
                      }
                      // Fallback if no shop data
                      if (uiState.shop == null && uiState.error == null) {
                          Text(
                              text = "جاري تحميل بيانات المتجر...",
                              color = Color.White,
                              style = MaterialTheme.typography.bodyLarge
                          )
                      }
                     }
                 }
             }

            Spacer(Modifier.height(24.dp))

            // ── Menu Items ───────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KoupaSpacing.md),
                verticalArrangement = Arrangement.spacedBy(KoupaSpacing.sm)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Store,
                    label = "بيانات المحل",
                    subtitle = "تعديل اسم ومعلومات متجرك",
                    onClick = {
                        val shopId = uiState.shop?.id ?: ""
                        val ownerId = userPreferences?.getUserId() ?: ""
                        onNavigateToEditShop(shopId, ownerId)
                    }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Schedule,
                    label = "ساعات العمل",
                    subtitle = "تحديد أوقات الفتح والإغلاق",
                    onClick = onNavigateToWorkingHours
                )
                ProfileMenuItem(
                    icon = Icons.Default.ContentCut,
                    label = "الخدمات",
                    subtitle = "إدارة قائمة الخدمات والأسعار",
                    onClick = onNavigateToServices
                )
                ProfileMenuItem(
                    icon = Icons.Default.Analytics,
                    label = "الإحصائيات",
                    subtitle = "تتبع أداء متجرك",
                    onClick = onNavigateToStatistics
                )
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    label = "الإعدادات",
                    subtitle = "تفضيلات التطبيق والإشعارات",
 onClick = onNavigateToSettings
 )
 }

 Spacer(Modifier.height(KoupaSpacing.lg))

 // ── Logout Button ────────────────────────────────────────────────
 OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KoupaSpacing.md)
                    .height(52.dp),
                shape = KoupaShapes.ButtonShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = KoupaError
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = KoupaError.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = KoupaError,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(KoupaSpacing.sm))
                    Text(
                        text = "تسجيل الخروج",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = KoupaError
                        )
                    )
                }
            }

            Spacer(Modifier.height(KoupaSpacing.md))

            Text(
                text = "KOUPA v1.0.0\nكوبا — منصة حجز الحلاقة الجزائرية",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
 .padding(bottom = KoupaSpacing.md)
 )
 }
 }

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    KoupaCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KoupaSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KoupaIconBox(
                icon = icon,
                size = 44.dp,
                iconSize = 22.dp
            )
            Spacer(Modifier.width(KoupaSpacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = KoupaDarkSlate
                    )
                )
                Text(
                    text = subtitle,
style = MaterialTheme.typography.bodySmall.copy(
                color = KoupaColors.Gray
            )
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = KoupaColors.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}