package com.koupa.barberbooking.presentation.barber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.koupa.barberbooking.domain.model.AvailabilitySlot
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.domain.model.Review
import com.koupa.barberbooking.ui.theme.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

// ══════════════════════════════════════════════════════════════════════════════
// BarberShopProfileScreen — Customer-facing shop profile & booking
// ══════════════════════════════════════════════════════════════════════════════

// ── Data Models ──────────────────────────────────────────────────────────────

data class ShopService(
    val id: String,
    val name: String,
    val duration: String,
    val price: Int,
    val icon: ImageVector,
    val isSelected: Boolean = false
)

data class TimeSlot(
    val time: String,
    val isAvailable: Boolean = true,
    val isSelected: Boolean = false
)

// ── Main Screen ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberShopProfileScreen(
    shopId: String = "",
    onNavigateBack: () -> Unit = {},
    onBookNow: (slotId: String, totalPrice: Int) -> Unit = { _, _ -> },
    viewModel: BarberShopProfileViewModel = hiltViewModel()
) {
    // ── State ────────────────────────────────────────────────────────────────
    val uiState by viewModel.uiState.collectAsState()

    // Load shop data when screen opens
    LaunchedEffect(shopId) {
        if (shopId.isNotEmpty()) {
            viewModel.loadShopData(shopId)
        }
    }

    // Get shop data from state
    val shop = uiState.shop
    val displayRating = uiState.averageRating.takeIf { it > 0 } ?: shop?.averageRating ?: 0.0
    val displayReviewCount = uiState.reviewCount.takeIf { it > 0 } ?: shop?.reviewCount ?: 0

    // Convert services from shop to UI model
    var selectedServices by remember(shop?.services) {
        mutableStateOf(
            shop?.services?.mapIndexed { index, serviceName ->
                ShopService(
                    id = index.toString(),
                    name = serviceName,
                    duration = "30 دقيقة",
                    price = (shop.priceMin + shop.priceMax) / 2,
                    icon = when {
                        serviceName.contains("شعر") || serviceName.contains("hair") -> Icons.Default.ContentCut
                        serviceName.contains("ذقن") || serviceName.contains("beard") -> Icons.Default.Face
                        serviceName.contains("تصفيف") || serviceName.contains("style") -> Icons.Default.Brush
                        else -> Icons.Default.ContentCut
                    },
                    isSelected = index == 0
                )
            } ?: listOf(
                ShopService("1", "قص شعر", "30 دقيقة", 500, Icons.Default.ContentCut, isSelected = true)
            )
        )
    }

    // Convert slots from ViewModel to UI model
    val morningSlots = remember(uiState.morningSlots) {
        uiState.morningSlots.map { slot ->
            TimeSlot(
                time = formatTime(slot.slotTime),
                isAvailable = !slot.isBooked,
                isSelected = slot.id == uiState.selectedSlot?.id
            )
        }
    }

    val afternoonSlots = remember(uiState.afternoonSlots) {
        uiState.afternoonSlots.map { slot ->
            TimeSlot(
                time = formatTime(slot.slotTime),
                isAvailable = !slot.isBooked,
                isSelected = slot.id == uiState.selectedSlot?.id
            )
        }
    }

    // Calculate total price
    val totalPrice = remember(selectedServices) {
        selectedServices.filter { it.isSelected }.sumOf { it.price }
    }

    Scaffold(
        topBar = {
            // Glass header with back button
            TopAppBar(
                title = {
                    Text(
                        text = shop?.shopName ?: "تحميل...",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = KoupaDarkSlate
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "رجوع",
                            tint = KoupaTeal
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share action */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = KoupaTeal
                        )
                    }
                    IconButton(onClick = { /* Favorite action */ }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "مفضلة",
                            tint = KoupaTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            // Bottom booking bar
            BottomBookingBar(
                totalPrice = totalPrice,
                onBookNow = {
                    val slotId = uiState.selectedSlot?.id ?: ""
                    onBookNow(slotId, totalPrice)
                }
            )
        }
    ) { innerPadding ->
        // Show loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = KoupaTeal)
            }
            return@Scaffold
        }

        // Show error state
        if (uiState.error != null && shop == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = KoupaError,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "حدث خطأ في تحميل البيانات",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.loadShopData(shopId) },
                        colors = ButtonDefaults.buttonColors(containerColor = KoupaTeal)
                    ) {
                        Text("إعادة المحاولة")
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(KoupaBackground)
        ) {
            // ── Hero Image Section ──────────────────────────────────────────
            HeroSection(heroImageUrl = shop?.profilePhotoUrl)

            // ── Shop Info Card ──────────────────────────────────────────────
            ShopInfoCard(
                shopName = shop?.shopName ?: "",
                rating = displayRating,
                reviewCount = displayReviewCount,
                address = shop?.address ?: "${shop?.city ?: ""}",
                isOpen = shop?.isActive ?: true
            )

            // ── Services Section ────────────────────────────────────────────
            ServicesSection(
                services = selectedServices,
                onServiceToggle = { serviceId ->
                    selectedServices = selectedServices.map { service ->
                        if (service.id == serviceId) {
                            service.copy(isSelected = !service.isSelected)
                        } else {
                            service
                        }
                    }
                }
            )

            // ── Time Slots Section ──────────────────────────────────────────
            TimeSlotsSection(
                morningSlots = morningSlots,
                afternoonSlots = afternoonSlots,
                isLoading = uiState.isLoadingSlots,
                onSlotSelected = { slotTime, isMorning ->
                    // Find the actual slot from ViewModel state
                    val slots = if (isMorning) uiState.morningSlots else uiState.afternoonSlots
                    val selectedSlot = slots.find { formatTime(it.slotTime) == slotTime }
                    if (selectedSlot != null && !selectedSlot.isBooked) {
                        viewModel.selectSlot(selectedSlot)
                    }
                }
            )

            // ── Reviews Section ─────────────────────────────────────────────
            ReviewsSection(
                reviews = uiState.reviews,
                averageRating = displayRating,
                reviewCount = displayReviewCount,
                isLoading = uiState.isLoadingReviews,
                hasUserReviewed = uiState.hasUserReviewed,
                onAddReview = { viewModel.showReviewDialog() },
                onDeleteReview = { reviewId -> viewModel.deleteReview(reviewId, shopId) }
            )

            // Spacer for bottom bar
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Review Dialog
    if (uiState.showReviewDialog) {
        ReviewDialog(
            shopName = shop?.shopName ?: "",
            isSubmitting = uiState.isSubmittingReview,
            onSubmit = { rating, comment ->
                viewModel.submitReview(shopId, rating, comment)
            },
            onDismiss = { viewModel.hideReviewDialog() }
        )
    }
}

// ── Hero Section ──────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(heroImageUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        if (heroImageUrl != null) {
            AsyncImage(
                model = heroImageUrl,
                contentDescription = "صورة المحل",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder when no image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(KoupaTeal.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    tint = KoupaTeal.copy(alpha = 0.3f),
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            KoupaBackground.copy(alpha = 0.3f),
                            KoupaBackground
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
    }
}

// ── Shop Info Card ──────────────────────────────────────────────────────────

@Composable
private fun ShopInfoCard(
    shopName: String,
    rating: Double,
    reviewCount: Int,
    address: String,
    isOpen: Boolean
) {
    KoupaCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KoupaSpacing.md)
            .offset(y = (-48).dp)
    ) {
        Column(
            modifier = Modifier.padding(KoupaSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = shopName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = KoupaDarkSlate,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Rating row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = KoupaGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = KoupaGold,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "($reviewCount تقييم)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Open status badge
                Box(
                    modifier = Modifier
                        .clip(KoupaShapes.BadgeShape)
                        .background(if (isOpen) Color(0xFFE8F5F5) else Color(0xFFFFEBEE))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isOpen) "مفتوح الآن" else "مغلق",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isOpen) KoupaTeal else KoupaError,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Address row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

// ── Services Section ──────────────────────────────────────────────────────────

@Composable
private fun ServicesSection(
    services: List<ShopService>,
    onServiceToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KoupaSpacing.md)
            .offset(y = (-24).dp)
    ) {
        KoupaSectionHeader(
            title = "الخدمات المختارة",
            actionText = "عرض الكل",
            onAction = { /* Navigate to all services */ }
        )

        Spacer(modifier = Modifier.height(KoupaSpacing.sm))

        services.forEach { service ->
            ServiceItem(
                service = service,
                onToggle = { onServiceToggle(service.id) }
            )
            Spacer(modifier = Modifier.height(KoupaSpacing.sm))
        }
    }
}

@Composable
private fun ServiceItem(
    service: ShopService,
    onToggle: () -> Unit
) {
    val borderColor = if (service.isSelected) KoupaTeal.copy(alpha = 0.1f) else Color.Transparent
    val borderWidth = if (service.isSelected) 2.dp else 0.dp

    KoupaCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = borderColor,
                shape = KoupaShapes.CardShape
            )
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KoupaSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Service icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = service.icon,
                        contentDescription = null,
                        tint = KoupaTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(KoupaSpacing.md))

                Column {
                    Text(
                        text = service.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = KoupaDarkSlate
                        )
                    )
                    Text(
                        text = service.duration,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${service.price} د.ج",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = KoupaTeal
                    )
                )
                Icon(
                    imageVector = if (service.isSelected) Icons.Default.CheckCircle else Icons.Default.AddCircle,
                    contentDescription = if (service.isSelected) "محدد" else "إضافة",
                    tint = if (service.isSelected) KoupaGold else KoupaGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── Time Slots Section ──────────────────────────────────────────────────────

@Composable
private fun TimeSlotsSection(
    morningSlots: List<TimeSlot>,
    afternoonSlots: List<TimeSlot>,
    isLoading: Boolean = false,
    onSlotSelected: (time: String, isMorning: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KoupaSpacing.md)
    ) {
        KoupaSectionHeader(title = "المواعيد المتاحة")

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = KoupaTeal)
            }
            return@Column
        }

        if (morningSlots.isEmpty() && afternoonSlots.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد مواعيد متاحة لهذا اليوم",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
            return@Column
        }

        Spacer(modifier = Modifier.height(KoupaSpacing.sm))

        // Morning slots
        Text(
            text = "صباحاً",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = Modifier.height(KoupaSpacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KoupaSpacing.sm)
        ) {
            morningSlots.forEach { slot ->
                TimeSlotButton(
                    slot = slot,
                    onClick = { onSlotSelected(slot.time, true) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(KoupaSpacing.md))

        // Afternoon slots
        Text(
            text = "مساءً",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 14.sp
            )
        )
        Spacer(modifier = Modifier.height(KoupaSpacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KoupaSpacing.sm)
        ) {
            afternoonSlots.forEach { slot ->
                TimeSlotButton(
                    slot = slot,
                    onClick = { onSlotSelected(slot.time, false) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TimeSlotButton(
    slot: TimeSlot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        slot.isSelected -> KoupaTeal
        slot.isAvailable -> Color.Transparent
        else -> Color.Transparent
    }

    val contentColor = when {
        slot.isSelected -> Color.White
        slot.isAvailable -> KoupaDarkSlate
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }

    val borderColor = when {
        slot.isSelected -> KoupaTeal
        slot.isAvailable -> MaterialTheme.colorScheme.outlineVariant
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                enabled = slot.isAvailable,
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = slot.time,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = contentColor,
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

// ── Reviews Section ──────────────────────────────────────────────────────────

@Composable
private fun ReviewsSection(
    reviews: List<Review>,
    averageRating: Double,
    reviewCount: Int,
    isLoading: Boolean,
    hasUserReviewed: Boolean,
    onAddReview: () -> Unit,
    onDeleteReview: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KoupaSpacing.md)
    ) {
        KoupaSectionHeader(
            title = "التقييمات والمراجعات",
            actionText = if (!hasUserReviewed) "أضف تقييمك" else null,
            onAction = if (!hasUserReviewed) onAddReview else null
        )

        Spacer(modifier = Modifier.height(KoupaSpacing.sm))

        // Rating summary card
        RatingSummaryCard(
            averageRating = averageRating,
            reviewCount = reviewCount
        )

        Spacer(modifier = Modifier.height(KoupaSpacing.md))

        // Reviews list
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = KoupaTeal)
            }
        } else if (reviews.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = KoupaGold.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لا توجد تقييمات بعد",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                    if (!hasUserReviewed) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onAddReview) {
                            Text(
                                text = "كن أول من يقيم",
                                color = KoupaTeal
                            )
                        }
                    }
                }
            }
        } else {
            // Show reviews
            reviews.forEach { review ->
                ReviewItem(
                    reviewerName = review.customerName ?: "مستخدم",
                    rating = review.rating,
                    comment = review.comment,
                    date = formatDate(review.createdAt),
                    isOwnReview = false, // TODO: Check if current user
                    onDelete = { onDeleteReview(review.id) }
                )
                Spacer(modifier = Modifier.height(KoupaSpacing.sm))
            }
        }
    }
}

@Composable
private fun RatingSummaryCard(
    averageRating: Double,
    reviewCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Big rating number
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%.1f", averageRating),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = KoupaDarkSlate,
                        fontSize = 48.sp
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < averageRating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < averageRating.toInt()) KoupaGold else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$reviewCount تقييم",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Rating bars
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RatingBar(5, 0.7f)
                RatingBar(4, 0.2f)
                RatingBar(3, 0.05f)
                RatingBar(2, 0.03f)
                RatingBar(1, 0.02f)
            }
        }
    }
}

@Composable
private fun RatingBar(stars: Int, percentage: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$stars",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.outline,
                fontSize = 12.sp
            ),
            modifier = Modifier.width(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .clip(RoundedCornerShape(4.dp))
                    .background(KoupaGold)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("ar"))
    return sdf.format(Date(timestamp))
}

private fun formatTime(time: LocalTime): String {
    val hour = time.hour
    val minute = time.minute
    val period = if (hour < 12) "ص" else "م"
    val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
    return String.format("%02d:%02d %s", displayHour, minute, period)
}

// ── Bottom Booking Bar ──────────────────────────────────────────────────────

@Composable
private fun BottomBookingBar(
    totalPrice: Int,
    onBookNow: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ),
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KoupaSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total price
            Column {
                Text(
                    text = "الإجمالي",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "$totalPrice د.ج",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = KoupaTeal,
                        fontSize = 20.sp
                    )
                )
            }

            // Book now button
            Button(
                onClick = onBookNow,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(start = KoupaSpacing.md),
                shape = KoupaShapes.ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KoupaTeal,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Text(
                    text = "احجز الآن",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}