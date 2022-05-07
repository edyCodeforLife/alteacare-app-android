package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ConsultationDoctor(
    val id: String?,
    val name: String?,
    val photo: String?,
    val specialist: Specialization?,
    val hospital: Hospital?,
) : Parcelable {

    companion object {
        fun toDoctorModel(data: ConsultationDoctor?): Doctor {
            return Doctor(
                doctorId = data?.id,
                doctorName = data?.name,
                photo = data?.photo,
                specialization = data?.specialist,
                hospital = if (data?.hospital == null) listOf() else listOf(data.hospital)
            )
        }
    }
}
