package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.GeneralSearch

@Keep
data class GeneralSearchResponse(
    @SerializedName("doctor") val doctor: List<DoctorResponse>?,
    @SerializedName("specialization") val specialization: List<SpecializationResponse>?,
    @SerializedName("symtom") val symtom: List<SymtomResponse>?
) {
    fun toGeneralSearch(meta : MetaResponseGeneralSearch?): GeneralSearch {
        return GeneralSearch(
            doctor?.map { it.toDoctor() }.orEmpty(),
            specialization?.map { it.toSpecialization() }.orEmpty(),
            symtom?.map { it.toSymtom() }.orEmpty(),
            GeneralSearch.Meta(
                totalDoctor = meta?.totalDoctor ?: 0,
                totalSpecialization = meta?.totalSpecialization ?: 0,
                totalSymptom = meta?.totalSymptom ?: 0
            )
        )
    }
}
