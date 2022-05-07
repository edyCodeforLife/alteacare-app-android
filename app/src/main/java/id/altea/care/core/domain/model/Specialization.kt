package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.view.specialistsearch.model.SpecialistFilterSpecializationModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Specialization(
    val description: String?,
    val icon: IconImage?,
    val name: String?,
    val isPopular: Boolean?,
    val specializationId: String?,
    val subSpecialization: List<SubSpecialization>
) : Parcelable {

    companion object {
        fun toSpecializationFilterModel(data: Specialization): SpecialistFilterSpecializationModel {
            return SpecialistFilterSpecializationModel(
                data.specializationId ?: "",
                data.name ?: "",
                false,
                data.isPopular == true,
                false,
                data.subSpecialization.map { SubSpecialization.toFilterSpecializationModel(it) }
            )
        }
    }
}
