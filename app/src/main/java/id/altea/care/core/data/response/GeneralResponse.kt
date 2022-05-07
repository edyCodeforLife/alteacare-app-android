package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.General

@Keep
data class GeneralResponse(
    @SerializedName("data")
    val data: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
){
    fun toGeneral() : General {
        return General(
            data,message,status
        )
    }
}