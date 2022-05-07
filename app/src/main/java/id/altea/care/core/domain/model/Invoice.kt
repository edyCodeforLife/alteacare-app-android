package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Invoice(
    val originalName: String?,
    val url : String?
) : Parcelable
