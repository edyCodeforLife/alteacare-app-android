package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

class SearchResultPayloadBuilder(
    internal val specialistCategory: String?,
    private val specialistDoctorName: String?,
    internal val symptom: String?
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.SPECIALIST_CATEGORY, specialistCategory)
            .addAttribute(AnalyticsConstants.KEY.SPECIALIST_DOCTOR_NAME, specialistDoctorName)
            .addAttribute(AnalyticsConstants.KEY.FILTER_SYMPTOM, symptom)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.SPECIALIST_CATEGORY, specialistCategory?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.FILTER_SYMPTOM, symptom?.analyticValidatedLength())
        }
    }

}