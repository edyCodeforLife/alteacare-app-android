package id.altea.care.view.register.personalinfo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.view.common.bottomsheet.DatePickerBottomSheet
import id.altea.care.view.common.bottomsheet.DatePickerType
import id.altea.care.view.common.bottomsheet.GenderBottomSheet
import id.altea.care.view.password.PasswordActivity
import id.altea.care.view.password.PasswordRouter
import id.altea.care.view.password.PasswordType
import id.altea.care.view.register.contactinfo.RegisterContactRouter
import id.altea.care.view.register.contactinfo.RegisterContactState

/**
 * Created by trileksono on 04/03/21.
 */
class RegisterPersonalInfoRouter {

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, RegisterPersonalInfoActivity::class.java)
        }

        fun createIntent(
            caller: Context,
            registerInfo: RegisterInfo?
        ): Intent {
            return Intent(caller, RegisterPersonalInfoActivity::class.java).apply {
                putExtra(RegisterPersonalInfoActivity.EXTRA_INFO, registerInfo)
            }
        }
    }

    fun openGenderBottomSheet(
        caller: AppCompatActivity,
        submitCallback: (String) -> Unit,
        dismissCallback: () -> Unit
    ) {
        GenderBottomSheet.newInstance(submitCallback, dismissCallback)
            .show(caller.supportFragmentManager, "gender")
    }

    fun openDatePickerBottomSheet(
        caller: AppCompatActivity,
        submitCallback: (String) -> Unit,
        dismissCallback: () -> Unit
    ) {
        DatePickerBottomSheet.newInstance(DatePickerType.BIRTH_DATE, submitCallback, dismissCallback)
            .show(caller.supportFragmentManager, "date")
    }

    fun openConfirmationBottomSheet(
        caller: AppCompatActivity,
        registerInfo: RegisterInfo
    ) {
        PersonalInfoConfirmationBottomSheet.newInstance(
            registerInfo
        ).show(caller.supportFragmentManager, "confirmation")
    }

    fun openRegisterContactInfo(source: AppCompatActivity, registerInfo: RegisterInfo) {
        source.startActivity(RegisterContactRouter.createIntent(source, registerInfo,null,RegisterContactState.PAGE_REGISTER,null))
    }

    fun openRegisterPassword(source: AppCompatActivity, registerInfo: RegisterInfo?) {
        return source.startActivity(
            PasswordActivity.createIntent(
                source,
                PasswordType.REGISTER,
                registerInfo
            )
        )
    }
}
