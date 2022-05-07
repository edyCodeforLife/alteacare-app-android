package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InformationCentral (
val contentResponse: Content?,
val contentId: String?,
val title: String?,
val type: String?
) : Parcelable