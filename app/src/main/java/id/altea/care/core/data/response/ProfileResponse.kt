package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.*
import id.altea.care.core.domain.model.Age

@Keep
data class ProfileResponse(
    @SerializedName("email")
    val email: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("is_verified_email")
    val isVerifiedEmail: Boolean?,
    @SerializedName("is_verified_phone")
    val isVerifiedPhone: Boolean?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("user_addresses")
    val userAddresses: List<UserAddressResponse>?,
    @SerializedName("user_details")
    val userDetails: UserDetailsResponse?,
    @SerializedName("user_role")
    val userRole: List<String>?,
    @SerializedName("default_patient_data")
    val defaultPatientData : DefaultPatientDataResponse?
) {
    companion object {
        fun mapToProfile(profileResponse: ProfileResponse, auth: Auth? = null): Profile {
            return Profile(
                email = profileResponse.email,
                firstName = profileResponse.firstName,
                id = profileResponse.id,
                isVerifiedEmail = profileResponse.isVerifiedEmail,
                isVerifiedPhone = profileResponse.isVerifiedPhone,
                lastName = profileResponse.lastName,
                phone = profileResponse.phone,
                userAddresses = profileResponse.userAddresses?.map { it.toAddress() },
                userDetails = UserDetailsResponse.mapToUserDetail(profileResponse.userDetails),
                userRole = profileResponse.userRole,
                loginAt = auth?.loginAt,
                defaultPatientData = profileResponse.defaultPatientData?.toDefaultPatientData()
            )
        }
    }
}

@Keep
data class UserDetailsResponse(
    @SerializedName("age")
    val age: AgeResponse?,
    @SerializedName("avatar")
    val avatar: IconResponse,
    @SerializedName("birth_date")
    val birthDate: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("id_card")
    val idCard: String?,
    @SerializedName("photo_id_card")
    val photoIdCard: PhotoIdCard?
) {
    companion object {
        fun mapToUserDetail(data: UserDetailsResponse?): UserDetails {
            return UserDetails(
                age = data?.age?.toAge(),
                avatar = IconResponse.toIconImage(data?.avatar),
                birthDate = data?.birthDate,
                gender = data?.gender,
                idCard = data?.idCard,
                photoIdCard = data?.photoIdCard?.formats?.small
            )
        }
    }
}

@Keep
data class AgeResponse(
    @SerializedName("month")
    val month: String?,
    @SerializedName("year")
    val year: String?
) {
    fun toAge(): Age {
        return Age(
            try {
                month?.toInt()
            } catch (e: Exception) {
                0
            }, try {
                year?.toInt()
            } catch (e: Exception) {
                0
            }
        )
    }
}