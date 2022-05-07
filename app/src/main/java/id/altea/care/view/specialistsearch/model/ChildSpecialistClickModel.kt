package id.altea.care.view.specialistsearch.model

data class ChildSpecialistClickModel(
    val parentSpecialistId: String,
    val parentSpecialistName: String,
    val childSpecialistId: String,
    val childSpecialistName: String,
    val isChecked: Boolean
)
