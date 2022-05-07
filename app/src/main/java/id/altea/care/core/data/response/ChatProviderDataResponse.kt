package id.altea.care.core.data.response


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import id.altea.care.core.domain.model.ChatProviderData

@Keep
data class ChatProviderDataResponse(
    @SerializedName("twilio")
    val twilio: TwilioResponse?
){

    fun toChatProviderData() : ChatProviderData{
        return ChatProviderData(twilio?.toTwillio())
    }

}