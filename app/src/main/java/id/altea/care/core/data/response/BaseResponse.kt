package id.altea.care.core.data.response

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("status")
    val status: Boolean? = null,
    @SerializedName("message")
    val message: String? = null
)
