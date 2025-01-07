package com.example.empapp

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Asset::class, AssetDailyData::class, Recent::class, Popular::class],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun assetDailyDataDao(): AssetDailyDataDao
    abstract fun recentDao(): RecentDao
    abstract fun popularDao(): PopularDao
}
