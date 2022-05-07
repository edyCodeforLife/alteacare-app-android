package id.altea.care.view.register.contactinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.core.domain.model.FamilyInfo
import id.altea.care.core.domain.model.RegisterInfo
import id.altea.care.view.family.listfamily.ListFamilyActivity
import id.altea.care.view.password.PasswordActivity
import id.altea.care.view.password.PasswordType
import id.altea.care.view.register.personalinfo.RegisterPersonalInfoActivity
import id.altea.care.view.register.personalinfo.RegisterPersonalInfoRouter

/**
 * Created by trileksono on 05/03/21.
 */
class RegisterContactRouter {
    companion object {

        fun createIntent(
            caller: Context,
            registerInfo: RegisterInfo?,
            familyInfo: FamilyInfo? ,
            registerContactState: RegisterContactState,
            patientId : String?
        ): Intent {
            return Intent(caller, RegisterContactActivity::class.java).apply {
                putExtra(RegisterContactActivity.EXTRA_INFO, registerInfo)
                putExtra(RegisterContactActivity.EXTRA_STATE, registerContactState)
                putExtra(RegisterContactActivity.EXTRA_FAMILY_INFO,familyInfo)
                putExtra(RegisterContactActivity.EXTRA_PATIENT_ID,patientId)
            }
        }
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

    fun openRegisterPersonalInfoActivity(source: Context, registerInfo: RegisterInfo?) {
        source.startActivity(RegisterPersonalInfoRouter.createIntent(source, registerInfo))
    }

    fun openFamilyContactActivity(source : Activity){
        source.startActivity(ListFamilyActivity.createIntent(source))
        source.finish()
    }
}
