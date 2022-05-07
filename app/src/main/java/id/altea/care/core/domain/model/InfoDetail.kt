package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoDetail(val name : String?,
                      val specialist : String?,
                      val schedule : String?) : Parcelable