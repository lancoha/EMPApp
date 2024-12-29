package com.example.empapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.empapp.Data.Crypto
import com.example.empapp.ui.theme.BlueStart
import com.example.empapp.ui.theme.GreenStart
import com.example.empapp.ui.theme.OrangeStart
import com.example.empapp.ui.theme.PurpleStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

val cryptoList = listOf(
    Crypto(
        name = "Bitcoin\nBTC",
        background = OrangeStart,
        symbol = "BTC"
    ),
    Crypto(
        name = "Ethereum\nETH",
        background = BlueStart,
        symbol = "ETH"
    ),
    Crypto(
        name = "Solana\nSOL",
        background = PurpleStart,
        symbol = "SOL"
    ),
    Crypto(
        name = "Dogecoin\nDOGE",
        background = GreenStart,
        symbol = "DOGE"
    )
)

@Preview(showBackground = true)
@Composable
fun CryptoSectionPreview() {
    CryptoSection(
        navController = TODO()
    )
}

@Composable
fun CryptoSection(navController: NavController){
    var prices by remember { mutableStateOf<Map<String, Float?>>(emptyMap()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val fetchedPrices = cryptoList.map { crypto ->
            coroutineScope.async {
                crypto.symbol to fetchCurrentPrice(crypto.symbol)
            }
        }.awaitAll().toMap()
        prices = fetchedPrices
    }

    Column {
        Text(
            text = "Crypto",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cryptoList) { crypto ->
                CryptoItem(
                    crypto = crypto,
                    price = prices[crypto.symbol],
                    onClick = {
                        MainActivity.GlobalVariables.ChartSymbol = "${crypto.symbol}/USD"
                        navController.navigate("charts")
                    }
                )
            }
        }
    }
}


suspend fun fetchCurrentPrice(stock: String): Float? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://finance.yahoo.com/quote/${stock}-USD"
            val doc = Jsoup.connect(url).get()
            val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()
            priceText?.replace(",", "")?.toFloatOrNull()
        } catch (e: Exception) {
            Log.e("CryptoSection", "Error fetching current price for $stock", e)
            null
        }
    }
}

@Composable
fun CryptoItem(
    crypto: Crypto,
    price: Float?,
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
        Column {
            Text(
                text = crypto.name,
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
                .background(crypto.background)
                .padding(6.dp)
        ) {
        }
    }
}

