package id.altea.care.core.data.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class MessageRequest(
    @SerializedName("name") val name : String,
    @SerializedName("email") val email : String,
    @SerializedName("phone") val phoneNumber : String,
    @SerializedName("user_id") val userId :String?,
    @SerializedName("message_type") val idMessageType : String,
    @SerializedName("message") val messageContent : String)