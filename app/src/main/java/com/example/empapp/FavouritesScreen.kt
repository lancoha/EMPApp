package com.example.empapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.empapp.Database.AssetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(navController: NavController, assetViewModel: AssetViewModel) {
    val recentAssets by assetViewModel.recentAssets.collectAsState()
    val favouriteAssets by assetViewModel.favouriteAssets.collectAsState(initial = emptyList())

    val allCoins = listOf(
        Coin("Bitcoin", "BTC"),
        Coin("Ethereum", "ETH"),
        Coin("Binance Coin", "BNB"),
        Coin("Cardano", "ADA"),
        Coin("Solana", "SOL"),
        Coin("Ripple", "XRP"),
        Coin("Dogecoin", "DOGE"),
        Coin("Polkadot", "DOT"),
        Coin("Litecoin", "LTC"),
        Coin("Chainlink", "LINK"),
        Coin("Stellar", "XLM"),
        Coin("VeChain", "VET"),
        Coin("TRON", "TRX"),
        Coin("EOS", "EOS"),
        Coin("Monero", "XMR"),
        Coin("Tezos", "XTZ"),
        Coin("IOTA", "MIOTA"),
        Coin("Neo", "NEO"),
        Coin("Dash", "DASH"),
        Coin("Zcash", "ZEC")
    )

    val allStocks = listOf(
        Stock("Apple", "AAPL"),
        Stock("Tesla", "TSLA"),
        Stock("Amazon", "AMZN"),
        Stock("Google", "GOOGL"),
        Stock("Microsoft", "MSFT"),
        Stock("Facebook", "FB"),
        Stock("Netflix", "NFLX"),
        Stock("NVIDIA", "NVDA"),
        Stock("Intel", "INTC"),
        Stock("Cisco", "CSCO"),
        Stock("Adobe", "ADBE"),
        Stock("IBM", "IBM"),
        Stock("Oracle", "ORCL"),
        Stock("Salesforce", "CRM"),
        Stock("PayPal", "PYPL"),
        Stock("Uber", "UBER"),
        Stock("Lyft", "LYFT"),
        Stock("Zoom", "ZM"),
        Stock("Snap", "SNAP"),
        Stock("Spotify", "SPOT")
    )

    val favouriteSymbols = favouriteAssets.map { it.id.replace("/USD", "") }
    val favouriteCoins = allCoins.filter { coin -> favouriteSymbols.contains(coin.symbol) }
    val favouriteStocks = allStocks.filter { stock -> favouriteSymbols.contains(stock.symbol) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recents & Favourites") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Recents",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (recentAssets.isEmpty()) {
                Text(
                    text = "No recent assets clicked.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                recentAssets.forEach { recent ->
                    RecentItem(
                        assetName = recent.assetId,
                        onClick = {
                            navController.navigate("charts")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (favouriteCoins.isEmpty() && favouriteStocks.isEmpty()) {
                Text(
                    text = "No favourite assets.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                if (favouriteCoins.isNotEmpty()) {
                    Text(
                        text = "Favourite Coins",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    favouriteCoins.forEach { coin ->
                        CoinOrStockItem(
                            name = coin.name,
                            symbol = coin.symbol.replace("/USD", ""),
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            price = 4f,
                        ) {
                            scope.launch { assetViewModel.addRecent(coin.symbol) }
                            MainActivity.GlobalVariables.ChartSymbol = "${coin.symbol}/USD"
                            navController.navigate("charts")
                        }
                    }
                    }
                }

                if (favouriteStocks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Favourite Stocks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        favouriteStocks.forEach { stock ->
                            CoinOrStockItem(
                                name = stock.name,
                                symbol = stock.symbol,
                                backgroundColor = Color(0xFFB0FFB0),
                                price = 4f,
                            ) {
                                scope.launch { assetViewModel.addRecent(stock.symbol) }
                                MainActivity.GlobalVariables.ChartSymbol = stock.symbol
                                navController.navigate("charts")
                            }
                        }
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
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
