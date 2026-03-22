package com.koupa.barberbooking.presentation.customer

import android.Manifest
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.koupa.barberbooking.R
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.presentation.auth.PhoneRegistrationDialog
import com.koupa.barberbooking.presentation.components.CustomerBottomNav
import com.koupa.barberbooking.presentation.components.CustomerTab
import com.koupa.barberbooking.ui.theme.KoupaGold
import com.koupa.barberbooking.ui.theme.KoupaTeal
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private val KoupaSlate = Color(0xFF323E4B)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CustomerHomeScreen(
    viewModel               : CustomerViewModel = hiltViewModel(),
    onNavigateToAppointments: () -> Unit        = {},
    onNavigateToAccount     : () -> Unit        = {},
    onBookAppointment       : (shopId: String) -> Unit = {}
) {
    var showPhoneDialog by remember { mutableStateOf(false) }
    var savedPhone      by remember { mutableStateOf<String?>(null) }
    var pendingShopId   by remember { mutableStateOf("") }
    var selectedTab     by remember { mutableStateOf(CustomerTab.HOME) }

    val homeState = viewModel.homeState.collectAsStateWithLifecycle().value

    // Location permission
    val locationPerm = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    LaunchedEffect(Unit) {
        viewModel.loadNearbyShops()
        if (!locationPerm.status.isGranted) locationPerm.launchPermissionRequest()
    }

    // Phone dialog
    if (showPhoneDialog) {
        PhoneRegistrationDialog(
            onDismiss = { showPhoneDialog = false },
            onPhoneConfirmed = { phone ->
                savedPhone = phone
                showPhoneDialog = false
                if (pendingShopId.isNotEmpty()) { onBookAppointment(pendingShopId); pendingShopId = "" }
            }
        )
    }

    // Bottom sheet for tapped marker
    homeState.selectedShop?.let { shop ->
        ShopMapBottomSheet(
            shop      = shop,
            onDismiss = { viewModel.selectShop(null) },
            onBookNow = { sid ->
                viewModel.selectShop(null)
                if (savedPhone != null) onBookAppointment(sid)
                else { pendingShopId = sid; showPhoneDialog = true }
            }
        )
    }

    fun tryBook(shopId: String) {
        if (savedPhone != null) onBookAppointment(shopId)
        else { pendingShopId = shopId; showPhoneDialog = true }
    }

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            CustomerTab.APPOINTMENTS -> onNavigateToAppointments()
            CustomerTab.ACCOUNT      -> onNavigateToAccount()
            else -> {}
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(shadowElevation = 0.dp, color = MaterialTheme.colorScheme.surface) {
                Column {
                    // ── App Bar ────────────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, contentDescription = "بحث", tint = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            "كوبا",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold, color = KoupaTeal, fontSize = 22.sp
                            )
                        )
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_round),
                            contentDescription = "شعار كوبا",
                            modifier = Modifier.size(38.dp).clip(CircleShape)
                        )
                    }

                    // ── List / Map Toggle ───────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(50)
                            )
                    ) {
                        listOf(HomeViewMode.LIST to "قائمة 🗒", HomeViewMode.MAP to "خريطة 🗺").forEach { (mode, label) ->
                            val isActive = homeState.viewMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isActive) KoupaTeal else Color.Transparent)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication        = null
                                    ) { viewModel.setViewMode(mode) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            CustomerBottomNav(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
    ) { padding ->
        AnimatedContent(
            targetState = homeState.viewMode,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            modifier = Modifier.padding(padding),
            label = "homeViewMode"
        ) { mode ->
            when (mode) {
                HomeViewMode.LIST -> ListContent(homeState, ::tryBook)
                HomeViewMode.MAP  -> MapContent(homeState, viewModel)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LIST VIEW (original — unchanged)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ListContent(homeState: CustomerHomeUiState, tryBook: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Location / Greeting
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                Text("الجزائر", style = MaterialTheme.typography.bodyMedium.copy(color = KoupaTeal), modifier = Modifier.padding(end = 4.dp))
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text("أهلاً بك في كوبا", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground))
        }
        Spacer(Modifier.height(8.dp))
        // Search bar
        Surface(color = MaterialTheme.colorScheme.surface) {
            OutlinedTextField(
                value = "", onValueChange = {},
                placeholder = { Text("ابحث عن حلاق", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFADB5BD)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = KoupaTeal,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )
        }
        Spacer(Modifier.height(12.dp))
        // Featured shops
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("عرض الكل", style = MaterialTheme.typography.bodyMedium.copy(color = KoupaTeal))
                Text("حلاقون متميزون", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (homeState.shops.isNotEmpty()) {
                    homeState.shops.take(5).forEach { shop ->
                        BarberShopCard(shop.shopName, "قص، لحية، عناية", "من ${shop.priceMin} دج", 4.5f) { tryBook(shop.id) }
                    }
                } else {
                    BarberShopCard("صالون الفخامة", "قص، لحية، عناية", "من 800 دج", 4.8f) { tryBook("demo-1") }
                    BarberShopCard("حلاقة كلاسيك",  "ستايل، كلاسيك",   "من 600 دج", 4.5f) { tryBook("demo-2") }
                    BarberShopCard("كوبا برستيج",   "فاخر، VIP",        "من 1200 دج", 4.9f) { tryBook("demo-3") }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // Services grid
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("الخدمات الأكثر طلباً", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ServiceChip(Icons.Default.ContentCut, "قص شعر")
                    ServiceChip(Icons.Default.Face,       "حلاقة لحية")
                    ServiceChip(Icons.Default.FaceRetouchingNatural, "تنظيف بشرة")
                    ServiceChip(Icons.Default.SelfImprovement, "مساج")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // Promo banner
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)).background(KoupaTeal).padding(20.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                Surface(shape = RoundedCornerShape(20.dp), color = KoupaGold) {
                    Text("عرض خاص", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold))
                }
                Spacer(Modifier.height(8.dp))
                Text("احصل على خصم 20%\nعلى حجزك الأول", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold, lineHeight = 24.sp), textAlign = TextAlign.End)
                Spacer(Modifier.height(4.dp))
                Text("استخدم الكود: KOUPA20", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB2DFDB)), textAlign = TextAlign.End)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { tryBook("promo-shop") }, colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                    Text("احجز الآن", color = KoupaTeal, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAP VIEW — osmdroid inside AndroidView
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MapContent(homeState: CustomerHomeUiState, viewModel: CustomerViewModel) {
    val context        = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val defaultCenter = GeoPoint(36.737232, 3.086472) // Algiers

    // Pre-loaded marker bitmaps keyed by shop id
    val markerBitmaps = remember { mutableStateMapOf<String, android.graphics.drawable.BitmapDrawable?>() }

    // Load bitmaps whenever the shops list or selection changes
    LaunchedEffect(homeState.shops, homeState.selectedShop?.id) {
        homeState.shops.forEach { shop ->
            coroutineScope.launch {
                markerBitmaps[shop.id] = createCircularMarkerBitmap(
                    context    = context,
                    photoUrl   = shop.profilePhotoUrl,
                    isSelected = homeState.selectedShop?.id == shop.id
                )
            }
        }
        // Always pre-load the selected-state variant too
        homeState.selectedShop?.let { shop ->
            coroutineScope.launch {
                markerBitmaps["${shop.id}_selected"] = createCircularMarkerBitmap(
                    context    = context,
                    photoUrl   = shop.profilePhotoUrl,
                    isSelected = true
                )
            }
        }
    }
    val userDotBitmap = remember { createUserDotBitmap(context) }

    // Hold a reference to the MapView for lifecycle callbacks
    val mapViewState = remember { mutableStateOf<MapView?>(null) }

    // Lifecycle: resume/pause the osmdroid map with the Compose lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val mv = mapViewState.value ?: return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_RESUME -> mv.onResume()
                Lifecycle.Event.ON_PAUSE  -> mv.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapViewState.value?.onDetach()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── osmdroid Map View ──────────────────────────────────────────────────
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { ctx ->
                // Must set user agent before creating MapView
                Configuration.getInstance().apply {
                    userAgentValue = ctx.packageName
                    osmdroidTileCache = java.io.File(ctx.cacheDir, "osmdroid_tiles")
                }
                MapView(ctx).also { mv ->
                    mapViewState.value = mv
                    mv.setTileSource(TileSourceFactory.MAPNIK)
                    mv.setMultiTouchControls(true)
                    mv.isTilesScaledToDpi = true
                    val center = homeState.userLatLon?.toGeoPoint() ?: defaultCenter
                    mv.controller.setZoom(12.0)
                    mv.controller.setCenter(center)
                }
            },
            update = { mv ->
                mv.overlays.clear()
                val route = homeState.activeRoute

                if (route != null) {
                    // ── Routing mode ───────────────────────────────────────────
                    // 1. Polyline
                    if (route.polyline.isNotEmpty()) {
                        val poly = Polyline(mv)
                        poly.setPoints(route.polyline.map { it.toGeoPoint() })
                        poly.outlinePaint.also {
                            it.color       = android.graphics.Color.parseColor("#1A7A78")
                            it.strokeWidth = 18f
                            it.isAntiAlias = true
                            it.strokeCap   = android.graphics.Paint.Cap.ROUND
                            it.strokeJoin  = android.graphics.Paint.Join.ROUND
                        }
                        mv.overlays.add(poly)
                    }
                    // 2. Booked shop marker (selected style)
                    val shopPos = GeoPoint(
                        route.shop.latitude  ?: defaultCenter.latitude,
                        route.shop.longitude ?: defaultCenter.longitude
                    )
                    val shopMarker = Marker(mv)
                    shopMarker.position = shopPos
                    shopMarker.icon     = markerBitmaps["${route.shop.id}_selected"]
                        ?: markerBitmaps[route.shop.id]
                    shopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    shopMarker.title = route.shop.shopName
                    shopMarker.setOnMarkerClickListener { _, _ ->
                        viewModel.selectShop(route.shop); true
                    }
                    mv.overlays.add(shopMarker)
                    // 3. User dot
                    homeState.userLatLon?.let { pos ->
                        val userMarker = Marker(mv)
                        userMarker.position = pos.toGeoPoint()
                        userMarker.icon     = userDotBitmap
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        mv.overlays.add(userMarker)
                    }
                    // Zoom to fit the route
                    if (route.polyline.size >= 2) {
                        val first = route.polyline.first().toGeoPoint()
                        val last  = route.polyline.last().toGeoPoint()
                        mv.zoomToBoundingBox(
                            org.osmdroid.util.BoundingBox.fromGeoPoints(listOf(first, last)).increaseByScale(1.4f),
                            true, 100
                        )
                    }
                } else {
                    // ── Normal mode: all nearby shops ──────────────────────────
                    homeState.shops.forEach { shop ->
                        if (shop.latitude != null && shop.longitude != null) {
                            val m = Marker(mv)
                            m.position = GeoPoint(shop.latitude, shop.longitude)
                            m.icon     = if (homeState.selectedShop?.id == shop.id)
                                markerBitmaps["${shop.id}_selected"] ?: markerBitmaps[shop.id]
                            else
                                markerBitmaps[shop.id]
                            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            m.title = shop.shopName
                            m.setOnMarkerClickListener { _, _ ->
                                viewModel.selectShop(shop); true
                            }
                            mv.overlays.add(m)
                        }
                    }
                    // Demo marker in Algiers when no data yet
                    if (homeState.shops.isEmpty()) {
                        val demoShop = BarberShop(id = "demo", ownerId = "", shopName = "صالون النخبة", city = "الجزائر", wilayaCode = 16)
                        val m = Marker(mv)
                        m.position = defaultCenter
                        m.icon     = userDotBitmap // fallback icon
                        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        m.title = demoShop.shopName
                        m.setOnMarkerClickListener { _, _ -> viewModel.selectShop(demoShop); true }
                        mv.overlays.add(m)
                    }
                }
                mv.invalidate()
            }
        )

        // ── ETA Card (post-booking routing mode) ───────────────────────────────
        homeState.activeRoute?.let { route ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Card(
                    shape  = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = KoupaTeal),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.clearRoute() }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "في طريقك إلى ${route.shop.shopName} 🛣",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White
                            )
                            if (route.durationMin > 0)
                                Text(
                                    "${route.durationMin} دقيقة تقريباً",
                                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f)
                                )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared sub-composables
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BarberShopCard(name: String, services: String, price: String, rating: Float, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier  = Modifier.width(180.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ContentCut, contentDescription = null, tint = KoupaTeal, modifier = Modifier.size(48.dp))
                Surface(modifier = Modifier.align(Alignment.TopStart).padding(8.dp), shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface) {
                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("$rating", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(Modifier.width(2.dp))
                        Icon(Icons.Default.Star, contentDescription = null, tint = KoupaGold, modifier = Modifier.size(10.dp))
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.End) {
                Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(2.dp))
                Text(services, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), textAlign = TextAlign.End)
                Spacer(Modifier.height(4.dp))
                Text(price, style = MaterialTheme.typography.bodySmall.copy(color = KoupaTeal, fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

@Composable
private fun ServiceChip(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = label, tint = KoupaTeal, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurface), textAlign = TextAlign.Center)
    }
}
