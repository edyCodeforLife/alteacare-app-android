package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsultationUser(
    val id: String?,
    val name: String?,
    val firstName: String?,
    val lastName: String?,
    val birthdate: String?,
    val gender: String?,
    val phoneNumber: String?,
    val email: String?,
    val address: String?,
    val addressRaw: List<UserAddress>?,
    val cardId: String?,
    val age: Age?
) : Parcelable
