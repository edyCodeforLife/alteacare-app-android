package id.altea.care.view.consultation.voucher

import com.google.gson.annotations.SerializedName
import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.data.request.VoucherRequest
import id.altea.care.core.domain.model.Voucher
import id.altea.care.core.domain.usecase.VoucherUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Scheduler
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class VoucherVm @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val voucherUseCase: VoucherUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler)
{
    var voucherEvent = SingleLiveEvent<Voucher>()
    fun getVoucher(code: String?,transactionId: String?,typeOfService: String?){
        executeJob {
            voucherUseCase
                .getVoucher(code,transactionId,typeOfService)
                .compose(applySchedulers())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false}
                .subscribe({
                    voucherEvent.value = it
                },{
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }




}