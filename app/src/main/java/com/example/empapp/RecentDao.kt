package com.example.empapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: Recent)

    @Query("SELECT * FROM recent ORDER BY id DESC LIMIT 5")
    fun getRecents(): Flow<List<Recent>>

    @Query("DELETE FROM recent WHERE assetId = :assetId")
    suspend fun removeAsset(assetId: String)

    @Query("DELETE FROM recent WHERE id NOT IN (SELECT id FROM recent ORDER BY id DESC LIMIT 5)")
    suspend fun maintainRecentLimit()
}
