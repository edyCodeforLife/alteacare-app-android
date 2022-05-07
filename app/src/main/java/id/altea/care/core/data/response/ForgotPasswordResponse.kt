package id.altea.care.core.data.response

import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.ForgotPassword

data class ForgotPasswordResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("username")
    val username: String? = null
) {

    fun toForgotPassword(): ForgotPassword {
        return ForgotPassword(type, username)
    }

}