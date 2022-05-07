package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.view.generalsearch.model.SearchContent
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataGeneralSearchButtonMore(
    val typeButton: TypeButton? = null,
    val textButton: String? = "",
    val searchContent: List<SearchContent>? = emptyList(),
    val dataDoctor: List<Doctor>? = emptyList(),
) : Parcelable

enum class TypeButton {
    SYMPTOM, SPECIALIST_CATEGORY, DOCTOR
}
