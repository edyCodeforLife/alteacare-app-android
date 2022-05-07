package id.altea.care.view.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.contact.ContactRouter
import id.altea.care.view.forgotpassword.ForgotPasswordRouter
import id.altea.care.view.main.MainActivity
import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.EmailOtpType
import id.altea.care.view.register.contactinfo.RegisterContactRouter
import id.altea.care.view.register.contactinfo.RegisterContactState
import id.altea.care.view.smsotp.SmsOtpActivity
import id.altea.care.view.smsotp.SmsOtpType

/**
 * Created by trileksono on 10/03/21.
 */
class LoginActivityRouter {

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, LoginActivity::class.java)
        }
    }

    fun openRegisterActivity(source: AppCompatActivity) {
        source.startActivity(RegisterContactRouter.createIntent(
            source,
            null,
            null,
            RegisterContactState.PAGE_REGISTER,
            null
        ))
    }

    fun openForgotPassword(source: AppCompatActivity) {
        source.startActivity(ForgotPasswordRouter.createIntent(source))
    }

    fun openContact(source: AppCompatActivity) {
        source.startActivity(ContactRouter.createIntent(source))
    }

    fun openMainActivity(source: AppCompatActivity) {
        source.startActivity(MainActivity.createIntent(source).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        source.finish()
    }

    fun openEmailOtp(source: AppCompatActivity, email: String, token: String) {
        source.startActivity(
            EmailOtpActivity.createIntent(
                source,
                EmailOtpType.REGISTER,
                email,
                "",
                "",
                token = token
            )
        )
    }

    fun openSmsOtp(source: AppCompatActivity, phone: String, token: String) {
        source.startActivity(
            SmsOtpActivity.createIntent(
                source,
                SmsOtpType.REGISTER,
                phone,
                "",
                "",
                token = token
            )
        )
    }

}
