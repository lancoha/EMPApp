package com.example.empapp.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular")
data class Pop(
    @PrimaryKey val assetId: String,
    val clickCount: Int = 0
)
