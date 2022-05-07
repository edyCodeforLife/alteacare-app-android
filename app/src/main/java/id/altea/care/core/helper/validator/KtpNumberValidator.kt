package id.altea.care.core.helper.validator

class KtpNumberValidator {
    companion object{
        fun isKtpNumberValidatorValid(ktpNumber : String) : Boolean {
            return ktpNumber.length >= 10 && ktpNumber.length <= 20
        }
    }
}