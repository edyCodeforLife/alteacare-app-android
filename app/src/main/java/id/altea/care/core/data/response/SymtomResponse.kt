package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.GeneralSearch

@Keep
data class SymtomResponse(
    @SerializedName("name") val name: String?,
    @SerializedName("symtom_id") val symtomId: String?
) {
    fun toSymtom(): GeneralSearch.Symtom {
        return GeneralSearch.Symtom(name.orEmpty(), symtomId.orEmpty())
    }
}
