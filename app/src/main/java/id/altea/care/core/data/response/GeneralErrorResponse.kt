package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GeneralErrorResponse(
    @SerializedName("status")
    val status: Boolean? = null,
    @SerializedName("version")
    val version: String? = null,
    @SerializedName("message")
    val message: String? = null
)
