package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.ConsultationUser

@Keep
data class ConsultationUserResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("birthdate") val birthdate: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("address_raw") val addressRaw: List<UserAddressResponse>?,
    @SerializedName("card_id") val cardId: String?,
    @SerializedName("age") val age: AgeResponse?
) {
    companion object {
        fun toConsultationUser(consultationFee: ConsultationUserResponse?): ConsultationUser {
            return ConsultationUser(
                consultationFee?.id,
                consultationFee?.name,
                consultationFee?.firstName,
                consultationFee?.lastName,
                consultationFee?.birthdate,
                consultationFee?.gender,
                consultationFee?.phoneNumber,
                consultationFee?.email,
                consultationFee?.address,
                consultationFee?.addressRaw?.map { it.toAddress() },
                consultationFee?.cardId,
                id.altea.care.core.domain.model.Age(
                    try {
                        consultationFee?.age?.month?.toInt()
                    } catch (e: Exception) {
                        0
                    }, try {
                        consultationFee?.age?.year?.toInt()
                    } catch (e: Exception) {
                        0
                    }
                )
            )
        }
    }
}