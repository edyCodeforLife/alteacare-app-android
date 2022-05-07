package id.altea.care.core.data.response


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.Nationality

@Keep
data class NationalityResponse(
        @SerializedName("code")
        val code: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("name")
        val name: String?
) {
    fun toNationality(): Nationality {
        return Nationality(
            code,
            id,
            name
        )
    }
}