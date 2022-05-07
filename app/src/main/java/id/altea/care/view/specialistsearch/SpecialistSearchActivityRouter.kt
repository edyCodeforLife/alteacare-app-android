package id.altea.care.view.specialistsearch

import android.app.Activity
import android.content.Context
import id.altea.care.core.domain.model.Doctor
import id.altea.care.view.contact.ContactRouter
import id.altea.care.view.doctordetail.DoctorDetailActivity
import id.altea.care.view.specialistsearch.bottomsheet.SpecialistFilterBottomSheet
import id.altea.care.view.specialistsearch.bottomsheet.SpecialistSortBottomSheet
import id.altea.care.view.specialistsearch.model.FilterActiveModel
import id.altea.care.view.specialistsearch.model.SpecialistSortModel

/**
 * Created by trileksono on 10/03/21.
 */
class SpecialistSearchActivityRouter {

    fun openSortBottomSheet(
        sortList: ArrayList<SpecialistSortModel>,
        caller: SpecialistSearchActivity,
        submitCallback: (SpecialistSortModel) -> Unit
    ) {
        SpecialistSortBottomSheet.newInstance(sortList, submitCallback)
            .show(caller.supportFragmentManager, "sort")
    }

    fun openFilterBottomSheet(
        defaultSpecialistId: String?,
        caller: SpecialistSearchActivity,
        submitCallback: (MutableList<FilterActiveModel>) -> Unit
    ) {
        SpecialistFilterBottomSheet.newInstance(
            defaultSpecialistId,
            submitCallback
        ).show(caller.supportFragmentManager, "filter")
    }

    fun openDoctorDetail(context: Context, doctor: Doctor,filterActive : FilterActiveModel.FilterDate?) {
        context.startActivity(DoctorDetailActivity.createIntent(context, doctor,null,null,null,filterActive))
    }


    fun openContactActivity(source: Activity) {
        source.startActivity(ContactRouter.createIntent(source))
        source.finish()
    }

}
