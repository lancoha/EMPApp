// StocksSection.kt
package com.example.empapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CandlestickChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.empapp.Data.Stock
import com.example.empapp.ui.theme.BlueStart
import com.example.empapp.ui.theme.GreenStart
import com.example.empapp.ui.theme.OrangeStart
import com.example.empapp.ui.theme.PurpleStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import android.util.Log
import androidx.navigation.NavController
import kotlinx.coroutines.launch

val stockList = listOf(
    Stock(
        name = "Tesla\nTSLA",
        background = GreenStart,
        symbol = "TSLA"
    ),
    Stock(
        name = "Nvidia\nNVDA",
        background = PurpleStart,
        symbol = "NVDA"
    ),
    Stock(
        name = "Apple\nAAPL",
        background = OrangeStart,
        symbol = "AAPL"
    ),
    Stock(
        name = "Netflix\nNFLX",
        background = BlueStart,
        symbol = "NFLX"
    )
)

@Composable
fun StocksSection(navController: NavController, assetViewModel: AssetViewModel) {
    var prices by remember { mutableStateOf<Map<String, Float?>>(emptyMap()) }

    val coroutineScope = rememberCoroutineScope()

    suspend fun fetchCurrentPrice(stockSymbol: String): Float? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://finance.yahoo.com/quote/${stockSymbol}"
                val doc = Jsoup.connect(url).get()
                val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()
                priceText?.replace(",", "")?.toFloatOrNull() // Remove commas for numbers like "34,000"
            } catch (e: Exception) {
                Log.e("StocksSection", "Error fetching current price for $stockSymbol", e)
                null
            }
        }
    }

    LaunchedEffect(Unit) {
        val fetchedPrices = stockList.map { stock ->
            coroutineScope.async {
                val symbol = stock.name.split("\n").getOrNull(1) ?: ""
                if (symbol.isNotEmpty()) {
                    symbol to fetchCurrentPrice(symbol)
                } else {
                    symbol to null
                }
            }
        }.awaitAll().toMap()
        prices = fetchedPrices
    }

    Column {
        Text(
            text = "Stocks",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow(
        ) {
            items(stockList) { stock ->
                val symbol = stock.name.split("\n").getOrNull(1) ?: ""
                StockItem(
                    stock = stock,
                    price = prices[symbol],
                    onClick = {
                        coroutineScope.launch {
                            assetViewModel.addRecent(stock.symbol)
                        }
                        MainActivity.GlobalVariables.ChartSymbol = stock.symbol
                        navController.navigate("charts")
                    }
                )
            }
        }
    }
}

@Composable
fun StockItem(
    stock: Stock,
    price: Float?,
    onClick: () -> Unit
) {
    val isLastItem = stockList.last() == stock
    val endPadding = if (isLastItem) 16.dp else 0.dp

    Box(modifier = Modifier.padding(start = 16.dp, end = endPadding)) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .size(120.dp)
                .clickable { onClick() }
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stock.name,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                if (price != null) {
                    Text(
                        text = "$${"%.2f".format(price)}",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = "Loading...",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(stock.background)
                    .padding(6.dp)
            ) {
            }
        }
    }
}
