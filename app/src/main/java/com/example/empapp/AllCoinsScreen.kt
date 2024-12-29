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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.empapp.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import com.example.empapp.ui.theme.EMPAppTheme


data class Coin(val name: String, val symbol: String)

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

suspend fun fetchCurrentPrice(stock: String): Float? {
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://finance.yahoo.com/quote/${stock}-USD"
            val doc = Jsoup.connect(url).get()
            val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()
            priceText?.replace(",", "")?.toFloatOrNull()
        } catch (e: Exception) {
            Log.e("AllCoinsScreen", "Error fetching current price for $stock", e)
            null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCoinsScreen(navController: NavController) {
    var prices by remember { mutableStateOf<Map<String, Float?>>(emptyMap()) }

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
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(coinsList) { coin ->
                CoinItem(
                    name = coin.name,
                    symbol = coin.symbol,
                    price = prices[coin.symbol]
                ) {
                    MainActivity.GlobalVariables.ChartSymbol = "${coin.symbol}/USD"
                    navController.navigate("charts")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CoinItem(name: String, symbol: String, price: Float?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
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

@Preview(showBackground = true)
@Composable
fun AllCoinsScreenPreview() {
    EMPAppTheme {
        val mockNavController = rememberNavController()
        AllCoinsScreen(navController = mockNavController)
    }
}
