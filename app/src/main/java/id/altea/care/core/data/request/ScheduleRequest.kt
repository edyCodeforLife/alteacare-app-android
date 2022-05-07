package id.altea.care.core.data.request


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ScheduleRequest(
    @SerializedName("code")
    val code : String?,

    val date: String?,
    @SerializedName("time_start")
    val timeStart: String?,
    @SerializedName("time_end")
    val timeEnd: String?
)