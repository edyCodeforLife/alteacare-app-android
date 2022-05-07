package id.altea.care.view.forgotpassword

import android.content.Context
import android.content.Intent
import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.EmailOtpType
import id.altea.care.view.smsotp.SmsOtpActivity
import id.altea.care.view.smsotp.SmsOtpType

/**
 * Created by trileksono on 10/03/21.
 */
class ForgotPasswordRouter {
    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, ForgotPasswordActivity::class.java)
        }
    }

    fun openEmailOtp(source: ForgotPasswordActivity, email: String) {
        source.startActivity(
            EmailOtpActivity.createIntent(
                source,
                EmailOtpType.FORGOT_PASSWORD,
                email,
                ""
            )
        )
    }

    fun openSmsOtp(source: ForgotPasswordActivity, phoneNumber: String) {
        source.startActivity(
            SmsOtpActivity.createIntent(
                source,
                SmsOtpType.FORGOT_PASSWORD,
                phoneNumber,
                "",
                ""
            )
        )
    }
}
