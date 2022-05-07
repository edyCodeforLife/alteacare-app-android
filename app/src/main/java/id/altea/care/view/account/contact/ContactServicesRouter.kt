package id.altea.care.view.account.contact

import android.app.Activity
import android.content.Context
import android.content.Intent
import id.altea.care.view.contact.ContactRouter
import zendesk.support.requestlist.RequestListActivity

object ContactServicesRouter {

    fun createIntent(caller: Context): Intent {
        return Intent(caller, ContactServicesActivity::class.java)
    }

    fun openContactActivity(source: Activity) {
        source.startActivity(ContactRouter.createIntent(source))
    }

    fun openRequestListActivity(source: Activity) {
        RequestListActivity.builder().show(source)
    }

}