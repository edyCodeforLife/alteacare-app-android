package id.altea.care.view.generalsearch

import id.altea.care.view.specialistsearch.SpecialistSearchActivity

class SymptomListRouter {

    fun openSpecialist(source: SymptomListActivity, id: String?, query: String?) {
        source.startActivity(SpecialistSearchActivity.createIntent(source, id, query))
    }
}