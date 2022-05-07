package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Insurance

@Keep
data class InsuranceResponse(
        @SerializedName("insurance_company_id")
        val insuranceCompanyId: String?,
        @SerializedName("insurance_company_name")
        val insuranceCompanyName: String?,
        @SerializedName("insurance_number")
        val insuranceNumber: String?,
        @SerializedName("insurance_plafon_group")
        val insurancePlafonGroup: String?
) {
    fun toInsurance(): Insurance {
        return Insurance(
                insuranceCompanyId,
                insuranceCompanyName,
                insuranceNumber,
                insurancePlafonGroup
        )
    }
}