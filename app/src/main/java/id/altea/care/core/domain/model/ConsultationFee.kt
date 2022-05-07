package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ConsultationFee(
    val amount: Long?,
    val id: Int?,
    val label: String?,
    val status: String?,
    val type: String?,
    val dataId : Int?,
    val category : String?
) : Parcelable
