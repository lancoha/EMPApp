package com.example.empapp

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class AssetRepository(private val db: AppDatabase) {

    companion object {
        @Volatile
        private var INSTANCE: AssetRepository? = null

        fun getInstance(context: Context): AssetRepository {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_local_database"
                ).fallbackToDestructiveMigration().build()

                val instance = AssetRepository(database)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getAllAssets(): Flow<List<Asset>> = db.assetDao().getAllAssets()

    suspend fun insertAsset(asset: Asset) {
        db.assetDao().insertAsset(asset)
    }

    suspend fun updateFavouriteStatus(assetId: String, isFav: Boolean) {
        val asset = db.assetDao().getAssetById(assetId)

        if (asset == null) {
            val newAsset = Asset(id = assetId, isFavourite = isFav)
            db.assetDao().insertAsset(newAsset)
        } else {
            db.assetDao().updateFavouriteStatus(assetId, isFav)
        }

        if (!isFav) {
            db.assetDailyDataDao().deleteAllDailyDataForAsset(assetId)
        }
    }

    fun getDailyDataForAsset(assetId: String): Flow<List<AssetDailyData>> =
        db.assetDailyDataDao().getAllDataForAsset(assetId)

    suspend fun insertDailyData(data: AssetDailyData) {
        db.assetDailyDataDao().insertDailyData(data)
    }

    suspend fun insertAllDailyData(dataList: List<AssetDailyData>) {
        db.assetDailyDataDao().insertAllDailyData(dataList)
    }

    suspend fun deleteAsset(assetId: String) {
        db.assetDao().deleteAsset(assetId)
    }

    suspend fun deleteAllDailyDataForAsset(assetId: String) {
        db.assetDailyDataDao().deleteAllDailyDataForAsset(assetId)
    }

    fun getRecents(): Flow<List<Recent>> = db.recentDao().getRecents()

    suspend fun insertRecent(assetId: String) {
        db.recentDao().removeAsset(assetId)
        db.recentDao().insertRecent(Recent(assetId = assetId))
        db.recentDao().maintainRecentLimit()
    }
}
