package id.altea.care.core.analytics.product.moengage

import android.app.Application
import android.content.Context
import com.moe.pushlibrary.MoEHelper
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.model.AppStatus
import com.moengage.pushbase.MoEPushHelper
import id.altea.care.BuildConfig
import id.altea.care.R
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.analytics.payload.DoctorProfilePayloadBuilder
import id.altea.care.core.analytics.payload.DoctorCallPayloadBuilder
import id.altea.care.core.analytics.payload.LoginSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.RegisterSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.*
import id.altea.care.core.analytics.product.AnalyticProduct
import id.altea.care.core.ext.analyticValidatedLength
import id.altea.care.core.ext.customDateFormat
import timber.log.Timber
import id.altea.care.core.notification.AlteaNotificationMoeExtension

class MoengageProduct : AnalyticProduct {

    override fun initialize(application: Application) {
        val moEngage = MoEngage
            .Builder(application, BuildConfig.MOENGAGE)
            .configureNotificationMetaData(NotificationConfig(R.drawable.ic_notification_small_icon, 0, R.color.primary, null, true, isBuildingBackStackEnabled = false, isLargeIconDisplayEnabled = true))
            .configureLogs(LogConfig(LogLevel.VERBOSE, false))
            .build()

        MoEngage.initialise(moEngage)
        MoEPushHelper.getInstance().messageListener = AlteaNotificationMoeExtension()
        trackInstallOrUpdatae(application)
    }

    private fun trackInstallOrUpdatae(application: Application) {
        val preferences = application.getSharedPreferences("id.altea.care", 0)
        if (!preferences.getBoolean("has_sent_install", false)) {
            MoEHelper.getInstance(application).setAppStatus(AppStatus.INSTALL)
            preferences.edit().putBoolean("has_sent_install", true).apply()
            preferences.edit().putString("existing", BuildConfig.VERSION_NAME).apply()
        } else if (!preferences.getString("existing", "").equals(BuildConfig.VERSION_NAME)) {
            MoEHelper.getInstance(application).setAppStatus(AppStatus.UPDATE)
            preferences.edit().putString("existing", BuildConfig.VERSION_NAME).apply()
        } else {
            Timber.i("No need to send event")
        }
    }

    override fun trackingEventRegistration(
        context: Context,
        registerSuccessPayloadBuilder: RegisterSuccessPayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.REGISTRATION, registerSuccessPayloadBuilder.moengagePayload())
    }

    override fun trackingEventSpecialistCategory(
        context: Context,
        specialistCategoryPayloadBuilder: SpecialistCategoryPayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.SPECIALIST_CATEGORY, specialistCategoryPayloadBuilder.moengagePayload())
        MoEHelper.getInstance(context)
            .setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_SPECIALIST_PICK, specialistCategoryPayloadBuilder.specialistCategoryName ?: "")
    }

    override fun trackingEventEndCallMa(
        context: Context,
        endCallMaPayloadBuilder: EndCallMaPayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.MA_CALL, endCallMaPayloadBuilder.moengagePayload())
    }

    override fun trackingEventSearchResult(
        context: Context,
        searchResultPayloadBuilder: SearchResultPayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.SEARCH_RESULT, searchResultPayloadBuilder.moengagePayload())
        MoEHelper.getInstance(context)
            .setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_SEARCH, searchResultPayloadBuilder.specialistCategory ?: "")
    }

    override fun trackingUserLogin(
        context: Context,
        loginSuccessPayloadBuilder: LoginSuccessPayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .setUniqueId(loginSuccessPayloadBuilder.uniqueId ?: "")
        MoEHelper.getInstance(context)
            .setFullName(loginSuccessPayloadBuilder.fullName ?: "")
        MoEHelper.getInstance(context)
            .setFirstName(loginSuccessPayloadBuilder.firstName ?: "")
        MoEHelper.getInstance(context)
            .setLastName(loginSuccessPayloadBuilder.lastName ?: "")
        MoEHelper.getInstance(context)
            .setEmail(loginSuccessPayloadBuilder.email ?: "")
        MoEHelper.getInstance(context)
            .setNumber(loginSuccessPayloadBuilder.phoneNumber ?: "")
        MoEHelper.getInstance(context)
            .setUserAttribute("age", loginSuccessPayloadBuilder.age ?: 0)
        MoEHelper.getInstance(context)
            .setUserAttribute("login at", loginSuccessPayloadBuilder.loginAt ?: "")
    }

    override fun trackingEventBookingSchedule(
        context: Context,
        bookingSchedulePayloadBuilder: BookingSchedulePayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.BOOKING_SCHEDULE, bookingSchedulePayloadBuilder.moengagePayload())
    }

    override fun trackingEventDoctorCall(
        context: Context,
        doctorCallPayloadBuilder: DoctorCallPayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.DOCTOR_CALL, doctorCallPayloadBuilder.moengagePayload())
        MoEHelper.getInstance(context).setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_CONSULTATION_DATE, doctorCallPayloadBuilder.lastConsultationDate ?: "")
        MoEHelper.getInstance(context).setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_HOSPITAL_PICK, doctorCallPayloadBuilder.hospitalName ?: "")
    }

    override fun trackingEventDoctorProfile(
        context: Context,
        doctorProfilePayloadBuilder: DoctorProfilePayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.DOCTOR_PROFILE, doctorProfilePayloadBuilder.moengagePayload())
    }

    override fun trackingEventMedicalNotes(
        context: Context,
        medicalNotesPayloadBuilder: MedicalNotesPayloadBuilder
    ) {
        MoEHelper.getInstance(context)
            .trackEvent(AnalyticsConstants.EVENT.MEDICAL_NOTES, medicalNotesPayloadBuilder.moengagePayload())
        MoEHelper.getInstance(context)
            .setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_DIAGNOSED_DISEASE, medicalNotesPayloadBuilder.diagnosis ?: "")
    }

    override fun trackingLastDoctorChatName(context: Context, doctorName: String?) {
        MoEHelper.getInstance(context).setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_DOCTOR_CHAT_NAME, doctorName ?: "")
    }

    override fun trackingLastOpenTime(context: Context) {
        MoEHelper.getInstance(context).setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_OPEN_TIME, customDateFormat())
    }

    override fun trackingEventChoosingDay(context: Context, choosingDayPayloadBuilder: ChoosingDayPayloadBuilder) {
        MoEHelper.getInstance(context).trackEvent(AnalyticsConstants.EVENT.CHOOSING_DAY, choosingDayPayloadBuilder.moengagePayload())
    }

    override fun trackingEventParameterGeneralSearch(
        context: Context,
        parameterGeneralSearchPayloadBuilder: ParameterGeneralSearchPayloadBuilder
    ) {
        MoEHelper.getInstance(context).trackEvent(AnalyticsConstants.EVENT.PARAMETER_SEARCH, parameterGeneralSearchPayloadBuilder.moengagePayload())
    }

    override fun trackingCity(context: Context, city: String?) {
        MoEHelper.getInstance(context).setUserAttribute(AnalyticsConstants.CUSTOM_ATTRIBUTES.CITY, city ?: "")
    }

}