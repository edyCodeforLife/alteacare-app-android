package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.view.specialistsearch.model.SpecialistFilterSpecializationModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubSpecialization(
    val description: String?,
    val icon: IconImage?,
    val name: String?,
    val specializationId: String?
) : Parcelable {

    companion object {
        fun toFilterSpecializationModel(data: SubSpecialization): SpecialistFilterSpecializationModel {
            return SpecialistFilterSpecializationModel(
                data.specializationId.orEmpty(),
                data.name.orEmpty(),
                isSelected = false,
                isPopular = false,
                isChild = true,
                subSpecialist = listOf()
            )
        }
    }
}
