// Necessary Imports
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import com.example.empapp.ui.theme.EMPAppTheme

// Data Model
data class Coin(val name: String, val symbol: String)

// List of Coins
val coinsList = listOf(
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

// Fetch Current Price Function
suspend fun fetchCurrentPrice(stock: String): Float? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://finance.yahoo.com/quote/${stock}-USD"
            val doc = Jsoup.connect(url).get()
            val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()
            priceText?.replace(",", "")?.toFloatOrNull() // Remove commas for numbers like "34,000"
        } catch (e: Exception) {
            Log.e("AllCoinsScreen", "Error fetching current price for $stock", e)
            null
        }
    }
}

// Updated AllCoinsScreen Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCoinsScreen() {
    // State to hold prices map
    var prices by remember { mutableStateOf<Map<String, Float?>>(emptyMap()) }

    // Launch coroutine to fetch prices
    LaunchedEffect(Unit) {
        val fetchedPrices = coinsList.map { coin ->
            async {
                coin.symbol to fetchCurrentPrice(coin.symbol)
            }
        }.awaitAll().toMap()
        prices = fetchedPrices
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Coins") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6AA9FC))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp) // Added horizontal padding for better UI
        ) {
            items(coinsList) { coin ->
                CoinItem(
                    name = coin.name,
                    symbol = coin.symbol,
                    price = prices[coin.symbol]
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Updated CoinItem Composable
@Composable
fun CoinItem(name: String, symbol: String, price: Float?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
            .clickable { /* Handle click if needed */ }
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
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = symbol,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        when {
            price == null -> {
                // Display loading indicator
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Normal
                )
            }
            price == -1f -> {
                // Display error state
                Text(
                    text = "N/A",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red,
                    fontWeight = FontWeight.Normal
                )
            }
            else -> {
                // Display fetched price
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

// Preview Composable
@Preview(showBackground = true)
@Composable
fun AllCoinsScreenPreview() {
    EMPAppTheme {
        AllCoinsScreen()
    }
}
