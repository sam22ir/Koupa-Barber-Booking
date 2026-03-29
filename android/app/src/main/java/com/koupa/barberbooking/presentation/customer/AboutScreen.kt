package com.koupa.barberbooking.presentation.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koupa.barberbooking.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val koupaTeal = Color(0xFF1A7A78)
    val koupaGold = Color(0xFFE1A553)
    val koupaDarkSlate = Color(0xFF323E4B)
    val koupaBackground = Color(0xFFF3F5F7)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "حول كوبا",
                        color = koupaDarkSlate,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = koupaDarkSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = koupaBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCut,
                    contentDescription = null,
                    tint = koupaTeal,
                    modifier = Modifier.size(80.dp)
                )
            }

            // App Name
            Text(
                text = "كوبا",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = koupaTeal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Version
            Text(
                text = "الإصدار 1.0.0",
                fontSize = 16.sp,
                color = koupaDarkSlate,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "حول التطبيق",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = koupaDarkSlate
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "كوبا هو تطبيق حجز مواعيد للحلاقين في الجزائر. يتيح للعملاء حجز مواعيد بسهولة، وللحلاقين إدارة متاجرهم ومواعيدهم بكفاءة.",
                        fontSize = 16.sp,
                        color = koupaDarkSlate,
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Features
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "المميزات",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = koupaDarkSlate
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureItem(icon = Icons.Default.CalendarToday, text = "حجز مواعيد سهل وسريع")
                    FeatureItem(icon = Icons.Default.LocationOn, text = "البحث عن حلاقين قريبين")
                    FeatureItem(icon = Icons.Default.Star, text = "تقييمات ومراجعات")
                    FeatureItem(icon = Icons.Default.Notifications, text = "إشعارات وتذكيرات")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تواصل معنا",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = koupaDarkSlate
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = koupaTeal,
                            modifier = Modifier.size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = koupaTeal,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Copyright
            Text(
                text = "© 2024 كوبا. جميع الحقوق محفوظة.",
                fontSize = 14.sp,
                color = koupaDarkSlate.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    val koupaTeal = Color(0xFF1A7A78)
    val koupaDarkSlate = Color(0xFF323E4B)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = koupaTeal,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = koupaDarkSlate
        )
    }
}
