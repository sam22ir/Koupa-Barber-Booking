package com.koupa.barberbooking.presentation.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.koupa.barberbooking.domain.model.BarberShop
import com.koupa.barberbooking.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedShopsScreen(
    onNavigateBack: () -> Unit,
    onBookShop: (String) -> Unit,
    onOpenShopProfile: (String) -> Unit,
    viewModel: FeaturedShopsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "المحلات المميزة",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = KoupaTeal
                )
            )
        },
        containerColor = KoupaBackground
    ) { padding ->
        when {
            uiState.isLoading -> {
                LoadingScreen(modifier = Modifier.fillMaxSize().padding(padding))
            }
            uiState.error != null -> {
                ErrorStateScreen(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.loadShops() },
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            }
            uiState.shops.isEmpty() -> {
                EmptyStateScreen(modifier = Modifier.fillMaxSize().padding(padding))
            }
            else -> {
                FeaturedShopsContent(
                    shops = uiState.shops,
                    currentSortBy = uiState.sortBy,
                    onSortOptionSelected = { sortBy -> viewModel.onSortChanged(sortBy) },
                    onBookShop = onBookShop,
                    onOpenShopProfile = onOpenShopProfile,
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(KoupaBackground)
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            color = KoupaTeal,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ErrorStateScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(KoupaBackground)
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = KoupaDarkSlate,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = KoupaDarkSlate,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "يرجى المحاولة مرة أخرى لاحقًا",
            style = MaterialTheme.typography.bodyLarge,
            color = KoupaDarkSlate.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KoupaTeal,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "إعادة المحاولة",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun EmptyStateScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(KoupaBackground)
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Icon(
            imageVector = Icons.Default.Store,
            contentDescription = null,
            tint = KoupaDarkSlate,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "لا توجد محلات مميزة حالياً",
            style = MaterialTheme.typography.titleMedium,
            color = KoupaDarkSlate,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "يرجى المحاولة مرة أخرى لاحقًا",
            style = MaterialTheme.typography.bodyLarge,
            color = KoupaDarkSlate.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FeaturedShopsContent(
    shops: List<BarberShop>,
    currentSortBy: String,
    onSortOptionSelected: (String) -> Unit,
    onBookShop: (String) -> Unit,
    onOpenShopProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SortOptionsRow(
            currentSortBy = currentSortBy,
            onSortOptionSelected = onSortOptionSelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(shops) { shop ->
                ShopCard(
                    shop = shop,
                    onBookClick = { onBookShop(shop.id) },
                    onCardClick = { onOpenShopProfile(shop.id) }
                )
            }
        }
    }
}

@Composable
private fun SortOptionsRow(
    currentSortBy: String,
    onSortOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val sortOptions = listOf(
            Pair("nearest", "الأقرب"),
            Pair("rating", "الأعلى تقييماً"),
            Pair("newest", "الأحدث")
        )
        sortOptions.forEach { (sortKey, arabicLabel) ->
            val isSelected = currentSortBy == sortKey
            FilterChip(
                selected = isSelected,
                onClick = { onSortOptionSelected(sortKey) },
                label = { Text(text = arabicLabel) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = KoupaTeal,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun ShopCard(
    shop: BarberShop,
    onBookClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = shop.profilePhotoUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = shop.shopName,
                    style = MaterialTheme.typography.titleSmall,
                    color = KoupaDarkSlate,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = shop.address ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = KoupaDarkSlate.copy(alpha = 0.7f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = KoupaGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", shop.averageRating ?: 0.0),
                            style = MaterialTheme.typography.bodySmall,
                            color = KoupaDarkSlate
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onBookClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KoupaTeal,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "احجز الآن",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
