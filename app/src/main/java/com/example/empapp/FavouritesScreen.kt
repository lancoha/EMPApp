package com.example.empapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(navController: NavController, assetViewModel: AssetViewModel) {
    val recentAssets by assetViewModel.recentAssets.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recents") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (recentAssets.isEmpty()) {
                Text(
                    text = "No recent assets clicked.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(recentAssets) { recent ->
                        RecentItem(
                            assetName = recent.assetId,
                            onClick = {
                                navController.navigate("charts")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentItem(assetName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = assetName,
            modifier = Modifier.padding(16.dp),
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
        )
    }
}
