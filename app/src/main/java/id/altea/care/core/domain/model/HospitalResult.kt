package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.view.specialistsearch.model.SpecialistFilterHospitalModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HospitalResult(
    val address: String?,
    val hospitalId: String?,
    val icon: IconImage?,
    val image: IconImage?,
    val latitude: String?,
    val longitude: String?,
    val name: String?,
    val phone: String?
) : Parcelable {
    companion object {
        fun mapToHospitalFilterModel(data: HospitalResult): SpecialistFilterHospitalModel {
            return SpecialistFilterHospitalModel(
                data.name.orEmpty(),
                data.hospitalId.orEmpty(),
                false
            )
        }
    }
}
