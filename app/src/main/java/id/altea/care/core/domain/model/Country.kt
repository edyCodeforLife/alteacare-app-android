package id.altea.care.core.domain.model

import kotlinx.android.parcel.Parcelize


@Parcelize
data class Country(
    val code: String?,
    val countryId: String?,
    val name: String?,
) : SelectedModel() {

    override fun getIdModel(): String {
        return countryId.orEmpty()
    }

    override fun getTitle(): String {
        return name.orEmpty()
    }
}
