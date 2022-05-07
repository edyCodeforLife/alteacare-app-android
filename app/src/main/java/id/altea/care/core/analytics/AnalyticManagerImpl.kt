package id.altea.care.core.analytics

import android.app.Application
import android.content.Context
import id.altea.care.core.analytics.payload.DoctorProfilePayloadBuilder
import id.altea.care.core.analytics.payload.DoctorCallPayloadBuilder
import id.altea.care.core.analytics.payload.LoginSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.RegisterSuccessPayloadBuilder
import id.altea.care.core.analytics.payload.*
import id.altea.care.core.analytics.product.AnalyticProduct
import id.altea.care.core.analytics.product.facebook.FacebookProduct
import id.altea.care.core.analytics.product.firebase.GtmProduct
import id.altea.care.core.analytics.product.moengage.MoengageProduct
import timber.log.Timber

class AnalyticManagerImpl(val application: Application) : AnalyticManager {

    private val analyticList: MutableList<AnalyticProduct> = mutableListOf()

    /**
     * Add all analytic product to analytic list,
     * looping all product and initialize using application inject from dagger
     */
    override fun initialize() {
        Timber.i("-> Initialize")
        analyticList.add(FacebookProduct())
        analyticList.add(MoengageProduct())
        analyticList.add(GtmProduct())

        analyticList.forEach {
            it.initialize(application)
            Timber.i(" Initialize is -> $it")
        }

    }

    /**
     * If tracking triggered, loop all product and send event
     */
    override fun trackingUserLogin(loginSuccessPayloadBuilder: LoginSuccessPayloadBuilder) {
        analyticList.forEach {
            it.trackingUserLogin(application, loginSuccessPayloadBuilder)
        }
    }

    override fun trackingEventRegistration(registerSuccessPayloadBuilder: RegisterSuccessPayloadBuilder) {
        analyticList.forEach {
            it.trackingEventRegistration(application, registerSuccessPayloadBuilder)
        }
    }

    override fun trackingEventDoctorProfile(doctorProfilePayloadBuilder: DoctorProfilePayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventDoctorProfile(application, doctorProfilePayloadBuilder)
        }
    }

    override fun trackingEventDoctorCall(doctorCallPayloadBuilder: DoctorCallPayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventDoctorCall(application, doctorCallPayloadBuilder)
        }
    }

    override fun trackingEventBookingSchedule(bookingSchedulePayloadBuilder: BookingSchedulePayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventBookingSchedule(application, bookingSchedulePayloadBuilder)
        }
    }

    override fun trackingEventEndCallMa(
        endCallMaPayloadBuilder: EndCallMaPayloadBuilder
    ) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventEndCallMa(application,endCallMaPayloadBuilder)
        }
    }

    override fun trackingEventSearchResult(searchResultPayloadBuilder: SearchResultPayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventSearchResult(application,searchResultPayloadBuilder)
        }
    }

    override fun trackingEventSpecialistCategory(specialistCategoryPayloadBuilder: SpecialistCategoryPayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventSpecialistCategory(application, specialistCategoryPayloadBuilder)
        }
    }

    override fun trackingEventMedicalNotes(medicalNotesPayloadBuilder: MedicalNotesPayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventMedicalNotes(application, medicalNotesPayloadBuilder)
        }
    }

    override fun trackingLastDoctorChatName(doctorName: String?) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingLastDoctorChatName(application, doctorName)
        }
    }

    override fun trackingLastOpenTime() {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingLastOpenTime(application)
        }
    }

    override fun trackingEventChoosingDay(choosingDayPayloadBuilder: ChoosingDayPayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventChoosingDay(application, choosingDayPayloadBuilder)
        }
    }

    override fun trackingEventParameterGeneralSearch(parameterGeneralSearchPayloadBuilder: ParameterGeneralSearchPayloadBuilder) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingEventParameterGeneralSearch(application, parameterGeneralSearchPayloadBuilder)
        }
    }

    override fun trackingCity(city: String?) {
        analyticList.forEach { analyticProduct ->
            analyticProduct.trackingCity(application, city)
        }
    }

}