package id.altea.care.view.specialistsearch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpecialistSortModel(
    val title: String,
    val queryParam: String,
    var isSelected: Boolean = false
) : Parcelable
