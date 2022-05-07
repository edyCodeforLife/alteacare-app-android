package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class RoomInfo(
    @SerializedName("nameValuePairs")
    val nameValuePairs: NameValuePairs?
)