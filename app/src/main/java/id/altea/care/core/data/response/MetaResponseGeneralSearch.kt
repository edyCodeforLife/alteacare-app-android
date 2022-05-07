package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MetaResponseGeneralSearch(
    @SerializedName("total_doctor")
    val totalDoctor: Int,
    @SerializedName("total_symtom")
    val totalSymptom: Int,
    @SerializedName("total_specialization")
    val totalSpecialization: Int,
)
