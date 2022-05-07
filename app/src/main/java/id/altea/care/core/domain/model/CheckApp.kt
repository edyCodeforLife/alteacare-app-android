package id.altea.care.core.domain.model


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckApp (
    val isUpdateRequired: Boolean?,
    val latestResponse: Latest?
) : Parcelable