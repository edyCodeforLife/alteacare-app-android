package id.altea.care.view.account.changeprofile

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.view.account.ChangePersonalBottomSheet
import id.altea.care.view.address.addresslist.AddressListActivity
import id.altea.care.view.changephonenumber.ChangePhoneNumberActivity
import id.altea.care.view.common.bottomsheet.ImageChooserBottomSheet
import id.altea.care.view.common.bottomsheet.TypeFileSource
import id.altea.care.view.contact.ContactRouter
import id.altea.care.view.otpmail.changemail.ChangeEmailActivity

class ChangeProfileRouter {

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, ChangeProfileActivity::class.java)
        }
    }

    fun openImageChooser(source: FragmentActivity, callback: (TypeFileSource) -> Unit) {
        ImageChooserBottomSheet.newInstance(callback).show(source.supportFragmentManager, "image")
    }

    fun openChangePersonalBottomSheet(
        caller: FragmentManager?,
        submitCallback: (String) -> Unit,
        dismissCallback: () -> Unit,
        state : ChangePersonalBottomSheet.State
    ) {
        caller?.let {
            ChangePersonalBottomSheet.newInstance(submitCallback, dismissCallback,state)
                .show(it, "change_personal")
        }
    }

    fun openContactActivity(source: Activity) {
        source.startActivity(ContactRouter.createIntent(source))
    }

    fun openChangeEmailActivity(source: Activity,email :String) {
        source.startActivity(ChangeEmailActivity.createIntent(source, email, ""))
    }

    fun openChangePhoneNumberActivity(source: Activity,phoneNumber : String){
        source.startActivity(ChangePhoneNumberActivity.createIntent(source, phoneNumber, ""))
    }

    fun openAddressListActivity(source: Activity) {
        source.startActivity(AddressListActivity.createIntent(source))
    }
}