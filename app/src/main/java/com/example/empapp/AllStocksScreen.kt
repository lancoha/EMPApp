import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.empapp.ui.theme.EMPAppTheme

data class Stock(val name: String, val symbol: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllStocksScreen() {
    val stocks = listOf(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Stocks") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB0FFB0))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(stocks) { stock ->
                StockItem(
                    name = stock.name,
                    symbol = stock.symbol,
                    backgroundColor = Color(0xFFB0FFB0)
                ) {

                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StockItem(name: String, symbol: String, backgroundColor: Color, onClick: () -> Unit) {
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

        Text(
            text = "API Data",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AllStocksScreenPreview() {
    EMPAppTheme {
        AllStocksScreen()
    }
}