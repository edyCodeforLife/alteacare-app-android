package id.altea.care.core.data.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import id.altea.care.core.domain.model.CheckUser
import id.altea.care.core.domain.model.Email
import id.altea.care.core.domain.model.Phone

@Keep
data class ResponseCheckUser(

	@field:SerializedName("is_phone_available")
	val isPhoneAvailable: Boolean? = null,

	@field:SerializedName("is_email_available")
	val isEmailAvailable: Boolean? = null,

	@field:SerializedName("phone")
	val responsePhone: ResponsePhone? = null,

	@field:SerializedName("email")
	val responseEmail: ResponseEmail? = null
) {
	fun mapToCheckUser(): CheckUser {
		return CheckUser(
			isEmailAvailable,
			responsePhone?.mapToPhone(),
			responseEmail?.mapToEmail(),
			isPhoneAvailable
		)
	}
}

@Keep
data class ResponseEmail(

	@field:SerializedName("error")
	val error: String? = null,

	@field:SerializedName("suggested_email")
	val suggestedEmail: String? = null,

	@field:SerializedName("is_available")
	val isAvailable: Boolean? = null
) {
	fun mapToEmail(): Email {
		return Email(error, suggestedEmail, isAvailable)
	}
}

@Keep
data class ResponsePhone(

	@field:SerializedName("error")
	val error: String? = null,

	@field:SerializedName("is_available")
	val isAvailable: Boolean? = null
) {
	fun mapToPhone(): Phone {
		return Phone(error, isAvailable)
	}
}
