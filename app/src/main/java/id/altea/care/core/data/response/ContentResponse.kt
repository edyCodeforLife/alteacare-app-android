package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Content

@Keep
data class ContentResponse(
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone")
    val phone: String?
){
    fun toContent() : Content{
        return Content(email,phone)
    }
}