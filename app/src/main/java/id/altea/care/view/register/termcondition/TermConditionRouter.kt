package id.altea.care.view.register.termcondition

import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.EmailOtpType
import id.altea.care.view.smsotp.SmsOtpActivity
import id.altea.care.view.smsotp.SmsOtpType

/**
 * Created by trileksono on 08/03/21.
 */
class TermConditionRouter {

    fun openEmailOtp(source: TermConditionActivity, email: String, token: String) {
        source.startActivity(
            EmailOtpActivity.createIntent(
                source, EmailOtpType.REGISTER, email,"", token
            )
        )
    }

    fun openSmsOtp(source: TermConditionActivity, phoneNumber: String, email: String, token: String?) {
        source.startActivity(
            SmsOtpActivity.createIntent(
                source, SmsOtpType.REGISTER, phoneNumber, "",email, token
            )
        )
    }

}