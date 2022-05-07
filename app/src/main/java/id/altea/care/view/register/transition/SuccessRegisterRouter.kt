package id.altea.care.view.register.transition

import android.content.Context
import android.content.Intent
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.EmailOtpType
import id.altea.care.view.register.termcondition.TermConditionActivity

class SuccessRegisterRouter {

    fun openLogin(source: SuccessRegisterActivity) {
        source.startActivity(LoginActivityRouter.createIntent(source))
        source.finish()
    }

}