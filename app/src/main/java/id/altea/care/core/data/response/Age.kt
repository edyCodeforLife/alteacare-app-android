package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Age(
    @SerializedName("month")
    val month: Int?,
    @SerializedName("year")
    val year: Int?
)