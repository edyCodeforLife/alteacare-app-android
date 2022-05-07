package id.altea.care.view.specialistsearch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpecialistFilterHospitalModel(
    val name: String,
    val idHospital: String,
    var isSelected: Boolean = false
) : SpecialistFilterModel(), Parcelable
