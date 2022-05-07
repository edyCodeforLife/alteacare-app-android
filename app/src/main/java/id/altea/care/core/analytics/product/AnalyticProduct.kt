package id.altea.care.core.analytics.product

import android.app.Application
import android.content.Context
import id.altea.care.core.analytics.payload.DoctorProfilePayloadBuilder
import id.altea.care.core.analytics.payload.DoctorCallPayloadBuilder
import id.altea.care.core.analytics.payload.LoginSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.RegisterSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.*
import id.altea.care.core.analytics.product.moengage.MoengageProduct

interface AnalyticProduct {

    fun initialize(application: Application)

    fun trackingUserLogin(
        context: Context,
        loginSuccessPayloadBuilder: LoginSuccessPayloadBuilder
    )

    fun trackingEventRegistration(
        context: Context,
        registerSuccessPayloadBuilder: RegisterSuccessPayloadBuilder
    )

    fun trackingEventDoctorCall(
        context: Context,
        doctorCallPayloadBuilder: DoctorCallPayloadBuilder
    )
    fun trackingEventSpecialistCategory(
        context: Context,
        specialistCategoryPayloadBuilder: SpecialistCategoryPayloadBuilder
    )

    fun trackingEventBookingSchedule(
        context: Context,
        bookingSchedulePayloadBuilder: BookingSchedulePayloadBuilder
    )

    fun trackingEventEndCallMa(
        context: Context,
        endCallMaPayloadBuilder : EndCallMaPayloadBuilder
    )

    fun trackingEventSearchResult(
        context: Context,
        searchResultPayloadBuilder: SearchResultPayloadBuilder
    )

    fun trackingEventDoctorProfile(
        context: Context,
        doctorProfilePayloadBuilder: DoctorProfilePayloadBuilder
    )

    fun trackingEventMedicalNotes(
        context: Context,
        medicalNotesPayloadBuilder: MedicalNotesPayloadBuilder
    )

    fun trackingLastDoctorChatName(context: Context, doctorName: String?)

    fun trackingLastOpenTime(context: Context)

    fun trackingEventChoosingDay(context: Context, choosingDayPayloadBuilder: ChoosingDayPayloadBuilder)

    fun trackingEventParameterGeneralSearch(context: Context, parameterGeneralSearchPayloadBuilder: ParameterGeneralSearchPayloadBuilder)

    fun trackingCity(context: Context, city: String?)

}