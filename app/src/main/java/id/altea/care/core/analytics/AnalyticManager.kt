package id.altea.care.core.analytics

import id.altea.care.core.analytics.payload.DoctorProfilePayloadBuilder
import id.altea.care.core.analytics.payload.LoginSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.RegisterSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.*

interface AnalyticManager {

    fun initialize()

    fun trackingUserLogin(loginSuccessPayloadBuilder: LoginSuccessPayloadBuilder)

    fun trackingEventRegistration(registerSuccessPayloadBuilder: RegisterSuccessPayloadBuilder)

    fun trackingEventDoctorProfile(doctorProfilePayloadBuilder: DoctorProfilePayloadBuilder)

    fun trackingEventDoctorCall(doctorCallPayloadBuilder: DoctorCallPayloadBuilder)

    fun trackingEventSpecialistCategory(specialistCategoryPayloadBuilder: SpecialistCategoryPayloadBuilder)

    fun trackingEventBookingSchedule(bookingSchedulePayloadBuilder: BookingSchedulePayloadBuilder)

    fun trackingEventEndCallMa(endCallMaPayloadBuilder : EndCallMaPayloadBuilder)

    fun trackingEventSearchResult(searchResultPayloadBuilder: SearchResultPayloadBuilder)

    fun trackingEventMedicalNotes(medicalNotesPayloadBuilder: MedicalNotesPayloadBuilder)

    fun trackingLastDoctorChatName(doctorName: String?)

    fun trackingLastOpenTime()

    fun trackingEventChoosingDay(choosingDayPayloadBuilder: ChoosingDayPayloadBuilder)

    fun trackingEventParameterGeneralSearch(parameterGeneralSearchPayloadBuilder: ParameterGeneralSearchPayloadBuilder)

    fun trackingCity(city: String?)

}