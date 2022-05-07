package id.altea.care.core.domain.model

import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val id: String?,
    val name: String?
) : SelectedModel() {
    override fun getIdModel(): String {
        return id.orEmpty()
    }

    override fun getTitle(): String {
        return name.orEmpty()
    }
}
