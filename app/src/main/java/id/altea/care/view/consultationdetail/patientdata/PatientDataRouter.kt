package id.altea.care.view.consultationdetail.patientdata

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.view.common.bottomsheet.CountDownSpecialistBottomSheet
import id.altea.care.view.common.bottomsheet.FileChooserBottomSheet
import id.altea.care.view.common.bottomsheet.TypeFileSource
import id.altea.care.view.consultation.ConsultationFragment
import id.altea.care.view.consultationdetail.ConfirmCallSpecialistBottomSheet
import id.altea.care.view.viewdocument.ViewDocumentActivity
import id.altea.care.view.doctordetail.DoctorDetailActivity
import id.altea.care.view.transition.specialist.TransitionSpecialistActivity

class PatientDataRouter {

    fun openFileChooser(source: FragmentActivity, callback: (TypeFileSource) -> Unit) {
        FileChooserBottomSheet.newInstance(callback).show(source.supportFragmentManager, "file")
    }

    fun openTransitionSpecialist(
        source: FragmentActivity,
        appointmentId: Int,
        doctor: ConsultationDoctor?,
        infoDetail: InfoDetail?
    ) {
        source.startActivity(
            TransitionSpecialistActivity.createIntent(
                source.applicationContext,
                appointmentId,
                doctor,
                infoDetail
            )
        )
        source.finish()
    }

    fun openViewDocument(source: FragmentActivity, urlPath: String) {
        source.startActivity(ViewDocumentActivity.createIntent(source.applicationContext, urlPath))
    }

    fun openConfirmCallBottomSheet(
        caller: FragmentManager?,
        submitCallback: (String) -> Unit,
        dismissCallback: () -> Unit,
        doctor: ConsultationDoctor?
    ) {
        caller?.let {
            ConfirmCallSpecialistBottomSheet.newInstance(submitCallback, dismissCallback, doctor)
                .show(it, "confirm_call")
        }
    }

    fun openDoctorDetail(
        source: FragmentActivity,
        doctor: Doctor,
        appointmentId: Int,
        consultationMethod: String? = ConsultationFragment.VIDEO_CALL
    ) {
        source.startActivity(
            DoctorDetailActivity.createIntent(
                source,
                doctor = doctor,
                refId = appointmentId,
                consulMethod = consultationMethod
            )
        )
    }

    fun openBottomSheetCountDownSpecialist(
        caller: FragmentManager?,
        dateConsultation: String,
        timeMinuteConsultation: String,
        countDownTimer: Long,
        onFinish: () -> Unit
    ) {
        caller?.let { fragmentManager ->
            CountDownSpecialistBottomSheet
                .newInstance(
                    dateConsultation = dateConsultation,
                    timeMinuteConsultation = timeMinuteConsultation,
                    countdownTime = countDownTimer,
                    onFinishTimer = onFinish
                )
                .show(fragmentManager,"countdown_timer")
        }
    }
}
