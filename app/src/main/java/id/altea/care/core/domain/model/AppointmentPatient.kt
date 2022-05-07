package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppointmentPatient(

    val familyRelationType: FamilyRelationType?,
    val firstName: String?,
    val id: String?,
    val lastName: String?,
    val name: String?,
    val type: String?
) : Parcelable {

    @Parcelize
    data class FamilyRelationType(
        val id: String?,
        val name: String?
    ) : Parcelable
}
