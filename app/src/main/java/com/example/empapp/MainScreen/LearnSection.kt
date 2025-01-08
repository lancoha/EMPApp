package com.example.empapp.MainScreen

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LightbulbCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.empapp.Data.Learn
import com.example.empapp.ui.theme.BlueStart
import com.example.empapp.ui.theme.GreenStart
import com.example.empapp.ui.theme.PurpleStart


val LearnList = listOf(
    Learn(
        icon = Icons.Rounded.LightbulbCircle,
        name = "What is Crypto",
        background = PurpleStart,
        url = "https://www.investopedia.com/terms/c/cryptocurrency.asp"
    ),
    Learn(
        icon = Icons.Rounded.LightbulbCircle,
        name = "What are Stocks",
        background = PurpleStart,
        url = "https://www.investopedia.com/terms/s/stock.asp"
    ),
    Learn(
        icon = Icons.Rounded.LightbulbCircle,
        name = "How to Invest",
        background = GreenStart,
        url = "https://www.investopedia.com/articles/basics/11/3-s-simple-investing.asp"
    ),
    Learn(
        icon = Icons.Rounded.LightbulbCircle,
        name = "What is Bitcoin",
        background = GreenStart,
        url = "https://www.investopedia.com/terms/b/bitcoin.asp"
    ),
    Learn(
        icon = Icons.Rounded.LightbulbCircle,
        name = "What time to invest",
        background = BlueStart,
        url = "https://www.investopedia.com/day-trading/best-time-day-week-month-trade-stocks/"
    ),
    Learn(
        icon = Icons.Rounded.LightbulbCircle,
        name = "Strategies to invest",
        background = BlueStart,
        url = "https://www.investopedia.com/investing/investing-strategies/"
    )
)

@Preview
@Composable
fun LearnSection() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Learn",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        val rows = LearnList.chunked(2)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { learnItem ->
                        LearnItem(
                            learnItem = learnItem,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                    }

                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun LearnItem(learnItem: Learn, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                openUrl(context, learnItem.url)
            }
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = learnItem.name,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Icon(
                imageVector = learnItem.icon,
                contentDescription = learnItem.name,
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .background(learnItem.background, RoundedCornerShape(50))
                    .padding(12.dp)
            )
        }
    }
}


fun openUrl(context: android.content.Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, url.toUri())
}
