package id.altea.care.view.password

import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.view.account.setting.SettingAccountActivity
import id.altea.care.view.account.setting.SettingAccountRouter
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.register.termcondition.TermConditionActivity

/**
 * Created by trileksono on 05/03/21.
 */
class PasswordRouter {

    fun openTermCondition(source: PasswordActivity, registerInfo: RegisterInfo) {
        source.startActivity(TermConditionActivity.createIntent(source, registerInfo))
    }

    fun openLogin(source: PasswordActivity) {
        source.apply {
            startActivity(LoginActivityRouter.createIntent(source))
            finish()
        }
    }

    fun openAccountSetting(source: PasswordActivity){
        source.apply {
            startActivity(SettingAccountRouter.createIntent(source))
            finish()
        }
    }
}

enum class PasswordType {
    REGISTER, FORGOT_PASSWORD,CHANGE_PASSWORD
}
