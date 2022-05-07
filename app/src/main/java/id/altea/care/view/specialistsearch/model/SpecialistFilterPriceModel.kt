package id.altea.care.view.specialistsearch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpecialistFilterPriceModel(
    var isSelected: Boolean = false,
    var content: String,
    var filterPriceType: FilterPriceType
) : SpecialistFilterModel(), Parcelable {
    sealed class FilterPriceType : Parcelable{
        @Parcelize
        object LowerThan150 : FilterPriceType()
        @Parcelize
        object GreaterThan300 : FilterPriceType()
        @Parcelize
        object Range150And300 : FilterPriceType()
    }
}

