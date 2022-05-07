package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppointmentStatusDetail(
    val bgColor: String?,
    val label: String?,
    val textColor: String?
) : Parcelable
