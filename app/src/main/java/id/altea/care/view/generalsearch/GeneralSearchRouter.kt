package id.altea.care.view.generalsearch

import id.altea.care.core.domain.model.Doctor
import id.altea.care.view.doctordetail.DoctorDetailActivity
import id.altea.care.view.specialistsearch.SpecialistSearchActivity

class GeneralSearchRouter {

    fun openSpecialist(source: GeneralSearchActivity, id: String?, query: String?) {
        source.startActivity(SpecialistSearchActivity.createIntent(source, id, query))
    }

    fun openDoctorDetail(source: GeneralSearchActivity, doctor: Doctor) {
        source.startActivity(DoctorDetailActivity.createIntent(source, doctor =  doctor))
    }

    fun openSymptomList(
        source: GeneralSearchActivity,
        keyword: String
    ) {
        source.startActivity(SymptomListActivity.createIntent(source,  keyword))
    }

    fun openSpecialistCategory(
        source: GeneralSearchActivity,
        keyword: String
    ) {
        source.startActivity(SpecialistCategoryActivity.createIntent(source, keyword))
    }

}
