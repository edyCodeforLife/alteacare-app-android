package id.altea.care.view.contact

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.main.MainActivity

/**
 * Created by trileksono on 11/03/21.
 */
class ContactRouter {

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, ContactActivity::class.java)
        }
    }

    fun openAccount(source : AppCompatActivity){
        source.startActivity(MainActivity.createIntent(source,3))
        source.finish()
    }

}
