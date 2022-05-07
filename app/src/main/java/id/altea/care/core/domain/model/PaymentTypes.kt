package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PaymentTypes(
    val paymentMethods: List<PaymentMethod>?,
    val type: String?
): Parcelable
