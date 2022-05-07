package id.altea.care.core.domain.usecase

import javax.inject.Inject

/**
 * Created by trileksono on 05/03/21.
 */
class RegisterPasswordValidationUseCase @Inject constructor() {

    companion object {
        private const val CONTAINS_NUMBER_REGEX = ".*\\d.*"
        private const val CONTAINS_LOWER_TEXT_REGEX = ".*[a-z].*"
        private const val CONTAINS_UPPER_TEXT_REGEX = ".*[A-Z].*"
    }

    fun isLengthValid(password: String): Boolean {
        return password.length >= 8
    }

    fun isContainNumber(password: String): Boolean {
        return password.matches(Regex(CONTAINS_NUMBER_REGEX))
    }

    fun isContainLowerText(password: String): Boolean {
        return password.matches(Regex(CONTAINS_LOWER_TEXT_REGEX))
    }

    fun isContainUpperText(password: String): Boolean {
        return password.matches(Regex(CONTAINS_UPPER_TEXT_REGEX))
    }
}
