package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FamilyRelationType(
        val id: String?,
        val name: String?
) : Parcelable