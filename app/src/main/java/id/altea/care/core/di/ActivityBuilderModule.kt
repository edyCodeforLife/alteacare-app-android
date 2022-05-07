package id.altea.care.core.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import id.altea.care.view.account.AccountFragmentProvider
import id.altea.care.view.account.changeprofile.ChangeProfileActivity
import id.altea.care.view.account.contact.ContactServicesActivity
import id.altea.care.view.account.setting.SettingAccountActivity
import id.altea.care.view.account.termandcondition.TermConditionAccount
import id.altea.care.view.address.addressform.AddressFormActivity
import id.altea.care.view.address.addresslist.AddressListActivity
import id.altea.care.view.address.addresslist.bottomsheet.AddressListBottomSheetModule
import id.altea.care.view.cancelstatusorder.CancelStatusActvity
import id.altea.care.view.changepassword.ChangePasswordActivity
import id.altea.care.view.changephonenumber.ChangePhoneNumberActivity
import id.altea.care.view.changephonenumber.otpphone.PhoneOtpActivity
import id.altea.care.view.common.ui.searchselectable.SearchSelectableActivity
import id.altea.care.view.consultation.ConsultationActivity
import id.altea.care.view.consultation.ConsultationFragmentProvider
import id.altea.care.view.consultation.confirm.ConfirmationFragmentFragmentProvider
import id.altea.care.view.consultation.payment.PaymentFragmentProvider
import id.altea.care.view.consultation.voucher.VoucherActivity
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.consultationdetail.canceled.ConsultationCancelActivity
import id.altea.care.view.consultationdetail.consultationcost.ConsultationCostProvider
import id.altea.care.view.consultationdetail.medicaldocument.MedicalDocumentProvider
import id.altea.care.view.consultationdetail.medicalresume.MedicalResumeProvider
import id.altea.care.view.consultationdetail.patientdata.PatientDataProvider
import id.altea.care.view.contact.ContactActivity
import id.altea.care.view.doctordetail.DoctorDetailActivity
import id.altea.care.view.doctordetail.bottomsheet.FamilyMemberListBottomSheetModule
import id.altea.care.view.endcall.EndCallActivity
import id.altea.care.view.endcall.specialist.EndCallSpecialistActivity
import id.altea.care.view.family.detailfamily.DetailFamilyActivity
import id.altea.care.view.family.formfamily.FormFamilyActivity
import id.altea.care.view.family.listfamily.ListFamilyActivity
import id.altea.care.view.family.listfamily.bottomsheet.FamilyListBottomSheetModule
import id.altea.care.view.faq.FaqActivity
import id.altea.care.view.forceupdate.ForceUpdateActivity
import id.altea.care.view.forgotpassword.ForgotPasswordActivity
import id.altea.care.view.generalsearch.GeneralSearchActivity
import id.altea.care.view.generalsearch.SpecialistCategoryActivity
import id.altea.care.view.generalsearch.SymptomListActivity
import id.altea.care.view.home.HomeFragmentProvider
import id.altea.care.view.login.LoginActivity
import id.altea.care.view.login.LoginGuestActivity
import id.altea.care.view.mabusy.MaBusyActivity
import id.altea.care.view.main.MainActivity
import id.altea.care.view.myconsultation.MyConsultationProvider
import id.altea.care.view.notification.NotificationActivity
import id.altea.care.view.onboarding.OnboardingActivity
import id.altea.care.view.onboarding.OnboardingStartNowActivty
import id.altea.care.view.onboarding.OnboardingWelcomeActivity
import id.altea.care.view.otpmail.EmailOtpActivity
import id.altea.care.view.otpmail.changemail.ChangeEmailActivity
import id.altea.care.view.password.PasswordActivity
import id.altea.care.view.payment.paymentinformation.PaymentInformationActivity
import id.altea.care.view.payment.paymentmethod.PaymentMethodActivity
import id.altea.care.view.payment.paymentsuccess.PaymentSuccessActivity
import id.altea.care.view.promotion.detail.PromotionDetailActivity
import id.altea.care.view.promotion.group.PromotionGroupActivity
import id.altea.care.view.promotion.teleconsultation.PromotionTeleconsultationActivity
import id.altea.care.view.reconsultation.ReconsultationActivity
import id.altea.care.view.register.contactinfo.RegisterContactActivity
import id.altea.care.view.register.personalinfo.RegisterPersonalInfoActivity
import id.altea.care.view.register.termcondition.TermConditionActivity
import id.altea.care.view.register.transition.SuccessRegisterActivity
import id.altea.care.view.smsotp.SmsOtpActivity
import id.altea.care.view.specialist.SpecialistFragmentProvider
import id.altea.care.view.specialistsearch.SpecialistSearchActivity
import id.altea.care.view.specialistsearch.bottomsheet.module.SpecialistFilterBottomSheetModule
import id.altea.care.view.splashscreen.SplashActivity
import id.altea.care.view.transition.TransitionActivity
import id.altea.care.view.transition.specialist.TransitionSpecialistActivity
import id.altea.care.view.webview.WebViewActivity
import id.altea.care.view.videocall.VideoCallActivity
import id.altea.care.view.videocall.chat.ChatFragmentProvider
import id.altea.care.view.videocall.videofragment.VideoFragmentProvider
import id.altea.care.view.videocall.videowebview.VideoFragmentWebView
import id.altea.care.view.videocall.videowebview.VideoFragmentWebViewProvider
import id.altea.care.view.viewdocument.ViewDocumentActivity
import id.altea.care.view.viewdocument.ViewDocumentDownloadActivity

@Suppress("unused")
@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    abstract fun bindLoginGuestActivity(): LoginGuestActivity

    @ContributesAndroidInjector
    abstract fun bindPaymentMethod(): PaymentMethodActivity

    @ContributesAndroidInjector
    abstract fun bindRegisterPasswordActivity(): PasswordActivity

    @ContributesAndroidInjector
    abstract fun bindEmailOtpActivity(): EmailOtpActivity

    @ContributesAndroidInjector
    abstract fun bindForgotPasswordActivity(): ForgotPasswordActivity

    @ContributesAndroidInjector
    abstract fun bindContactActivity(): ContactActivity

    @ContributesAndroidInjector
    abstract fun bindCancelStatusActivity(): CancelStatusActvity

    @ContributesAndroidInjector
    abstract fun bindReconsultationActivity() : ReconsultationActivity

    @ContributesAndroidInjector(
        modules = [
            ChatFragmentProvider::class,
            VideoFragmentProvider::class,
            VideoFragmentWebViewProvider::class
        ]
    )
    abstract fun bindVideoActivity(): VideoCallActivity

    @ContributesAndroidInjector
    abstract fun bindEndCallActivity(): EndCallActivity

    @ContributesAndroidInjector
    abstract fun bindTermConditionActivity(): TermConditionActivity

    @ContributesAndroidInjector
    abstract fun bindTransitionVideoActivity(): TransitionActivity


    @ContributesAndroidInjector(
        modules = [
            FamilyMemberListBottomSheetModule::class,
            ConsultationFragmentProvider::class,
            ConfirmationFragmentFragmentProvider::class,
            PaymentFragmentProvider::class
        ]
    )
    abstract fun bindConsultationActivity(): ConsultationActivity

    @ContributesAndroidInjector(
        modules = [
            HomeFragmentProvider::class,
            SpecialistFragmentProvider::class,
            AccountFragmentProvider::class,
            MyConsultationProvider::class
        ]
    )
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [SpecialistFilterBottomSheetModule::class])
    abstract fun bindSpecialisSearchActivity(): SpecialistSearchActivity

    @ContributesAndroidInjector
    abstract fun bindNotificationActivity(): NotificationActivity

    @ContributesAndroidInjector(modules = [FamilyMemberListBottomSheetModule::class])
    abstract fun bindDoctorDetailActivity(): DoctorDetailActivity

    @ContributesAndroidInjector(
        modules = [
            PatientDataProvider::class,
            MedicalResumeProvider::class,
            MedicalDocumentProvider::class,
            ConsultationCostProvider::class
        ]
    )

    abstract fun bindConsultationDetail(): ConsultationDetailActivity

    @ContributesAndroidInjector
    abstract fun bindGeneralSearchActivity(): GeneralSearchActivity


    @ContributesAndroidInjector
    abstract fun bindChangeProfileActivity(): ChangeProfileActivity

    @ContributesAndroidInjector
    abstract fun bindSettingAccountActivity(): SettingAccountActivity

    @ContributesAndroidInjector
    abstract fun bindChangePasswordActivity(): ChangePasswordActivity

    @ContributesAndroidInjector
    abstract fun bindFaqActivity(): FaqActivity

    @ContributesAndroidInjector
    abstract fun bindTermConditionAccount(): TermConditionAccount

    @ContributesAndroidInjector
    abstract fun bindChangeEmailActivity(): ChangeEmailActivity

    @ContributesAndroidInjector
    abstract fun bindSelectableActivity(): SearchSelectableActivity

    @ContributesAndroidInjector
    abstract fun bindRegisterPersonalInfoActivity(): RegisterPersonalInfoActivity

    @ContributesAndroidInjector
    abstract fun bindTransitionSpecialistActivity(): TransitionSpecialistActivity

    @ContributesAndroidInjector
    abstract fun bindEndCallSpecialistActivity(): EndCallSpecialistActivity

    @ContributesAndroidInjector
    abstract fun bindPaymentInformationActivity(): PaymentInformationActivity

    @ContributesAndroidInjector
    abstract fun bindChangePhoneNumberActivity() : ChangePhoneNumberActivity

    @ContributesAndroidInjector
    abstract fun bindPhoneNumberOtpActivity() : PhoneOtpActivity

    @ContributesAndroidInjector
    abstract fun bindPaymentSuccessActivity(): PaymentSuccessActivity

    @ContributesAndroidInjector
    abstract fun bindAppointmentCancelActivity(): ConsultationCancelActivity

    @ContributesAndroidInjector
    abstract fun bindViewDocumentActivity() : ViewDocumentActivity

    @ContributesAndroidInjector
    abstract fun bindAddFamilyContactActivity() : FormFamilyActivity

    @ContributesAndroidInjector(modules = [AddressListBottomSheetModule::class])
    abstract fun bindViewAddressListActivity() : AddressListActivity


    @ContributesAndroidInjector
    abstract fun bindAddressFormActivity(): AddressFormActivity

    @ContributesAndroidInjector(modules = [FamilyListBottomSheetModule::class])
    abstract fun bindListFamilyActivity() : ListFamilyActivity

    @ContributesAndroidInjector
    abstract fun bindDetailFamilyActivity() : DetailFamilyActivity

    @ContributesAndroidInjector
    abstract fun bindRegisterContactActivity() : RegisterContactActivity

    @ContributesAndroidInjector
    abstract fun bindVoucherActivity() : VoucherActivity

    @ContributesAndroidInjector
    abstract fun bindSmsOtpActivity(): SmsOtpActivity

    @ContributesAndroidInjector
    abstract fun bindSuccessRegisterActivity(): SuccessRegisterActivity

    @ContributesAndroidInjector
    abstract fun bindForceUpdateActivity() : ForceUpdateActivity

    @ContributesAndroidInjector
    abstract fun bindSpecialistCategoryActivity() : SpecialistCategoryActivity

    @ContributesAndroidInjector
    abstract fun bindSymptomListActivity() : SymptomListActivity

    @ContributesAndroidInjector
    abstract fun bindContactServicesActivity(): ContactServicesActivity

    @ContributesAndroidInjector
    abstract fun bindWebViewActivity() : WebViewActivity

    @ContributesAndroidInjector
    abstract fun bindDocumentDownloadActivity() : ViewDocumentDownloadActivity

    @ContributesAndroidInjector
    abstract fun bindPromotionGroupActivity() : PromotionGroupActivity

    @ContributesAndroidInjector
    abstract fun bindPromotionTeleconsultationActivity() : PromotionTeleconsultationActivity

    @ContributesAndroidInjector
    abstract fun bindPromotionDetailActivity() : PromotionDetailActivity

    @ContributesAndroidInjector
    abstract fun bindOnBoardingActivity() : OnboardingActivity

    @ContributesAndroidInjector
    abstract fun bindOnBoardingWelcomeActivity() : OnboardingWelcomeActivity

    @ContributesAndroidInjector
    abstract fun bindOnBoardingStartNowActivity() : OnboardingStartNowActivty

    @ContributesAndroidInjector
    abstract fun bindMaBusyActivity() : MaBusyActivity

}