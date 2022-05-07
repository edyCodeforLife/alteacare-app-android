package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Hospital(
    val icon: String?,
    val id: String?,
    val name: String?
) : Parcelable
