import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "historical_data",
    foreignKeys = [
        ForeignKey(
            entity = Asset::class,
            parentColumns = ["id"],
            childColumns = ["assetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["assetId"])] // Dodan indeks za tuji ključ
)
data class HistoricalData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetId: Int,
    val date: String,
    val openPrice: Double,
    val closePrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val volume: Double
)
