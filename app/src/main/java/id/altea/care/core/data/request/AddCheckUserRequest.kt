package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AddCheckUserRequest(
    @SerializedName("email") val email : String,
    @SerializedName("phone") val phone : String
)