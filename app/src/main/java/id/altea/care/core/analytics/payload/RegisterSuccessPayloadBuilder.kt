package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

/**
 * if this track event have multiple implementation
 * just override another payload
 */
class RegisterSuccessPayloadBuilder(
    internal val userId: String?,
    internal val name: String?,
    internal val gender: String?,
    internal val email: String?,
    internal val phone: String?,
    internal val userBod: String?
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.USER_ID, userId)
            .addAttribute(AnalyticsConstants.KEY.NAME, name)
            .addAttribute(AnalyticsConstants.KEY.GENDER, gender)
            .addAttribute(AnalyticsConstants.KEY.EMAIL, email)
            .addAttribute(AnalyticsConstants.KEY.PHONE, phone)
            .addAttribute(AnalyticsConstants.KEY.USER_BOD, userBod)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.USER_ID, userId?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.NAME, name?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.GENDER, gender?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.EMAIL, email?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.PHONE, phone?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.USER_BOD, userBod?.analyticValidatedLength())
        }
    }

}