package id.altea.care.view.specialistsearch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpecialistFilterSpecializationModel(
    val idSpecialist: String,
    val name: String,
    var isSelected: Boolean,
    var isPopular: Boolean = false,
    var isChild: Boolean = false,
    val subSpecialist: List<SpecialistFilterSpecializationModel> = listOf()
) : SpecialistFilterModel(), Parcelable
