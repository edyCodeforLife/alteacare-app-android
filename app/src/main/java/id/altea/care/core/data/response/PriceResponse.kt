package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PriceResponse(
    @SerializedName("formatted") val formatted: String?,
    @SerializedName("raw") val raw: Long?
)
