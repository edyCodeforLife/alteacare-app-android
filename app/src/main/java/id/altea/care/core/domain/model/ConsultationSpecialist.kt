package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class ConsultationSpecialist(
    val id: String?,
    val name: String?
) : Parcelable