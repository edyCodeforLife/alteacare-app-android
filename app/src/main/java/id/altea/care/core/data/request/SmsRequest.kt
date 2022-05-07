package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SmsRequest(@SerializedName("phone") val phone: String)