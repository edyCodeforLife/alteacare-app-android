package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultFilter (var id : String?, var text : String?,var  type : String?) : Parcelable