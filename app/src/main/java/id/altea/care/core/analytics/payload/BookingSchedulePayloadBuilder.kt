package id.altea.care.core.analytics.payload

import android.os.Bundle
import com.moengage.core.Properties
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.ext.analyticValidatedLength

data class BookingSchedulePayloadBuilder(
    internal val hospitalId: String?,
    internal val hospitalName: String?,
    internal val bookingDate: String?,
    internal val bookingHour: String?,
    internal val doctorId: String?,
    internal val doctorName: String?,
    internal val doctorSpeciality: String?
) : AnalyticPayload {

    override fun moengagePayload(): Properties {
        return Properties()
            .addAttribute(AnalyticsConstants.KEY.HOSPITAL_ID, hospitalId)
            .addAttribute(AnalyticsConstants.KEY.HOSPITAL_NAME, hospitalName)
            .addAttribute(AnalyticsConstants.KEY.BOOKING_DATE, bookingDate)
            .addAttribute(AnalyticsConstants.KEY.BOOKING_HOUR, bookingHour)
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_ID, doctorId)
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_NAME, doctorName)
            .addAttribute(AnalyticsConstants.KEY.DOCTOR_SPECIALITY, doctorSpeciality)
    }

    override fun facebookPayload(): Bundle {
        return Bundle().apply {
            putString(AnalyticsConstants.KEY.HOSPITAL_ID, hospitalId?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.HOSPITAL_NAME, hospitalName?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.BOOKING_DATE, bookingDate?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.BOOKING_HOUR, bookingHour?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.DOCTOR_ID, doctorId?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.DOCTOR_NAME, doctorName?.analyticValidatedLength())
            putString(AnalyticsConstants.KEY.DOCTOR_SPECIALITY, doctorSpeciality?.analyticValidatedLength())
        }
    }

}