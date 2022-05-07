package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.Enable

@Keep
data class EnableResponse(
    @SerializedName("chat")
    val chat: Boolean?,
    @SerializedName("video")
    val video: Boolean?,
    @SerializedName("voice")
    val voice: Boolean?
){
    fun toEnable(): Enable{
        return Enable(
            chat,
            video,
            voice
        )
    }
}