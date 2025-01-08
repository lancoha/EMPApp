package com.example.empapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.empapp.Database.AssetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

data class Coin(val name: String, val symbol: String)
data class Stock(val name: String, val symbol: String)

suspend fun fetchCurrent(asset: String): Float? {
    if (asset.contains("/")) {
        return null
    }
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://finance.yahoo.com/quote/$asset"
            val doc = Jsoup.connect(url).get()
            val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()
            priceText?.replace(",", "")?.toFloatOrNull()
        } catch (e: Exception) {
            Log.e("DataFetcher", "Error fetching current price for $asset", e)
            null
        }
    }
}

@Composable
fun ExploreScreen(navController: NavController, assetViewModel: AssetViewModel) {

    val coinsList = listOf(
        Coin("Bitcoin", "BTC"),
        Coin("Ethereum", "ETH"),
        Coin("Solana", "SOL"),
        Coin("DogeCoin", "DOGE"),
        Coin("XRP", "XRP")
    )

    val stocksList = listOf(
        Stock("Tesla", "TSLA"),
        Stock("Apple", "AAPL"),
        Stock("Amazon", "AMZN"),
        Stock("Microsoft", "MSFT"),
        Stock("Google", "GOOGL")
    )

    var coinPrices by remember { mutableStateOf<Map<String, Float?>>(emptyMap()) }
    var stockPrices by remember { mutableStateOf<Map<String, Float?>>(emptyMap()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val fetchedCoinPrices = coinsList.map { coin ->
            async {
                coin.symbol to fetchCurrent(coin.symbol)
            }
        }.awaitAll().toMap()

        val fetchedStockPrices = stocksList.map { stock ->
            async {
                stock.symbol to fetchCurrent(stock.symbol)
            }
        }.awaitAll().toMap()

        coinPrices = fetchedCoinPrices
        stockPrices = fetchedStockPrices
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Coins",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            coinsList.forEach { coin ->
                CoinOrStockItem(
                    name = coin.name,
                    symbol = coin.symbol,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    price = coinPrices[coin.symbol]
                ) {
                    scope.launch {
                        assetViewModel.addRecent(coin.symbol)
                        assetViewModel.trackClick(coin.symbol)
                    }
                    MainActivity.GlobalVariables.ChartSymbol = "${coin.symbol}/USD"
                    navController.navigate("charts")
                }
            }

            Button(
                onClick = { navController.navigate("allCoinsScreen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "See all",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Stocks",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            stocksList.forEach { stock ->
                CoinOrStockItem(
                    name = stock.name,
                    symbol = stock.symbol,
                    backgroundColor = Color(0xFFB0FFB0),
                    price = stockPrices[stock.symbol]
                ) {
                    scope.launch {
                        assetViewModel.addRecent(stock.symbol)
                        assetViewModel.trackClick(stock.symbol)
                    }
                    MainActivity.GlobalVariables.ChartSymbol = stock.symbol
                    navController.navigate("charts")
                }
            }

            Button(
                onClick = { navController.navigate("allStocksScreen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "See all",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}


@Composable
fun CoinOrStockItem(
    name: String,
    symbol: String,
    backgroundColor: Color,
    price: Float?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = symbol,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        when {
            price == null -> {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Normal
                )
            }
            price == -1f -> {
                Text(
                    text = "N/A",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red,
                    fontWeight = FontWeight.Normal
                )
            }
            price == 4f -> {
                Text(
                    text = "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red,
                    fontWeight = FontWeight.Normal
                )
            }
            else -> {
                Text(
                    text = "$${"%.2f".format(price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

