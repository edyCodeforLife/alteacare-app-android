package id.altea.care.core.domain.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep


data class UploadedBy(
    val firstName: String?,
    val lastName: String?,
    val refId: String?
)