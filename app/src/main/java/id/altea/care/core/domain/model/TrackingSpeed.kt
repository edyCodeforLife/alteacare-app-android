package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackingSpeed(val speedValue : String?,val speedUnit : String?,val speedRaw : Long ) : Parcelable