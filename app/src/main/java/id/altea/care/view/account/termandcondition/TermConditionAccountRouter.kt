package id.altea.care.view.account.termandcondition

import android.content.Context
import android.content.Intent
import id.altea.care.view.account.changeprofile.ChangeProfileActivity

class TermConditionAccountRouter {
    companion object{
        fun createIntent(caller: Context): Intent {
            return Intent(caller, TermConditionAccount::class.java)
        }
    }
}