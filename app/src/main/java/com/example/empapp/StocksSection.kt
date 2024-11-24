package com.example.empapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CandlestickChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

val stockList = listOf(
    Stock(
        icon = Icons.Rounded.CandlestickChart,
        name = "Tesla\nTSLA",
        background = GreenStart
    ),

    Stock(
        icon = Icons.Rounded.CandlestickChart,
        name = "Nvidia\nNVDA",
        background = PurpleStart
    ),

    Stock(
        icon = Icons.Rounded.CandlestickChart,
        name = "Apple\nAAPL",
        background = OrangeStart
    ),

    Stock(
        icon = Icons.Rounded.CandlestickChart,
        name = "Netflix\nNFLX",
        background = BlueStart
    )
)

@Preview
@Composable
fun StocksSection() {
    Column{
        Text(
            text = "Stocks",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow {
            items(stockList.size) {
                StockItem(it)
            }
        }
    }
}

@Composable
fun StockItem(
    index: Int
) {
    val finance = stockList[index]
    var lastPaddingEnd = 0.dp
    if (index == stockList.size - 1) {
        lastPaddingEnd = 16.dp
    }

    Box(modifier = Modifier.padding(start = 16.dp, end = lastPaddingEnd)) {
        Column(
            modifier = Modifier.clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .size(120.dp)
                .clickable {}
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = finance.name,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Box(
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    .background(finance.background)
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = finance.icon,
                    contentDescription = finance.name,
                    tint = Color.White
                )
            }
        }
    }
}