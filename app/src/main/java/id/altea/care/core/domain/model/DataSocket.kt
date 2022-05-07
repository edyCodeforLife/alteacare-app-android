package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DataSocket(
    @SerializedName("nameValuePairs")
    val nameValuePairs: NameOfSocket?
)