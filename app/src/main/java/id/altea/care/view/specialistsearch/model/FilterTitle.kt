package id.altea.care.view.specialistsearch.model

data class FilterTitle(
    val title: String,
    val titleType: TitleType,
    val visibleShowAll: Boolean = false
)

enum class TitleType {
    SPECIALIST, LOCATION, HOSPITAL, PRICE, DAY
}