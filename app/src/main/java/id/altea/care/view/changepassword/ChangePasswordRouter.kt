package id.altea.care.view.changepassword

import android.content.Context
import android.content.Intent
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.view.password.PasswordActivity
import id.altea.care.view.password.PasswordRouter
import id.altea.care.view.password.PasswordType

class ChangePasswordRouter {
    companion object{
        fun createIntent(
            caller: Context
        ): Intent {
            return Intent(caller, ChangePasswordActivity::class.java)
        }
    }

    fun openPasswordActivity(source : ChangePasswordActivity,registerInfo: RegisterInfo){
        source.startActivity(PasswordActivity.createIntent(source,PasswordType.CHANGE_PASSWORD,registerInfo))
        source.finish()
    }
}