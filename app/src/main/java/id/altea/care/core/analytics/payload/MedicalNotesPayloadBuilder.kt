package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

data class MedicalNotesPayloadBuilder(
    internal val diagnosis: String?,
    internal val symptom: String?,
    internal val medicalPrescription: String?,
    internal val doctorsRecommendation: String?,
    internal val anotherNote: String?
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.DIAGNOSIS, diagnosis)
            .addAttribute(AnalyticsConstants.KEY.SYMPTOM, symptom)
            .addAttribute(AnalyticsConstants.KEY.MEDICAL_PRESCRIPTION, medicalPrescription)
            .addAttribute(AnalyticsConstants.KEY.DOCTORS_RECOMMENDATION, doctorsRecommendation)
            .addAttribute(AnalyticsConstants.KEY.ANOTHER_NOTE, anotherNote)
    }

    override fun facebookPayload(): Bundle? {
        return super.facebookPayload()
    }
}