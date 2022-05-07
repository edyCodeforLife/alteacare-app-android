package id.altea.care.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doctor(
    val about: String? = null,
    val abboutHtml: String? = null,
    val doctorId: String? = null,
    val experience: String? = null,
    val hospital: List<Hospital>? = null,
    val doctorName: String? = null,
    val photo: String? = null,
    val overview: String? = null,
    val sip: String? = null,
    val specialization: Specialization? = null,
    val price: Price? = null,
    val flatPrice: Price? = null,
    val originalPrice: Price? = null,
    val slug: String? = null,
    val isPopular: Boolean? = null,
    val availableDay: List<String>? = null,
    val isAvailable: Boolean? = null
) : Parcelable {

    val id: String
        get() = doctorId ?: ""

    val name: String
        get() = doctorName ?: ""

    val speciality: String
        get() = specialization?.name ?: ""
}
