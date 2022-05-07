package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class ConsulMedicalResume(
    val additionalResume: String?,
    val consultation: String?,
    val diagnosis: String?,
    val drugResume: String?,
    val files: @RawValue List<FilesMedicalResume>?,
    val id: Int?,
    val symptom: String?,
    val notes : String?
): Parcelable