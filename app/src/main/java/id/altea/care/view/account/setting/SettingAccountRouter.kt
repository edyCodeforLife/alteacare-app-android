package id.altea.care.view.account.setting

import android.content.Context
import android.content.Intent
import id.altea.care.view.changepassword.ChangePasswordActivity
import id.altea.care.view.changepassword.ChangePasswordRouter

class SettingAccountRouter {

    companion object{
        fun createIntent(
            caller: Context
        ): Intent {
            return Intent(caller, SettingAccountActivity::class.java)
        }
    }

    fun openChangePassword(source : SettingAccountActivity){
        source.startActivity(ChangePasswordRouter.createIntent(source))
    }


}