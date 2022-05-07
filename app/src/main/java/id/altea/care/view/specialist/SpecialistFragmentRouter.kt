package id.altea.care.view.specialist

import android.content.Context
import id.altea.care.view.specialistsearch.SpecialistSearchActivity
import timber.log.Timber

class SpecialistFragmentRouter {

    fun openSpecialistSearch(caller: Context, specialistId: String, specialistName: String) {
        caller.let {
            it.startActivity(SpecialistSearchActivity.createIntent(it, specialistId, specialistName))
        }
    }
}
