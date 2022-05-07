package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethod(
    val code: String?,
    val description: String?,
    val icon: String?,
    val name: String?,
    val provider: String?
): Parcelable
