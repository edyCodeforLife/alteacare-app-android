package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NameValuePairs(
    @SerializedName("data")
    val data: DataSocket?,
    @SerializedName("type")
    val type: String?
)