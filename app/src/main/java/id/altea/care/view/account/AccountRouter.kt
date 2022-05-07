package id.altea.care.view.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import id.altea.care.view.account.changeprofile.ChangeProfileRouter
import id.altea.care.view.account.contact.ContactServicesRouter
import id.altea.care.view.account.setting.SettingAccountRouter
import id.altea.care.view.account.termandcondition.TermConditionAccountRouter
import id.altea.care.view.common.bottomsheet.AddAccountOrCreateBottomSheet
import id.altea.care.view.contact.ContactRouter
import id.altea.care.view.family.listfamily.ListFamilyActivity
import id.altea.care.view.faq.FaqRouter
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.main.MainActivity
import id.altea.care.view.register.contactinfo.RegisterContactRouter
import id.altea.care.view.register.contactinfo.RegisterContactState

class AccountRouter {

    fun openChangeProfileActivity(activity: Activity) {
        activity.startActivity(ChangeProfileRouter.createIntent(activity))
    }

    fun openChangePasswordActivity(activity: Activity) {
        activity.startActivity(SettingAccountRouter.createIntent(activity))
    }

    fun openFaqActivity(activity: Activity) {
        activity.startActivity(FaqRouter.createIntent(activity))
    }

    fun openContactServicesActivity(source: Activity) {
        source.startActivity(ContactServicesRouter.createIntent(source))
    }

    fun openTermConditionAccountActivity(source: Activity) {
        source.startActivity(TermConditionAccountRouter.createIntent(source))
    }

    fun openFamilyContactActivity(source: Activity) {
        source.startActivity(ListFamilyActivity.createIntent(source))
    }

    fun openMainActivity(source: Activity) {
        source.startActivity(MainActivity.createIntent(source).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        source.finish()
    }

    fun openBottomSheetAccount(
        source: FragmentManager,
        addAccountCallback: () -> Unit,
        createAccountCallback: () -> Unit
    ) {
        AddAccountOrCreateBottomSheet.newInstance(addAccountCallback, createAccountCallback)
            .show(source, "add_create_account")
    }

    fun openLoginActivity(source: Activity) {
        source.startActivity(LoginActivityRouter.createIntent(source))
    }

    fun openRegistration(source: Context) {
        source.startActivity(
            RegisterContactRouter.createIntent(
                source,
                null,
                null,
                RegisterContactState.PAGE_REGISTER,
                null
            ))
    }
}