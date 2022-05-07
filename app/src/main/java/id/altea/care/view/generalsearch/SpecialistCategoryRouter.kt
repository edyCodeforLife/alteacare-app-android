package id.altea.care.view.generalsearch

import id.altea.care.view.specialistsearch.SpecialistSearchActivity

class SpecialistCategoryRouter {

    fun openSpecialist(source: SpecialistCategoryActivity, id: String?, query: String?) {
        source.startActivity(SpecialistSearchActivity.createIntent(source, id, query))
    }
}