import androidx.room.*

@Dao
interface AssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: Asset): Long // Vračanje ID vstavljene vrstice

    @Query("SELECT * FROM assets WHERE isFavorite = 1")
    suspend fun getFavoriteAssets(): List<Asset>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Int): Asset?

    @Update
    suspend fun updateAsset(asset: Asset): Int // Vračanje števila posodobljenih vrstic

    @Delete
    suspend fun deleteAsset(asset: Asset): Int // Vračanje števila izbrisanih vrstic
}
