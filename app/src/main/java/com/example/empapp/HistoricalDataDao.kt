import androidx.room.*

@Dao
interface HistoricalDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalData(data: HistoricalData): Long

    @Query("SELECT * FROM historical_data WHERE assetId = :assetId ORDER BY date DESC")
    suspend fun getHistoricalDataForAsset(assetId: Int): List<HistoricalData>
}
