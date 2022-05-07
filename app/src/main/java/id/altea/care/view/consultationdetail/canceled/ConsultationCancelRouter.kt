package id.altea.care.view.consultationdetail.canceled

import android.content.Context
import id.altea.care.core.domain.model.Doctor
import id.altea.care.view.doctordetail.DoctorDetailActivity

class ConsultationCancelRouter {
    fun openDoctorDetail(
        source: Context,
        doctor: Doctor,
        appointmentId: Int,
        consultationMethod: String,
        patientId: String
    ) {
        source.startActivity(
            DoctorDetailActivity.createIntent(
                source,
                doctor = doctor,
                refId = appointmentId,
                consulMethod = consultationMethod,
                patientId = patientId
            )
        )
    }
}
