package id.altea.care.view.address.addresslist

import id.altea.care.core.base.BaseViewModel
import id.altea.care.core.domain.model.UserAddress
import id.altea.care.core.domain.usecase.DeleteAddressUseCase
import id.altea.care.core.domain.usecase.GetProfileAddressUserCase
import id.altea.care.core.domain.usecase.SetPrimaryAddressUseCase
import id.altea.care.core.ext.TagInjection
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.getGeneralErrorServer
import id.altea.care.core.helper.NetworkHandler
import id.altea.care.core.helper.SingleLiveEvent
import id.altea.care.view.address.failure.AddressFailure
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class AddressListVM @Inject constructor(
    @Named(TagInjection.UI_Scheduler) uiSchedulers: Scheduler,
    @Named(TagInjection.IO_Scheduler) ioScheduler: Scheduler,
    networkHandler: NetworkHandler,
    private val getProfileAddressUserCase: GetProfileAddressUserCase,
    private val setPrimaryAddressUseCase: SetPrimaryAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) : BaseViewModel(uiSchedulers, ioScheduler, networkHandler) {

    val addressListEvent = SingleLiveEvent<List<UserAddress>>()
    val isNextPageAvailableEvent = SingleLiveEvent<Boolean>()
    val isLoadingFirstEvent = SingleLiveEvent<Boolean>()
    val isLoadingMoreEvent = SingleLiveEvent<Boolean>()
    val setPrimaryAddressEvent = SingleLiveEvent<Int>()
    val deleteAddressEvent = SingleLiveEvent<Int>()

    fun getListAddress(page: Int) {
        executeJob {
            val isFirstPage = page == 1
            getProfileAddressUserCase.getProfileAddress(page)
                .compose(applySchedulers())
                .doOnSubscribe {
                    if (isFirstPage) {
                        isLoadingFirstEvent.value = true
                    } else {
                        isLoadingMoreEvent.value = true
                    }
                }
                .doOnTerminate {
                    if (isFirstPage) {
                        isLoadingFirstEvent.value = false
                    } else {
                        isLoadingMoreEvent.value = false
                    }
                }
                .subscribe({
                    addressListEvent.value = it.address
                    val totalPage = it.meta.totalPage ?: -1
                    isNextPageAvailableEvent.value = totalPage > page
                }, {
                    isNextPageAvailableEvent.value = false
                    handleFailure(it.getGeneralErrorServer())
                }).disposedBy(disposable)
        }
    }

    fun setPrimaryAddress(idAddress: String, adapterPosition: Int) {
        setPrimaryAddressUseCase.setPrimaryAddress(idAddress)
            .subscribeOn(ioScheduler)
            .observeOn(uiSchedulers)
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe({
                setPrimaryAddressEvent.value = adapterPosition
            }, {
                handleFailure(AddressFailure.SetPrimaryAddressFailure)
            })
            .disposedBy(disposable)
    }

    fun deleteAddress(idAddress: String, adapterPosition: Int) {
        deleteAddressUseCase.deleteAddress(idAddress)
            .subscribeOn(ioScheduler)
            .observeOn(uiSchedulers)
            .doOnSubscribe { isLoadingLiveData.value = true }
            .doOnTerminate { isLoadingLiveData.value = false }
            .subscribe(
                {
                    deleteAddressEvent.value = adapterPosition
                }, {
                    handleFailure(AddressFailure.DeleteAddressFailure)
                }
            )
            .disposedBy(disposable)
    }
}
