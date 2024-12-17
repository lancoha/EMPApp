package com.example.empapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Asset::class, AssetDailyData::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun assetDailyDataDao(): AssetDailyDataDao
}
