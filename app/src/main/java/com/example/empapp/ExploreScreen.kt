package com.example.empapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.empapp.ui.theme.EMPAppTheme

@Composable
fun ExploreScreen(navController: NavController) {
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
            listOf("Bitcoin" to "BTC", "Ethereum" to "ETH", "Solana" to "SOL", "DogeCoin" to "DOGE", "XRP" to "XRP").forEach { coin ->
                CoinOrStockItem(name = coin.first, symbol = coin.second, backgroundColor = MaterialTheme.colorScheme.primaryContainer) {
                }
            }

            Button(
                onClick = { navController.navigate("allCoinsScreen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("See all",
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
            listOf(
                "Tesla" to "TSLA", "Apple" to "AAPL", "Amazon" to "AMZN", "Microsoft" to "MSFT", "Google" to "GOOGL")
                .forEach { stock ->
                CoinOrStockItem(
                    name = stock.first,
                    symbol = stock.second,
                    backgroundColor = Color(0xFFB0FFB0)
                ) {

                }
            }


            Button(
                onClick = { navController.navigate("allStocksScreen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("See all",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
            }
        }
    }
}

@Composable
fun CoinOrStockItem(name: String, symbol: String, backgroundColor: Color, onClick: () -> Unit) {
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
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenPreview() {
    EMPAppTheme {
        val mockNavController = rememberNavController()
        ExploreScreen(navController = mockNavController)
    }
}
