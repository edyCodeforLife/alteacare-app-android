package id.altea.care.view.payment.paymentmethod

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.Payment
import id.altea.care.core.domain.usecase.CreatePaymentUseCase
import id.altea.care.core.domain.usecase.GetAuthUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class PaymentMethodVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val authUseCase: GetAuthUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val createPaymentEvent = SingleLiveEvent<Payment>()
    val redirectUrlEvent = SingleLiveEvent<String>()

    fun doCreatePayment(appointmentId: Int, paymentMethod: String,voucherCode : String?) {
        executeJob {
            createPaymentUseCase.doPayment(appointmentId, paymentMethod,voucherCode)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribe(
                    {
                        when (it.type) {
                            "ALTEA_PAYMENT_WEBVIEW" -> {
                                redirectUrlEvent.value = it.paymentUrl?.replace(
                                    "{{REPLACE_THIS_TO_BEARER_USER}}",
                                    authUseCase.getToken()
                                )
                            }
                            "MIDTRANS_SNAP" -> createPaymentEvent.value = it
                        }
                    },
                    { handleFailure(it.getGeneralErrorServer()) })
                .disposedBy(disposable)
        }
    }
}
