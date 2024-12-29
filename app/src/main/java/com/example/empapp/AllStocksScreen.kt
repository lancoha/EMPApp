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

data class Stock(val name: String, val symbol: String)

val stocksList = listOf(
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

suspend fun fetchCurrentStock(stock: String): Float? {
    if (stock.contains("/")) {
        return null
    }
    return withContext(Dispatchers.IO) {
        try {
            val url = "https://finance.yahoo.com/quote/$stock"
            val doc = Jsoup.connect(url).get()
            val priceText = doc.select("fin-streamer[data-field=regularMarketPrice]").first()?.text()
            priceText?.replace(",", "")?.toFloatOrNull()
        } catch (e: Exception) {
            Log.e("AllStocksScreen", "Error fetching current price for $stock", e)
            null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllStocksScreen(navController: NavController) {
    var prices by remember { mutableStateOf<Map<String, Float?>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val fetchedPrices = stocksList.map { stock ->
            async {
                stock.symbol to fetchCurrentStock(stock.symbol)
            }
        }.awaitAll().toMap()
        prices = fetchedPrices
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Stocks") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF51F590))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items(stocksList) { stock ->
                StockItem(
                    name = stock.name,
                    symbol = stock.symbol,
                    price = prices[stock.symbol],
                    backgroundColor = Color(0xFFB0FFB0)
                ) {
                    MainActivity.GlobalVariables.ChartSymbol = stock.symbol
                    navController.navigate("charts")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StockItem(
    name: String,
    symbol: String,
    price: Float?,
    backgroundColor: Color,
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
fun AllStocksScreenPreview() {
    EMPAppTheme {
        val mockNavController = rememberNavController()
        AllStocksScreen(navController = mockNavController)
    }
}