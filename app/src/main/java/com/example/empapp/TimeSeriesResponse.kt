import com.google.gson.annotations.SerializedName

data class TimeSeriesResponse(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("values")
    val values: List<TimeSeriesValue>,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String?
)

data class Meta(
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("interval")
    val interval: String,
    @SerializedName("currency_base")
    val currencyBase: String?,
    @SerializedName("currency_quote")
    val currencyQuote: String?,
    @SerializedName("exchange")
    val exchange: String?,
    @SerializedName("type")
    val type: String?
)

data class TimeSeriesValue(
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("open")
    val open: String,
    @SerializedName("high")
    val high: String,
    @SerializedName("low")
    val low: String,
    @SerializedName("close")
    val close: String,
    @SerializedName("volume")
    val volume: String?
)
