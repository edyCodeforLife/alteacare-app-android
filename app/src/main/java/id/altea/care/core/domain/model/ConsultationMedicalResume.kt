package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsultationMedicalResume(
    val id: String?,
    val url: String?,
    val originalName: String?,
    val size: String?,
    val uploadByUser: Int?,
) : Parcelable
