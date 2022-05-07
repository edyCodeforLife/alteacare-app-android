package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    val expiredAt: String?,
    val redirectUrl: String?,
    val refId: String?,
    val token: String?,
    val total: Int?,
    val type: String?
): Parcelable
