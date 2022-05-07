package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

data class DoctorCallPayloadBuilder(
    internal val doctorId: String?,
    internal val doctorName: String?,
    internal val doctorSpecialist: String?,
    internal val hospitalId: String?,
    internal val hospitalName: String?,
    internal val lastConsultationDate: String?,
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_ID, doctorId)
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_NAME, doctorName)
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_SPECIALIST, doctorSpecialist)
            .addAttribute(AnalyticsConstants.KEY.HOSPITAL_ID, hospitalId)
            .addAttribute(AnalyticsConstants.KEY.HOSPITAL_NAME, hospitalName)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.DOCTOR_ID, doctorId?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.HOSPITAL_ID, hospitalId?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.HOSPITAL_NAME, hospitalName?.analyticValidatedLength())
        }
    }

}