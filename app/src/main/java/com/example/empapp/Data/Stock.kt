package com.example.empapp.Data

data class Stock(
    val name: String,
    val background: androidx.compose.ui.graphics.Color,
    val symbol: String,
    val price: Float? = null
)
