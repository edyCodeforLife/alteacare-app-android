package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class City(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)