package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.TypeMessage

@Keep
data class TypeMessageResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?
){
    fun toTypeMessage() : TypeMessage {
        return  TypeMessage(
            id,
            name
        )
    }
}