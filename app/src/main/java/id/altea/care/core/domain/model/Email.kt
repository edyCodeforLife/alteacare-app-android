package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Email(
    val error: String? = null,
    val suggestedEmail: String? = null,
    val isAvailable: Boolean? = null
): Parcelable