package id.altea.care.view.deeplink

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import id.altea.care.R
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.common.enums.TypeMyConsultation
import id.altea.care.view.common.enums.TypeNotification
import id.altea.care.view.common.enums.TypeNotification.*
import id.altea.care.view.common.enums.TypeTeleconsultation
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import android.net.UrlQuerySanitizer
import android.net.UrlQuerySanitizer.ParameterValuePair

import java.util.HashMap




class DeepLinkManagerImpl(context: Context) : DeepLinkManager {

    private val resources = context.resources
    private var deepLinkUrl: Uri? = null

    private val router by lazy { DeepLinkRouter() }

    private val deepLinkAppointment get() = resources.getString(R.string.deep_link_host_appointment)
    private val deepLinkDetailConsultation get() = resources.getString(R.string.str_detail_consultation)
    private val deepLinkDoctor get() = resources.getString(R.string.deep_link_doctor)
    private val deepLinkWidgets get() = resources.getString(R.string.deep_link_home_widgets)
    private val deepLinkRegistration get() = resources.getString(R.string.deep_link_registration)
    private val deepLinkAccount get() = resources.getString(R.string.deep_link_account)
    private val deepLinkVaccine get() = resources.getString(R.string.deep_link_covid_vaccine)
    private val deepLinkPromotion get() = resources.getString(R.string.deep_link_promotion)
    private val deepLinkSpecialization get() = resources.getString(R.string.deep_link_specialization)

    override fun setDeepLinkUri(uri: Uri) {
        deepLinkUrl = uri
    }

    override fun getDeepLinkUri(): Uri? = deepLinkUrl

    override fun executeDeepLink(source: Activity) {
        executeDeepLink(deepLinkUrl, source)
    }

    override fun executeDeepLink(uri: Uri?, source: Activity) {
        when {
            isAppointmentDeepLink(uri) -> {
                val appointmentId = getAppointmentIdDeepLink(uri)

                when (getTypeNotification(uri)) {
                    PUSH_NOTIFICATION_APPOINTMENT_WAITING_FOR_PAYMENT -> {
                        router.openPaymentPage(source, appointmentId)
                    }
                    PUSH_NOTIFICATION_APPOINTMENT_PAYMENT_SUCCESS -> {
                        router.openPaymentSuccess(source, appointmentId)
                    }
                    PUSH_NOTIFICATION_APPOINTMENT_CANCELED_BY_GP,
                    PUSH_NOTIFICATION_APPOINTMENT_CANCELED_BY_SYSTEM,
                    PUSH_NOTIFICATION_APPOINTMENT_REFUNDED -> {
                        router.openDetailAppointmentCancel(source, appointmentId)
                    }
                    PUSH_NOTIFICATION_APPOINTMENT_MEET_SPECIALIST -> {
                        router.openDetailAppointment(
                            source,
                            TypeAppointment.MEET_SPECIALIST,
                            appointmentId,
                        )
                    }
                    PUSH_NOTIFICATION_APPOINTMENT_COMPLETED -> {
                        router.openDetailAppointment(source, TypeAppointment.COMPLETED, appointmentId)
                    }
                    PUSH_NOTIFICATION_APPOINTMENT_WILL_ENDED_IN_10MINUTES ->{
                        router.openMyConsultation(source)
                    }
                    PUSH_NOTIFICATION_APPOINTMENT_15MINUTES_BEFORE_MEET_SPECIALIST ->{
                        router.openDetailAppointment(source,TypeAppointment.PAID,appointmentId)
                    }
                    PUSH_NOTIFICATION_APPOINTMENT_SCHEDULE_CHANGED,
                    PUSH_NOTIFICATION_APPOINTMENT_SPECIALIST_CHANGED -> {
                        router.openMyConsultation(source)
                    }
                    else -> {
                        // todo after click notification
                    }
                }
            }
            isDoctorDeepLink(uri) ->{

                if (doctorTypeDeepLink(uri)){
                    val doctorId = getDoctorIdDeepLink(uri)
                    router.openDetailDoctor(source,doctorId)
                }else{
                   val specialistIdList =  getSpecialicationsDeepLink(uri)
                   val hospitalIdList = getHospitalsDeepLink(uri)

                    router.openSearchDoctorSpecialist(
                        source,specialistIdList?.toTypedArray(),
                        hospitalIdList?.toTypedArray()
                    )
                }
            }
            isSpecilizationDeepLink(uri) ->{
                router.openDoctorSpecialist(source)
            }
            isTeleconsultationDeepLink(uri)->{
                when(getTypeTeleconsultation(uri)){
                    TypeTeleconsultation.list ->{
                        router.openMyConsultation(source,getStatusMyConsultation(uri),getDateMyConsultation(uri))
                    }
                    TypeTeleconsultation.detail ->{
                        router.openDetailAppointment(source,TypeAppointment.COMPLETED,getTeleconsultationIdDeepLink(uri),getConsultationTabDeepLink(uri))
                    }
                }
            }
            isWidgetsHome(uri) -> {
                if (getTypeWidgets(uri) == TELEKONSULTASI_DOKTER){
                    router.openDoctorSpecialist(source)
                }else{
                    router.openMainActivity(source)
                }
            }
            isRegistrationDeepLink(uri) -> {
                when(getRegistrationDeepLink(uri)){
                    "contactInfo" -> router.openRegistration(source)
                }
            }
            isAccountDeepLink(uri) -> {
                when(getAccountDeepLink(uri)){
                    "tnc" -> {
                        router.openTermsAndCondition(source)
                    }
                    else -> {
                        router.openAccount(source)
                    }
                }
            }
            isCovidVaccineDeepLink(uri) -> {

            }
            isPromotionDeepLink(uri) -> {
                when(getPromotionDeepLink(uri)){
                    "list" -> {
                       if (getPromotionCategory(uri)?.isNotEmpty() == true){
                           router.openPromotionTeleconsultation(source,getPromotionCategory(uri).toString())
                       }else{
                           router.openPromotionListGroup(source)
                       }
                    }
                    "detail" -> {
                        router.openPromotionDetail(source,getPromotionPromotionId(uri) ?: 0)
                    }
                }
            }
        }


        invalidate()
    }

    override fun invalidate() {
        deepLinkUrl = null
    }

    private fun isRegistrationDeepLink(uri: Uri?) : Boolean {
        return uri?.host == deepLinkRegistration
    }

    private fun isAccountDeepLink(uri: Uri?) : Boolean{
        return  uri?.host == deepLinkAccount
    }

    private fun isCovidVaccineDeepLink(uri: Uri?) : Boolean {
        return uri?.host == deepLinkVaccine
    }

    private fun isAppointmentDeepLink(uri: Uri?): Boolean {
        return uri?.host == deepLinkAppointment
    }

    private fun isDoctorDeepLink(uri : Uri?) : Boolean{
        return uri?.host == deepLinkDoctor
    }

    private fun isSpecilizationDeepLink(uri: Uri?) : Boolean{
        return uri?.host == deepLinkSpecialization
    }

    private fun isWidgetsHome(uri: Uri?) : Boolean {
        return uri?.host == deepLinkWidgets
    }

    private fun isTeleconsultationDeepLink(uri : Uri?) : Boolean{
        return uri?.host == deepLinkDetailConsultation
    }

    private fun getDoctorIdDeepLink(uri: Uri?) : String {
        val pathQueryParam = uri?.getQueryParameter("doctorId")

        return pathQueryParam?.toString() ?: ""

    }

    private fun doctorTypeDeepLink(uri: Uri?) : Boolean{
        return if(uri?.lastPathSegment.toString().equals("detail") == true){
            true
        }else{
            false
        }
    }

    private fun getSpecialicationsDeepLink(uri: Uri?) : MutableList<String>? {
        val pathQueryParam = uri?.getQueryParameters("specializationIds")


        return pathQueryParam
    }

    private fun getHospitalsDeepLink(uri: Uri?) : MutableList<String>? {
        val pathQueryParam = uri?.getQueryParameters("hospitalIds")

        return pathQueryParam
    }

    private fun isPromotionDeepLink(uri: Uri?) : Boolean{
        return uri?.host == deepLinkPromotion
    }


    private fun getPromotionDeepLink(uri: Uri?) : String {
        val pathSegment = uri?.pathSegments ?: listOf()
        return if (pathSegment.isNotEmpty()  ){
            pathSegment[0].toString()
        }else{
            ""
        }
    }

    private fun getRegistrationDeepLink(uri: Uri?) : String {
        val pathSegment = uri?.pathSegments ?: listOf()
        return if (pathSegment.isNotEmpty()){
            pathSegment[0].toString()
        }else{
            ""
        }
    }

    private fun getAccountDeepLink(uri: Uri?) : String{
        val pathSegment = uri?.pathSegments ?: listOf()
        return if (pathSegment.isNotEmpty()){
            pathSegment[0].toString()
        }else{
            ""
        }
    }

    private fun getAppointmentIdDeepLink(uri: Uri?): Int {
        val pathSegement = uri?.pathSegments ?: listOf()
        return if (pathSegement.isNotEmpty() && pathSegement.size > 1) {
            pathSegement[1].toInt()
        } else {
            0
        }
    }

    private fun getTeleconsultationIdDeepLink(uri: Uri?) : Int? {
        val pathQueryParam = uri?.getQueryParameter("teleconsultationId")

        return pathQueryParam?.toInt()
    }

    private fun getStatusMyConsultation(uri: Uri?) : Int?{
        val pathQueryParam = uri?.getQueryParameter("status")

       return when(TypeMyConsultation.valueOf(pathQueryParam ?: "ongoing")){
            TypeMyConsultation.ongoing -> 0
            TypeMyConsultation.history -> 1
            TypeMyConsultation.canceled -> 2
        }
    }

    private fun getPromotionCategory(uri: Uri?) : String? {
        val pathQueryParam = uri?.getQueryParameter("promotionType")

        return pathQueryParam
    }

    private fun getPromotionPromotionId(uri: Uri?) : Int? {
        val pathQueryParam = uri?.getQueryParameter("promotionId")

        return pathQueryParam?.toInt()
    }

    private fun getDateMyConsultation(uri: Uri?) : String?{
        val pathQueryParam = uri?.getQueryParameter("date")

        return pathQueryParam.toString()
    }

    private fun getConsultationTabDeepLink(uri: Uri?) : Int? {
        val pathQueryParam = uri?.getQueryParameter("tab")
        return when(pathQueryParam){
            "payment" -> 3
            "documents" ->2
            "resume"->1
            else -> 0
        }

    }

    private fun getTypeTeleconsultation(uri: Uri?) : TypeTeleconsultation? {
        val pathSegment = uri?.pathSegments ?: listOf()
        return if (pathSegment.isNotEmpty())
            TypeTeleconsultation.valueOf(pathSegment[0].toString())
        else
            null
    }


    private fun getTypeNotification(uri: Uri?): TypeNotification? {
        val pathSegment = uri?.pathSegments ?: listOf()
        return if (pathSegment.isNotEmpty())
            valueOf(pathSegment[0].toString())
        else
            null
    }

    private fun getTypeWidgets(uri: Uri?) : String {
        val pathSegment = uri?.pathSegments ?: emptyList()
        return if (pathSegment.isNotEmpty())
            pathSegment[0].toString()
        else ""
    }

    companion object {
        private const val TELEKONSULTASI_DOKTER = "telekonsultasi"
    }
}
