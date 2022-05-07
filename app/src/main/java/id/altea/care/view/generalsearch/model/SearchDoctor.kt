package id.altea.care.view.generalsearch.model

data class SearchDoctor(
    val doctorId: String,
    val doctorName: String,
    val specialist: String,
    val hospitalName: String,
    val hospitalImage: String,
    val doctorImage: String,
    val experience: String,
    val price: String,
)
