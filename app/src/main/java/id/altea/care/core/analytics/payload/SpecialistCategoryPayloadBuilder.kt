package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

class SpecialistCategoryPayloadBuilder(
    internal val specialistCategoryId: String?,
    internal val specialistCategoryName: String?
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.CATEGORY_ID, specialistCategoryId)
            .addAttribute(AnalyticsConstants.KEY.SPECIALIST_CATEGORY_NAME, specialistCategoryName)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.CATEGORY_ID, specialistCategoryId)
            putString(AnalyticsConstants.KEY.SPECIALIST_CATEGORY_NAME, specialistCategoryName?.analyticValidatedLength())
        }
    }

}