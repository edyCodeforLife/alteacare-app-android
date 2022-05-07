package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CheckUser(
    val isEmailAvailable: Boolean? = null,
    val phone: Phone? = null,
    val email: Email? = null,
    val isPhoneAvailable: Boolean? = null
): Parcelable