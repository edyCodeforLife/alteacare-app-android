package id.altea.care.core.analytics.product.firebase

import android.app.Application
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import id.altea.care.core.analytics.AnalyticsConstants
import id.altea.care.core.analytics.payload.*
import id.altea.care.core.analytics.product.AnalyticProduct
import id.altea.care.core.ext.analyticValidatedLength
import id.altea.care.core.ext.customDateFormat

class GtmProduct : AnalyticProduct {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun initialize(application: Application) {
        firebaseAnalytics = Firebase.analytics
    }

    override fun trackingUserLogin(
        context: Context,
        loginSuccessPayloadBuilder: LoginSuccessPayloadBuilder
    ) {
        firebaseAnalytics.setUserId(loginSuccessPayloadBuilder.uniqueId)
        firebaseAnalytics.setUserProperty("last_name", loginSuccessPayloadBuilder.lastName?.analyticValidatedLength())
        firebaseAnalytics.setUserProperty("email", loginSuccessPayloadBuilder.email?.analyticValidatedLength())
        firebaseAnalytics.setUserProperty("phone", loginSuccessPayloadBuilder.phoneNumber?.analyticValidatedLength())
        firebaseAnalytics.setUserProperty("login_at", loginSuccessPayloadBuilder.loginAt?.analyticValidatedLength())
    }

    override fun trackingEventRegistration(
        context: Context,
        registerSuccessPayloadBuilder: RegisterSuccessPayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.REGISTRATION) {
            param(AnalyticsConstants.KEY.USER_ID, registerSuccessPayloadBuilder.userId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.NAME, registerSuccessPayloadBuilder.name?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.GENDER, registerSuccessPayloadBuilder.gender?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.EMAIL, registerSuccessPayloadBuilder.email?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.PHONE, registerSuccessPayloadBuilder.phone?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.USER_BOD, registerSuccessPayloadBuilder.userBod?.analyticValidatedLength() ?: "")
        }
    }

    override fun trackingEventSpecialistCategory(
        context: Context,
        specialistCategoryPayloadBuilder: SpecialistCategoryPayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.SPECIALIST_CATEGORY) {
            param(AnalyticsConstants.KEY.CATEGORY_ID, specialistCategoryPayloadBuilder.specialistCategoryId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.SPECIALIST_CATEGORY_NAME, specialistCategoryPayloadBuilder.specialistCategoryName?.analyticValidatedLength() ?: "")
        }
    }

    override fun trackingEventBookingSchedule(
        context: Context,
        bookingSchedulePayloadBuilder: BookingSchedulePayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.BOOKING_SCHEDULE) {
            param(AnalyticsConstants.KEY.HOSPITAL_ID, bookingSchedulePayloadBuilder.hospitalId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.HOSPITAL_NAME, bookingSchedulePayloadBuilder.hospitalName?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.BOOKING_DATE, bookingSchedulePayloadBuilder.bookingDate?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.BOOKING_HOUR, bookingSchedulePayloadBuilder.bookingHour?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.DOCTOR_ID, bookingSchedulePayloadBuilder.doctorId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.DOCTOR_NAME, bookingSchedulePayloadBuilder.doctorName?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.DOCTOR_SPECIALITY, bookingSchedulePayloadBuilder.doctorSpeciality?.analyticValidatedLength() ?: "")
        }
    }

    override fun trackingEventEndCallMa(
        context: Context,
        endCallMaPayloadBuilder: EndCallMaPayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.MA_CALL) {
            param(AnalyticsConstants.KEY.APPOINTMENT_ID, endCallMaPayloadBuilder.appointmentId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.ORDER_CODE, endCallMaPayloadBuilder.orderCode?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.ROOM_CODE, endCallMaPayloadBuilder.roomCode?.analyticValidatedLength() ?: "")
        }
    }

    override fun trackingEventSearchResult(
        context: Context,
        searchResultPayloadBuilder: SearchResultPayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.SEARCH_RESULT) {
            param(AnalyticsConstants.KEY.SPECIALIST_CATEGORY, searchResultPayloadBuilder.specialistCategory?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.FILTER_SYMPTOM, searchResultPayloadBuilder.symptom?.analyticValidatedLength() ?: "")
            firebaseAnalytics.setUserProperty(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_SEARCH, searchResultPayloadBuilder.specialistCategory?.analyticValidatedLength())
        }
    }

    override fun trackingEventDoctorCall(
        context: Context,
        doctorCallPayloadBuilder: DoctorCallPayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.DOCTOR_CALL) {
            param(AnalyticsConstants.KEY.DOCTOR_ID, doctorCallPayloadBuilder.doctorId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.HOSPITAL_ID, doctorCallPayloadBuilder.hospitalId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.HOSPITAL_NAME, doctorCallPayloadBuilder.hospitalName?.analyticValidatedLength() ?: "")
            firebaseAnalytics.setUserProperty(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_CONSULTATION_DATE, doctorCallPayloadBuilder.lastConsultationDate?.analyticValidatedLength())
            firebaseAnalytics.setUserProperty(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_HOSPITAL_PICK, doctorCallPayloadBuilder.hospitalName?.analyticValidatedLength())
            firebaseAnalytics.setUserProperty(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_SPECIALIST_PICK, doctorCallPayloadBuilder.doctorSpecialist?.analyticValidatedLength())
        }
    }

    override fun trackingEventDoctorProfile(
        context: Context,
        doctorProfilePayloadBuilder: DoctorProfilePayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.DOCTOR_PROFILE) {
            param(AnalyticsConstants.KEY.DOCTOR_ID, doctorProfilePayloadBuilder.doctorId?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.DOCTOR_NAME, doctorProfilePayloadBuilder.doctorName?.analyticValidatedLength() ?: "")
            param(AnalyticsConstants.KEY.SPECIALITY, doctorProfilePayloadBuilder.speciality?.analyticValidatedLength() ?: "")
        }
    }

    override fun trackingEventMedicalNotes(
        context: Context,
        medicalNotesPayloadBuilder: MedicalNotesPayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.MEDICAL_NOTES) {}
    }

    override fun trackingLastDoctorChatName(context: Context, doctorName: String?) {
        firebaseAnalytics.setUserProperty(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_DOCTOR_CHAT_NAME, doctorName?.analyticValidatedLength())
    }

    override fun trackingLastOpenTime(context: Context) {
        firebaseAnalytics.setUserProperty(AnalyticsConstants.CUSTOM_ATTRIBUTES.LAST_OPEN_TIME, customDateFormat())
    }

    override fun trackingEventChoosingDay(context: Context, choosingDayPayloadBuilder: ChoosingDayPayloadBuilder) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.CHOOSING_DAY) {
            param(AnalyticsConstants.KEY.CHOOSING_DAY, choosingDayPayloadBuilder.day?.analyticValidatedLength() ?: "")
        }
    }

    override fun trackingEventParameterGeneralSearch(
        context: Context,
        parameterGeneralSearchPayloadBuilder: ParameterGeneralSearchPayloadBuilder
    ) {
        firebaseAnalytics.logEvent(AnalyticsConstants.FIREBASE_OR_FACEBOOK_EVENT.PARAMETER_SEARCH) {
            param(AnalyticsConstants.KEY.SEARCH_RESULT, parameterGeneralSearchPayloadBuilder.searchResult?.analyticValidatedLength() ?: "")
        }
    }

    override fun trackingCity(context: Context, city: String?) {
        firebaseAnalytics.setUserProperty(AnalyticsConstants.CUSTOM_ATTRIBUTES.CITY, city)
    }

}