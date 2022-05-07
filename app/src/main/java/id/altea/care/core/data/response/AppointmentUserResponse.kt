package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Profile

@Keep
data class AppointmentUserResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
) {
    companion object {
        fun toProfile(data: AppointmentUserResponse?): Profile {
            return Profile(
                email = null,
                firstName = data?.name,
                id = data?.id,
                isVerifiedEmail = null,
                isVerifiedPhone = null,
                lastName = data?.name,
                phone = null,
                userAddresses = listOf(),
                userDetails = null,
                userRole = listOf(),
            )
        }
    }
}
