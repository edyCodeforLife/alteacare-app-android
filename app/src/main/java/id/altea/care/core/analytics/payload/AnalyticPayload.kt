package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties

interface AnalyticPayload {

    fun moengagePayload(): Properties? = null

    fun facebookPayload(): Bundle? = null

}