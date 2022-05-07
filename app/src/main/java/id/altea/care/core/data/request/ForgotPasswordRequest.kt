package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class ForgotPasswordRequest(@SerializedName("username") val username: String)
