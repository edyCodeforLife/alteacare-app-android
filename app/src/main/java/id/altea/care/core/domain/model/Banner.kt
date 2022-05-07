package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Banner(
val bannerId: String?,
val deeplinkTypeAndroid: String?,
val deeplinkTypeIos: String?,
val deeplinkUrlAndroid: String?,
val needLogin: Boolean? = false,
val deeplinkUrlIos: String?,
val description: String?,
val imageDesktop: String?,
val imageMobile: String?,
val title: String?,
val urlWeb: String?
) : Parcelable