package com.example.empapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset)

    @Query("SELECT * FROM asset WHERE id = :assetId LIMIT 1")
    fun getAssetById(assetId: String): Flow<Asset?>

    @Query("UPDATE asset SET isFavourite = :isFavourite WHERE id = :assetId")
    suspend fun updateFavouriteStatus(assetId: String, isFavourite: Boolean)

    @Query("SELECT * FROM asset")
    fun getAllAssets(): Flow<List<Asset>>

    @Query("DELETE FROM asset WHERE id = :assetId")
    suspend fun deleteAsset(assetId: String)
}

