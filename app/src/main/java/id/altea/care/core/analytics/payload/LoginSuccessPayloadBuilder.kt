package id.altea.care.core.analytics.payload

import android.os.Bundle
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

data class LoginSuccessPayloadBuilder(
    internal val uniqueId: String?,
    internal val fullName: String?,
    internal val firstName: String?,
    internal val lastName: String?,
    internal val email: String?,
    internal val phoneNumber: String?,
    internal val age: Int?,
    internal val loginAt: String?,
) : AnalyticPayload {

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.UNIQUE_ID, uniqueId?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.LAST_NAME, lastName?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.EMAIL, email?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.PHONE, phoneNumber?.analyticValidatedLength())
            putInt(AnalyticsConstants.KEY.AGE, age ?: 0)
            putString(AnalyticsConstants.KEY.LOGIN_AT, loginAt)
        }
    }
}