package id.altea.care.core.ext

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableSingleObserver

fun Disposable.disposedBy(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

object TagInjection {
    const val UI_Scheduler = "TagInjection.UI_Scheduler"
    const val IO_Scheduler = "TagInjection.IO_Scheduler"
}
