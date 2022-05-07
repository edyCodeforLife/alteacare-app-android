package id.altea.care.core.domain.model

data class GeneralSearch(
    val doctor: List<Doctor>,
    val specialization: List<Specialization>,
    val symtom: List<Symtom>,
    val meta : Meta
) {
    data class Specialization(
        val specializationName: String,
        val specializationId: String,
        val specializationIcon : String
    )

    data class Symtom(
        val symtomName: String,
        val symtomId: String
    )

    data class Meta(
        val totalDoctor : Int,
        val totalSpecialization : Int,
        val totalSymptom : Int
    )
}
