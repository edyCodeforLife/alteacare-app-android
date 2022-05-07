package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

class EndCallMaPayloadBuilder(
    internal val appointmentId: String?,
    internal val orderCode: String?,
    internal val roomCode: String?
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.APPOINTMENT_ID, appointmentId)
            .addAttribute(AnalyticsConstants.KEY.ORDER_CODE, orderCode)
            .addAttribute(AnalyticsConstants.KEY.ROOM_CODE, roomCode)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.ROOM_CODE, roomCode?.analyticValidatedLength())
        }
    }

}