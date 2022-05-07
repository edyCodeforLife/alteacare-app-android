package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NameOfSocket(
    @SerializedName("is_ma_available")
    val isMaAvailable: Boolean?
)