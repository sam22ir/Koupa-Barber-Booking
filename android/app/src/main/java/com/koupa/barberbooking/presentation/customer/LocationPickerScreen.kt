package com.koupa.barberbooking.presentation.customer

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

private val KoupaTeal = Color(0xFF1A7A78)
private val KoupaDarkSlate = Color(0xFF323E4B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    onNavigateBack: () -> Unit,
    onLocationSelected: (latitude: Double, longitude: Double, address: String) -> Unit
) {
 val context = LocalContext.current

 // Initialize OSMDroid
 val prefs = context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
 Configuration.getInstance().load(context, prefs)

 var currentLat by remember { mutableStateOf(36.7527) } // Default to Algiers
    var currentLng by remember { mutableStateOf(3.0425) }
    var selectedAddress by remember { mutableStateOf("جارٍ تحديد الموقع...") }

    LaunchedEffect(currentLat, currentLng) {
        // In a real app, you would call a reverse geocoding API here
        selectedAddress = "عنوان تجريبي، شارع المثال، المدينة"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "اختر الموقع",
                        color = KoupaDarkSlate
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = KoupaDarkSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // OSMDroid MapView
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(currentLat, currentLng))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Center pin icon
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = KoupaTeal,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )

            // Bottom Sheet
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedAddress,
                    color = KoupaDarkSlate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
                Button(
                    onClick = {
                        onLocationSelected(currentLat, currentLng, selectedAddress)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KoupaTeal
                    )
                ) {
                    Text(
                        text = "تأكيد الموقع",
                        color = Color.White
                    )
                }
            }
        }
    }
}
