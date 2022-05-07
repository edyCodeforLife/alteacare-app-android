package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

data class ChatProviderData(
    val twilio: Twilio?
)