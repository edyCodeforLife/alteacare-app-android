package id.altea.care.core.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.altea.care.core.exception.Failure
import id.altea.care.core.helper.NetworkHandler
import io.reactivex.Scheduler
import io.reactivex.SingleTransformer
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by trileksono on 02/03/21.
 */
abstract class BaseViewModel(
    protected val uiSchedulers: Scheduler,
    protected val ioScheduler: Scheduler,
    protected val networkHandler: NetworkHandler
) : ViewModel() {

    protected val disposable = CompositeDisposable()

    val failureLiveData: MutableLiveData<Failure> = MutableLiveData()

    val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        isLoadingLiveData.postValue( false)
        failureLiveData.postValue(failure)
    }

    protected fun executeJob(invoke: () -> Unit) {
        when (networkHandler.isNetworkAvailable()) {
            true -> invoke()
            else -> handleFailure(Failure.NetworkConnection)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    fun <T> applySchedulers(): SingleTransformer<T, T> {
        return SingleTransformer { observable ->
            observable.subscribeOn(ioScheduler).observeOn(uiSchedulers)
        }
    }
}
