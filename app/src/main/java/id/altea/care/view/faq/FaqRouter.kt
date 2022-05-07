package id.altea.care.view.faq

import android.content.Context
import android.content.Intent
import id.altea.care.view.changepassword.ChangePasswordActivity
import id.altea.care.view.contact.ContactRouter

class FaqRouter {
    companion object{
        fun createIntent(
            caller: Context
        ): Intent {
            return Intent(caller, FaqActivity::class.java)
        }
    }
}