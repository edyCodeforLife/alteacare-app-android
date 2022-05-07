package id.altea.care.core.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import id.altea.care.core.data.response.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PatientCosulDetail(
    val address: String?,
    val addressRaw: List<AddressRaw>?,
    val age: Age?,
    val avatar: String?,
    val birthdate: String?,
    val cardId: String?,
    val cardPhoto: String?,
    val cardType: String?,
    val email: String?,
    val familyRelationType: FamilyRelationType?,
    val firstName: String?,
    val gender: String?,
    val id: String?,
    val insurance: Insurance?,
    val lastName: String?,
    val name: String?,
    val phoneNumber: String?,
    val type: String?
) : Parcelable