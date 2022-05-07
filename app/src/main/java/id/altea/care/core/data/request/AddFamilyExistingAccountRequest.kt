package id.altea.care.core.data.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class AddFamilyExistingAccountRequest(
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone")
    val phone: String?
)