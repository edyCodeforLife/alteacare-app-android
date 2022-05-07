package id.altea.care.view.myconsultation

import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.view.myconsultation.bottomsheet.ConsultationFilterBottomSheet

object MyConsultationRouter {

    fun openFilterConsultation(
        fragment: MyConsultationFragment,
        familyList: List<PatientFamily>,
        patientSelected: PatientFamily?,
        callbackPatient: (PatientFamily?) -> Unit
    ) {
        ConsultationFilterBottomSheet.newInstance(
            ArrayList(familyList),
            patientSelected,
            callbackPatient
        ).show(fragment.childFragmentManager, "filter_consultation")
    }
}
