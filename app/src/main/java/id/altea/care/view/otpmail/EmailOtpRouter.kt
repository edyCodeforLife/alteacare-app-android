package id.altea.care.view.otpmail

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.otpmail.changemail.ChangeEmailActivity
import id.altea.care.view.password.PasswordActivity
import id.altea.care.view.password.PasswordType
import id.altea.care.view.register.termcondition.TermConditionActivity
import id.altea.care.view.register.transition.SuccessRegisterActivity
import id.altea.care.view.register.transition.SuccessRegisterRouter
import id.altea.care.view.smsotp.SmsOtpActivity
import id.altea.care.view.smsotp.SmsOtpType

class EmailOtpRouter {

    fun openChangeEmail(
        source: EmailOtpActivity,
        email: String,
        tokenRegister: String,
        activityResult: ActivityResultLauncher<Intent>
    ) {
        activityResult.launch(
            ChangeEmailActivity.createIntent(source, email, tokenRegister)
        )
    }

    fun openPassword(source: EmailOtpActivity, registerInfo: RegisterInfo, token: String) {
        source.startActivity(
            PasswordActivity.createIntent(
                source,
                PasswordType.FORGOT_PASSWORD,
                registerInfo,
                token
            )
        )
    }

    fun openSmsOtp(source: EmailOtpActivity, phoneNumber: String, token: String) {
        source.startActivity(
            SmsOtpActivity.createIntent(
                source, SmsOtpType.REGISTER, phoneNumber, "","", token
            )
        )
    }

    fun openSuccessRegister(source: EmailOtpActivity, token: String) {
        source.startActivity(SuccessRegisterActivity.createIntent(source, token))
        source.finish()
    }
}

enum class EmailOtpType {
    REGISTER, FORGOT_PASSWORD, ACCOUNT
}
