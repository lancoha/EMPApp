package com.example.empapp

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "asset_daily_data",
    foreignKeys = [
        ForeignKey(
            entity = Asset::class,
            parentColumns = ["id"],
            childColumns = ["assetId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AssetDailyData(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val assetId: String,
    val datetime: String,
    val close: Double
)
