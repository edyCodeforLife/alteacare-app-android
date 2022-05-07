package id.altea.care.core.helper.validator

class LastNameValidator {
    companion object{
        fun isLastNameValidatorValid(lastName : String) : Boolean {
            return lastName.length >= 2 || lastName.isEmpty()
        }
    }
}