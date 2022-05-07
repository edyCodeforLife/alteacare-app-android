package id.altea.care.view.specialistsearch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpecialistFilterDayModel(
    val dayLocale: String, // day locale based on locale phone ex: MINGGU
    val dayServer: String, // day server will be english day ex: SUNDAY
    val date: String, // it will be 2020-01-01
    var isSelected: Boolean = false
) : SpecialistFilterModel(), Parcelable
