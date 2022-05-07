package id.altea.care.view.changephonenumber

import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.changephonenumber.otpphone.PhoneOtpActivity
import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.EmailOtpType
import id.altea.care.view.smsotp.SmsOtpActivity
import id.altea.care.view.smsotp.SmsOtpType

class ChangePhoneNumberRouter {

    fun openPhoneOtp(source: AppCompatActivity, oldPhoneNumber : String, newPhoneNumber : String) {
        source.startActivity(
            PhoneOtpActivity.createIntent(source,newPhoneNumber,oldPhoneNumber)
        )
        source.finish()
    }

    fun openSmsOtp(source: AppCompatActivity, oldPhoneNumber : String, newPhoneNumber : String, token: String) {
        source.startActivity(
            SmsOtpActivity.createIntent(
                source,
                SmsOtpType.ACCOUNT,
                oldPhoneNumber,
                newPhoneNumber,
                "",
                token
            )
        )
        source.finish()
    }

}