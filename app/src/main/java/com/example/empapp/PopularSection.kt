package com.example.empapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun PopularSection(
    navController: NavController,
    assetViewModel: AssetViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val popularList by assetViewModel.popularAssets.collectAsState(emptyList())

    Column {
        Text(
            text = "Popular",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(popularList) { popularItem ->
                PopularItem(
                    popularItem = popularItem,
                    onClick = {
                        coroutineScope.launch {
                            assetViewModel.trackClick(popularItem.assetId)
                        }
                        MainActivity.GlobalVariables.ChartSymbol = "${popularItem.assetId}/USD"
                        navController.navigate("charts")
                    }
                )
            }
        }
    }
}

@Composable
fun PopularItem(
    popularItem: Popular,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .size(120.dp)
            .clickable { onClick() }
            .padding(13.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = popularItem.assetId,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        Text(
            text = "Clicks: ${popularItem.clickCount}",
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
