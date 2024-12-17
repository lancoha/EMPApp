package com.example.empapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDailyDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyData(data: AssetDailyData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDailyData(dataList: List<AssetDailyData>)

    @Query("SELECT * FROM asset_daily_data WHERE assetId = :assetId ORDER BY datetime ASC")
    fun getAllDataForAsset(assetId: String): Flow<List<AssetDailyData>>

    @Query("DELETE FROM asset_daily_data WHERE assetId = :assetId")
    suspend fun deleteAllDailyDataForAsset(assetId: String)
}
