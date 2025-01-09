package com.example.empapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular")
data class Popular(
    @PrimaryKey val assetId: String,
    val clickCount: Int = 0
)