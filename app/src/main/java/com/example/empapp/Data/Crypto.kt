package com.example.empapp.Data

data class Crypto(
    val name: String,
    val background: androidx.compose.ui.graphics.Color,
    val symbol: String,
    val price: Float? = null
)
