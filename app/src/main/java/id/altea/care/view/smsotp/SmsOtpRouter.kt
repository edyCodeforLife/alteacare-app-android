package id.altea.care.view.smsotp

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.view.changephonenumber.ChangePhoneNumberActivity
import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.EmailOtpType
import id.altea.care.view.password.PasswordActivity
import id.altea.care.view.password.PasswordType
import id.altea.care.view.register.transition.SuccessRegisterActivity

object SmsOtpRouter {

    fun openSuccessRegister(source: SmsOtpActivity, token: String) {
        source.startActivity(SuccessRegisterActivity.createIntent(source, token))
        source.finish()
    }

    fun openEmailOtp(source: SmsOtpActivity, email: String, phoneNumber: String?, token: String) {
        source.startActivity(
            EmailOtpActivity.createIntent(
                source, EmailOtpType.REGISTER, email,"", phoneNumber, token
            )
        )
    }

    fun openChangePhoneNumber(
        source: SmsOtpActivity,
        phoneNumber: String?,
        tokenRegister: String,
        activityResult: ActivityResultLauncher<Intent>
    ) {
        activityResult.launch(
            ChangePhoneNumberActivity.createIntent(
                source,
                phoneNumber,
                tokenRegister
            )
        )
    }

    fun openPassword(source: SmsOtpActivity, registerInfo: RegisterInfo, token: String) {
        source.startActivity(
            PasswordActivity.createIntent(
                source,
                PasswordType.FORGOT_PASSWORD,
                registerInfo,
                token
            )
        )
    }

}


enum class SmsOtpType {
    REGISTER, FORGOT_PASSWORD, ACCOUNT
}