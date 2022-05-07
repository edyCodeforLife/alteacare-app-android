package id.altea.care.core.domain.model

import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubDistrict(
    val geoArea: String?,
    val id: String?,
    val name: String?,
    val postalCode: String?,
) : SelectedModel() {
    override fun getIdModel(): String {
        return id.orEmpty()
    }

    override fun getTitle(): String {
        return name.orEmpty()
    }
}
