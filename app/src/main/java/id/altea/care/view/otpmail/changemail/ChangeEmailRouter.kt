package id.altea.care.view.otpmail.changemail

import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.EmailOtpType

class ChangeEmailRouter {

    fun openEmailOtp(source: AppCompatActivity,oldEmail : String, newEmail: String, token: String) {
        source.startActivity(
            EmailOtpActivity.createIntent(
                source,
                EmailOtpType.ACCOUNT,
                oldEmail,
                newEmail,
                token
            )
        )
        source.finish()
    }
}