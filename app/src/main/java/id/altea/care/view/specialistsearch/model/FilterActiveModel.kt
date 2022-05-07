package id.altea.care.view.specialistsearch.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class FilterActiveModel(var view: String) {

    @Parcelize
    data class FilterSpecialist(val idSpecialis: String, var name: String) :
        FilterActiveModel(name), Parcelable

    @Parcelize
    data class FilterHospital(val idHospital: String, var name: String) : FilterActiveModel(name),
        Parcelable

    @Parcelize
    data class FilterPrice(
        val priceType: SpecialistFilterPriceModel.FilterPriceType,
        val name: String
    ) : FilterActiveModel(name), Parcelable

    @Parcelize
    data class FilterDate(
        val dayLocale: String, // day locale based on locale phone ex: MINGGU
        val dayServer: String, // day server will be english day ex: SUNDAY
        val date: String, // it will be 2020-01-01
    ) : FilterActiveModel(dayLocale), Parcelable

}
