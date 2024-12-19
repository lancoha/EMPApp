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

data class Coin(val name: String, val symbol: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCoinsScreen() {
    val coins = listOf(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Coins") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(coins) { coin ->
                CoinItem(
                    name = coin.name,
                    symbol = coin.symbol,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                ) {

                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CoinItem(name: String, symbol: String, backgroundColor: Color, onClick: () -> Unit) {
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
fun AllCoinsScreenPreview() {
    EMPAppTheme {
        AllCoinsScreen()
    }
}
