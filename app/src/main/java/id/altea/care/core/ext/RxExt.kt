package id.altea.care.core.ext

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun CompositeDisposable.delay(long: Long, action: () -> Unit) {
    val disposable = Observable.just(long)
        .subscribeOn(Schedulers.io())
        .delay(long, TimeUnit.MILLISECONDS)
        .map {
            return@map action
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            it.invoke()
        }
    add(disposable)
}