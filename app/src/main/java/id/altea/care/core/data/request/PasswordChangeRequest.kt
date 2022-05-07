package id.altea.care.core.data.request

import com.google.gson.annotations.SerializedName

data class PasswordChangeRequest (
    @SerializedName("password") val password : String?,
    @SerializedName("password_confirmation") val password_confirmation : String?
)