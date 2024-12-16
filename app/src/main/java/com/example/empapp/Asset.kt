import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val isFavorite: Boolean
)
