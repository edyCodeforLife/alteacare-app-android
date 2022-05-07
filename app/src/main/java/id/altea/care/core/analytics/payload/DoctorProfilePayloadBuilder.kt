package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

data class DoctorProfilePayloadBuilder(
    internal val doctorId: String?,
    internal val doctorName: String?,
    internal val speciality: String?,
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_ID, doctorId)
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_NAME, doctorName)
            .addAttribute(AnalyticsConstants.KEY.SPECIALITY, speciality)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.SPECIALITY, speciality?.analyticValidatedLength())
        }
    }

}