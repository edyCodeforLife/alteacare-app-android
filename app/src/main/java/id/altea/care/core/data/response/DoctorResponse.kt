package id.altea.care.core.data.response

import androidx.annotation.Keep
import androidx.core.text.HtmlCompat
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.domain.model.Price

@Keep
data class DoctorResponse(
    @SerializedName("about") val about: String?,
    @SerializedName("doctor_id") val doctorId: String?,
    @SerializedName("experience") val experience: String?,
    @SerializedName("hospital") val hospital: List<HospitalResponse>?,
    @SerializedName("name") val name: String?,
    @SerializedName("photo") val photo: IconResponse?,
    @SerializedName("price") val price: PriceResponse?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("sip") val sip: String?,
    @SerializedName("specialization") val specialization: SpecializationDoctorResponse?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("is_popular") val isPopular: Boolean?,
    @SerializedName("original_price") val originalPrice: PriceResponse?,
    @SerializedName("flat_price") val flatPrice: PriceResponse?,
    @SerializedName("available_day_all_hospital") val availableDay: List<String>?,
    @SerializedName("is_available") val isAvailable: Boolean?

) {

    fun toDoctor(): Doctor {
        return Doctor(
            HtmlCompat.fromHtml(about ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
            about,
            doctorId,
            experience,
            hospital?.map { HospitalResponse.toHospital(it) } ?: emptyList(),
            name,
            photo?.formats?.small,
            overview,
            sip,
            specialization?.toSpecialization(),
            Price.mapToModel(price),
            Price.mapToModel(flatPrice),
            Price.mapToModel(originalPrice),
            slug,
            isPopular,
            availableDay,
            isAvailable
        )
    }
}
