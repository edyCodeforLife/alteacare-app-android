package id.altea.care.core.data.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RegisterRequest(
    @SerializedName("birth_date") val birthDate: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("birth_place") val birthPlace: String,
    @SerializedName("birth_country") val birthCountry: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
    @SerializedName("phone") val phone: String
)
