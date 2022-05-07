package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class InvoiceResponse(
    @SerializedName("originalName") val originalName: String?,
    @SerializedName("url") val url : String?
)
