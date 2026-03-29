package com.koupa.barberbooking.presentation.customer.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.ui.theme.KoupaTeal
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun CustomerMapScreen(
    viewModel: CustomerMapViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToShopDetails: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    
    // Status bar padding
    val topPadding = with(LocalContext.current.resources) {
        val resourceId = getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) getDimensionPixelSize(resourceId).dp else 0.dp
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map AndroidView
        AndroidView(
            factory = { ctx ->
                Configuration.getInstance().userAgentValue = ctx.packageName
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(36.737232, 3.086472)) // Algiers
                    mapView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topPadding + 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Black.copy(alpha = 0.35f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                    androidx.compose.material3.Text(
                        text = "ابحث عن صالون",
                        modifier = Modifier.padding(start = 8.dp),
                        color = Color.White
                    )
                }
            }
        }

        // Back button
        SmallFloatingActionButton(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = topPadding + 16.dp, end = 16.dp),
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                modifier = Modifier.size(20.dp)
            )
        }

        // Home button
        SmallFloatingActionButton(
            onClick = { /* Handle home button click */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 140.dp),
            containerColor = Color.White,
            contentColor = KoupaTeal
        ) {
            Icon(Icons.Default.Home, contentDescription = "الرئيسية", modifier = Modifier.size(18.dp))
        }

// Location button
val userLoc = uiState.userLocation
if (userLoc != null) {
FloatingActionButton(
onClick = {
mapView?.controller?.animateTo(GeoPoint(userLoc.latitude, userLoc.longitude))
},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 72.dp),
                containerColor = Color.White,
                contentColor = KoupaTeal
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "موقعي", modifier = Modifier.size(20.dp))
            }
        }
    }
}