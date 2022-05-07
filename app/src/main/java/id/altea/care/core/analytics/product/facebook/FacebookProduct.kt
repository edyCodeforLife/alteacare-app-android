package id.altea.care.core.analytics.product.facebook

import android.app.Application
import android.content.Context
import com.facebook.appevents.AppEventsLogger
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.analytics.payload.*
import id.altea.care.core.analytics.product.AnalyticProduct

class FacebookProduct : AnalyticProduct {

    private lateinit var logger: AppEventsLogger

    override fun initialize(application: Application) {
        logger = AppEventsLogger.newLogger(application)
    }

    override fun trackingEventRegistration(
        context: Context,
        registerSuccessPayloadBuilder: RegisterSuccessPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.REGISTRATION, registerSuccessPayloadBuilder.facebookPayload())
    }

    override fun trackingEventSpecialistCategory(
        context: Context,
        specialistCategoryPayloadBuilder: SpecialistCategoryPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.SPECIALIST_CATEGORY, specialistCategoryPayloadBuilder.facebookPayload())
    }

    override fun trackingEventBookingSchedule(
        context: Context,
        bookingSchedulePayloadBuilder: BookingSchedulePayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.BOOKING_SCHEDULE, bookingSchedulePayloadBuilder.facebookPayload())
    }

    override fun trackingEventEndCallMa(
        context: Context,
        endCallMaPayloadBuilder: EndCallMaPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.MA_CALL, endCallMaPayloadBuilder.facebookPayload())
    }

    override fun trackingEventSearchResult(
        context: Context,
        searchResultPayloadBuilder: SearchResultPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.SEARCH_RESULT, searchResultPayloadBuilder.facebookPayload())
    }

    override fun trackingEventDoctorCall(
        context: Context,
        doctorCallPayloadBuilder: DoctorCallPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.DOCTOR_CALL, doctorCallPayloadBuilder.facebookPayload())
    }
    override fun trackingUserLogin(
        context: Context,
        loginSuccessPayloadBuilder: LoginSuccessPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.LOGIN, loginSuccessPayloadBuilder.facebookPayload())
    }

    override fun trackingEventDoctorProfile(
        context: Context,
        doctorProfilePayloadBuilder: DoctorProfilePayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.DOCTOR_PROFILE, doctorProfilePayloadBuilder.facebookPayload())
    }

    override fun trackingEventMedicalNotes(
        context: Context,
        medicalNotesPayloadBuilder: MedicalNotesPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.MEDICAL_NOTES, medicalNotesPayloadBuilder.facebookPayload())
    }

    /**
     * Temporarily not implemented
     */
    override fun trackingLastDoctorChatName(context: Context, doctorName: String?) {}
    override fun trackingLastOpenTime(context: Context) {}
    override fun trackingCity(context: Context, city: String?) {}

    override fun trackingEventChoosingDay(
        context: Context,
        choosingDayPayloadBuilder: ChoosingDayPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.CHOOSING_DAY, choosingDayPayloadBuilder.facebookPayload())
    }

    override fun trackingEventParameterGeneralSearch(
        context: Context,
        parameterGeneralSearchPayloadBuilder: ParameterGeneralSearchPayloadBuilder
    ) {
        logger.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.PARAMETER_SEARCH, parameterGeneralSearchPayloadBuilder.facebookPayload())
    }

}