package com.example.empapp.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PopularDao {
    @Query("SELECT * FROM popular ORDER BY clickCount DESC LIMIT 5")
    fun getTop5Popular(): Flow<List<Popular>>

    @Query("SELECT * FROM popular WHERE assetId = :assetId LIMIT 1")
    suspend fun getPopularById(assetId: String): Popular?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(popular: Popular)
}
