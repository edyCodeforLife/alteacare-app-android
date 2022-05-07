package id.altea.care.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import id.altea.care.core.di.viewmodel.ViewModelFactory
import id.altea.care.core.di.viewmodel.ViewModelKey
import id.altea.care.core.domain.cache.OnboardingCache
import id.altea.care.view.account.AccountFragmentVM
import id.altea.care.view.account.changeprofile.ChangeProfileVM
import id.altea.care.view.account.contact.ContactServicesVM
import id.altea.care.view.account.setting.SettingAccountVM
import id.altea.care.view.account.termandcondition.TermAndConditionAccountVM
import id.altea.care.view.address.addressform.AddressFormVM
import id.altea.care.view.address.addresslist.AddressListVM
import id.altea.care.view.changepassword.ChangePasswordVM
import id.altea.care.view.changephonenumber.ChangePhoneNumberVM
import id.altea.care.view.changephonenumber.otpphone.PhoneOtpVM
import id.altea.care.view.common.ui.searchselectable.SearchSelectableVM
import id.altea.care.view.consultation.ConsultationFragmentVM
import id.altea.care.view.consultation.confirm.ConfirmationFragmentVM
import id.altea.care.view.consultation.payment.PaymentFragmentVM
import id.altea.care.view.consultation.voucher.VoucherVm
import id.altea.care.view.consultationdetail.ConsultationDetailSharedVM
import id.altea.care.view.consultationdetail.canceled.ConsultationCancelVM
import id.altea.care.view.contact.ContactVM
import id.altea.care.view.doctordetail.DoctorDetailVM
import id.altea.care.view.endcall.EndCallVM
import id.altea.care.view.endcall.specialist.EndCallSpecialistVM
import id.altea.care.view.family.detailfamily.DetailFamilyVM
import id.altea.care.view.family.formfamily.FamilyVM
import id.altea.care.view.family.listfamily.ListFamilyVM
import id.altea.care.view.faq.FaqVM
import id.altea.care.view.forceupdate.ForceUpdateVM
import id.altea.care.view.forgotpassword.ForgotPasswordVM
import id.altea.care.view.generalsearch.GeneralSearchVM
import id.altea.care.view.home.HomeFragmentVM
import id.altea.care.view.login.LoginViewModel
import id.altea.care.view.mabusy.MaBusyVM
import id.altea.care.view.main.MainActivityVM
import id.altea.care.view.myconsultation.MyConsultationFragmentVM
import id.altea.care.view.myconsultation.conversation.HistoryConversationVM
import id.altea.care.view.myconsultation.history.cancelfragment.MyConsultationCancelFragmentVM
import id.altea.care.view.myconsultation.history.historyfragment.MyConsultationHistoryVM
import id.altea.care.view.myconsultation.ongoing.OnGoingFragmentVM
import id.altea.care.view.notification.NotificationActivityVM
import id.altea.care.view.onboarding.OnBoardingVM
import id.altea.care.view.otpmail.EmailOtpVM
import id.altea.care.view.otpmail.changemail.ChangeEmailVM
import id.altea.care.view.password.PasswordVM
import id.altea.care.view.payment.paymentinformation.PaymentInfoVM
import id.altea.care.view.payment.paymentmethod.PaymentMethodVM
import id.altea.care.view.payment.paymentsuccess.PaymentSuccessVM
import id.altea.care.view.promotion.detail.PromotionDetailVM
import id.altea.care.view.promotion.group.PromotionVM
import id.altea.care.view.promotion.teleconsultation.PromotionTeleconsultationVM
import id.altea.care.view.reconsultation.ReconsultationVM
import id.altea.care.view.register.contactinfo.RegisterContactVM
import id.altea.care.view.register.personalinfo.RegisterPersonalInfoVM
import id.altea.care.view.register.termcondition.TermConditionVM
import id.altea.care.view.register.transition.SuccesRegisterVM
import id.altea.care.view.smsotp.SmsOtpVM
import id.altea.care.view.specialist.SpecialistFragmentVM
import id.altea.care.view.specialistsearch.SpecialistSearchVM
import id.altea.care.view.splashscreen.SplashVM
import id.altea.care.view.transition.TransitionVM
import id.altea.care.view.transition.specialist.TransitionSpecialistVM
import id.altea.care.view.videocall.VideoCallVM

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginVM(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityVM::class)
    abstract fun bindMainActivity(viewModel: MainActivityVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordVM::class)
    abstract fun bindRegisterPasswordVM(viewModel: PasswordVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EmailOtpVM::class)
    abstract fun bindEmailOtpVM(viewModel: EmailOtpVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordVM::class)
    abstract fun bindForgotPasswordVM(viewModel: ForgotPasswordVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactVM::class)
    abstract fun bindContactVM(viewModel: ContactVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeFragmentVM::class)
    abstract fun bindHomeVM(viewModel: HomeFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConsultationFragmentVM::class)
    abstract fun bindHConsultationVM(viewModel: ConsultationFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SpecialistFragmentVM::class)
    abstract fun bindSpecialistFragmentVM(viewModel: SpecialistFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SpecialistSearchVM::class)
    abstract fun bindSpecialistSearchVM(viewModel: SpecialistSearchVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationActivityVM::class)
    abstract fun bindNotificationVM(viewModel: NotificationActivityVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DoctorDetailVM::class)
    abstract fun bindDoctorDetailVM(viewModel: DoctorDetailVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConfirmationFragmentVM::class)
    abstract fun bindConfirmationVM(viewModelConfirmationFragmentVM: ConfirmationFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentFragmentVM::class)
    abstract fun bindPaymentVM(viewmodPaymentFragmentVM: PaymentFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TermConditionVM::class)
    abstract fun bindTermConditionVM(viewModel: TermConditionVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentMethodVM::class)
    abstract fun bindPaymenMethodtVM(viewmodPaymentMethodVM: PaymentMethodVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoCallVM::class)
    abstract fun bindVideoCallVM(videoCallVM: VideoCallVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EndCallVM::class)
    abstract fun bindEndCallVM(endCallVM: EndCallVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GeneralSearchVM::class)
    abstract fun bindGeneralSearchVM(viewmodel: GeneralSearchVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyConsultationFragmentVM::class)
    abstract fun bindMyConsultationVM(viewModel: MyConsultationFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnGoingFragmentVM::class)
    abstract fun bindOnGoingVM(viewModel: OnGoingFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyConsultationCancelFragmentVM::class)
    abstract fun bindMyConsultationCancelVM(viewModel: MyConsultationCancelFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyConsultationHistoryVM::class)
    abstract fun bindMyConsultationHistoryVM(viewModel: MyConsultationHistoryVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryConversationVM::class)
    abstract fun bindHistoryConversationVM(viewModel: HistoryConversationVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangeProfileVM::class)
    abstract fun bindChangeProfileVM(viewModel: ChangeProfileVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingAccountVM::class)
    abstract fun bindSettingAccountVM(viewModel: SettingAccountVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangePasswordVM::class)
    abstract fun bindChangePasswordVM(viewModel: ChangePasswordVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FaqVM::class)
    abstract fun bindFaqVM(viewModel: FaqVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangeEmailVM::class)
    abstract fun bindChangeEmailVM(viewModel: ChangeEmailVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransitionVM::class)
    abstract fun bindTransitionVM(viewModel: TransitionVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountFragmentVM::class)
    abstract fun bindAccountFragmentVM(viewModel: AccountFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchSelectableVM::class)
    abstract fun bindsearchSelectableVM(viewModel: SearchSelectableVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterPersonalInfoVM::class)
    abstract fun bindRegisterPersonalInfoVM(viewModel: RegisterPersonalInfoVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransitionSpecialistVM::class)
    abstract fun bindTransitionSpecialistVM(viewModel: TransitionSpecialistVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EndCallSpecialistVM::class)
    abstract fun bindEndCallSpecialistVM(viewModel: EndCallSpecialistVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConsultationDetailSharedVM::class)
    abstract fun bindConsultationDetailSharedVM(viewModel: ConsultationDetailSharedVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TermAndConditionAccountVM::class)
    abstract fun bindTermAndConditionAccountVM(viewModel: TermAndConditionAccountVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentInfoVM::class)
    abstract fun bindPaymentInfoVM(viewModel: PaymentInfoVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentSuccessVM::class)
    abstract fun bindPaymentSuccessVM(viewModel: PaymentSuccessVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChangePhoneNumberVM::class)
    abstract fun bindChangePhoneNumberVM(viewModel : ChangePhoneNumberVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PhoneOtpVM::class)
    abstract fun bindPhoneOtpVM(viewModel : PhoneOtpVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConsultationCancelVM::class)
    abstract fun bindConsultationCancelVM(viewModel: ConsultationCancelVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FamilyVM::class)
    abstract fun bindAddFamilyVM(viewModel : FamilyVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddressListVM::class)
    abstract fun bindAddressListVm(viewModel: AddressListVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ListFamilyVM::class)
    abstract fun bindListFamilyVM(viewModel : ListFamilyVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddressFormVM::class)
    abstract fun bindAddressFormVm(viewModel: AddressFormVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailFamilyVM::class)
    abstract fun bindDetailFamily(viewModel : DetailFamilyVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegisterContactVM::class)
    abstract fun bindRegisterContact(viewModel : RegisterContactVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VoucherVm::class)
    abstract fun bindVoucherVm(viewmodel : VoucherVm) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SmsOtpVM::class)
    abstract fun bindSmsOtpVM(viewModel: SmsOtpVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SplashVM::class)
    abstract fun bindSplashVM(viewModel: SplashVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForceUpdateVM::class)
    abstract fun bindForceUpdateVM(viewModel: ForceUpdateVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SuccesRegisterVM::class)
    abstract fun bindSuccesRegisterVM(viewModel: SuccesRegisterVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactServicesVM::class)
    abstract fun bindContactServicesVM(viewModel: ContactServicesVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReconsultationVM::class)
    abstract fun bindReconsultationVM(viewModel : ReconsultationVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnBoardingVM::class)
    abstract fun bindOnboardingVM(viewmodel : OnBoardingVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PromotionVM::class)
    abstract fun bindPromotionVM(viewModel : PromotionVM) : ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(PromotionTeleconsultationVM::class)
    abstract fun bindPromotionTeleconsultationVM(viewModel : PromotionTeleconsultationVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PromotionDetailVM::class)
    abstract fun bindPromotionDetailVM(viewModel : PromotionDetailVM) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MaBusyVM::class)
    abstract fun bindMaBusyVM(viewModel : MaBusyVM) : ViewModel
}