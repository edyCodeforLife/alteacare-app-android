package id.altea.care.core.helper.validator

/**
 * Created by trileksono on 12/03/21.
 */
class PhoneValidator {
    companion object {
        fun isPhoneNumberValid(phoneNumber: String): Boolean {
            return phoneNumber.length > 8
        }
    }
}
