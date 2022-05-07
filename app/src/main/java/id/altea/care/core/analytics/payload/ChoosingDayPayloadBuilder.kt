package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

data class ChoosingDayPayloadBuilder(
    internal val day: String?
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.CHOOSING_DAY, day)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.CHOOSING_DAY, day?.analyticValidatedLength())
        }
    }

}