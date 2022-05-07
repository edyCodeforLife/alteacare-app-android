package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.AppointmentPatient

@Keep
data class AppointmentPatientResponse(
    @SerializedName("family_relation_type") val familyRelationType: FamilyRelationTypeResponse?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?
) {
    @Keep
    data class FamilyRelationTypeResponse(
        @SerializedName("id") val id: String?,
        @SerializedName("name") val name: String?
    )

    companion object {
        fun mapToDomain(data: PatientConsulDetailResponse?): AppointmentPatient {
            return AppointmentPatient(
                AppointmentPatient.FamilyRelationType(
                    data?.familyRelationType?.id,
                    data?.familyRelationType?.name
                ),
                data?.firstName,
                data?.id,
                data?.lastName,
                data?.name,
                data?.type
            )
        }
    }
}