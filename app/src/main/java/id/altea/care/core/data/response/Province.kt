package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Province(
    @SerializedName("code")
    val code: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
)