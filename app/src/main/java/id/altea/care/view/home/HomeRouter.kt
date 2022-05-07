package id.altea.care.view.home

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.*
import id.altea.care.view.common.bottomsheet.AddAccountOrCreateBottomSheet
import id.altea.care.view.common.bottomsheet.MaOperationalHourBottomSheet
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultation.ConsultationRouter
import id.altea.care.view.consultation.PageType
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.generalsearch.GeneralSearchActivity
import id.altea.care.view.home.bottomsheet.LiveChatBottomSheet
import id.altea.care.view.home.bottomsheet.SwitchAccountBottomSheet
import id.altea.care.view.login.LoginActivityRouter
import id.altea.care.view.notification.NotificationActivityRouter
import id.altea.care.view.payment.paymentinformation.PaymentInformationActivity
import id.altea.care.view.promotion.detail.PromotionDetailActivity
import id.altea.care.view.promotion.group.PromotionGroupActivity
import id.altea.care.view.reconsultation.ReconsultationActivity
import id.altea.care.view.register.contactinfo.RegisterContactRouter
import id.altea.care.view.register.contactinfo.RegisterContactState
import id.altea.care.view.specialistsearch.SpecialistSearchActivity
import id.altea.care.view.transition.specialist.TransitionSpecialistActivity
import id.altea.care.view.webview.WebViewActivity
import zendesk.support.request.RequestActivity
import zendesk.support.requestlist.RequestListActivity
import java.util.*

class HomeRouter {

    fun openGeneralSearch(source: Context) {
        source.startActivity(GeneralSearchActivity.createIntent(source))
    }

    fun openSpecialistSearch(context: Context, specialistId: String) {
        context.startActivity(SpecialistSearchActivity.createIntent(context, specialistId, ""))
    }

    fun openNotification(context: Context) {
        context.startActivity(NotificationActivityRouter.createIntent(context))
    }

    fun openLogin(context: Context) {
        context.startActivity(LoginActivityRouter.createIntent(context))
    }

    fun openDetailPromotion(source: Activity,promotionId : Int){
        source.startActivity(PromotionDetailActivity.createIntent(source,promotionId))
    }

    fun openConsultationDetail(
        activity: Activity,
        typeAppointment: TypeAppointment,
        consultationId: Int
    ) {
        activity.startActivity(
            ConsultationDetailActivity.createIntent(
                activity,
                typeAppointment,
                consultationId
            )
        )
    }

    fun openConsultation(
        actvity: Activity,
        pageType: PageType,
        dataConsultation: Consultation,
        orderCode: String?,
        appointmentId: Int?
    ) {
        actvity.startActivity(
            ConsultationRouter.createIntent(
                actvity,
                pageType,
                dataConsultation,
                orderCode,
                appointmentId
            )
        )
    }

    fun openPaymentInformation(
        activity: AppCompatActivity,
        appointmentId: Int,
        url: String,
        orderCode: String
    ) {
        activity.startActivity(
            PaymentInformationActivity.createIntent(
                activity,
                appointmentId,
                url,
                orderCode
            )
        )
    }

    fun openReconsultationWithMA(
        activity: AppCompatActivity,
        appointmentId: Int,
        orderCode: String,
        profile: UserProfile,
        infoDetail: InfoDetail?
    ) {
        activity.startActivity(
            ReconsultationActivity.createIntent(activity, appointmentId, orderCode, profile,infoDetail)
        )
    }

    fun openCallWithSpecialist(
        activity: AppCompatActivity,
        appointmentId: Int,
        doctor: ConsultationDoctor?,
        infoDetail: InfoDetail?
    ) {
        activity.startActivity(
            TransitionSpecialistActivity.createIntent(activity, appointmentId, doctor, infoDetail)
        )
    }

    fun openRegistration(source: Context) {
        source.startActivity(
            RegisterContactRouter.createIntent(
                source,
                null,
                null,
                RegisterContactState.PAGE_REGISTER,
                null
            ))
    }

    fun openSwitchAccount(
        fragmentManager: FragmentManager,
        accountList: List<Account>,
        submitCallback: (Account) -> Unit,
        addAccountCallback: () -> Unit
    ) {
        SwitchAccountBottomSheet.newInstance(ArrayList(accountList), submitCallback, addAccountCallback)
            .show(fragmentManager, "switch_account")
    }

    fun openBottomSheetCreateOrAddAccount(
        fragmentManager: FragmentManager,
        addAccountCallback: () -> Unit,
        createAccountCallback: () -> Unit
    ) {
        AddAccountOrCreateBottomSheet.newInstance(addAccountCallback, createAccountCallback)
            .show(fragmentManager, "add_create_account")
    }

    fun openMaOperationalHourBottomSheet(
        fragmentManager: FragmentManager?,
        operationalHour : String
    ) {
        fragmentManager?.let { fragManager ->
            MaOperationalHourBottomSheet
                .newInstance(operationalHour)
                .show(fragManager,"ma operational hour")
        }
    }

    fun openWebView(context: Context, url: String, cookie : String? = null) {
        context.startActivity(WebViewActivity.createIntent(context, url,cookie))
    }

    fun openRequestListLiveChatActivity(source: Activity,tag : String) {
        val requestActivityConfig = RequestActivity.builder()
            .withTags(tag)
            .config()

        RequestListActivity.builder()
            .show(source, requestActivityConfig)
    }

    fun openLiveChatBottomSheet(
        fragmentManager: FragmentManager,
        profile: Profile?
    ) {
        LiveChatBottomSheet.newInstance(profile).show(fragmentManager, "live_chat")
    }

    fun openPromotionGroupActivity(source: Activity){
        source.startActivity(PromotionGroupActivity.createIntent(source))
    }
}
