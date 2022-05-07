package id.altea.care.core.helper

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxBus {

    private val subject = PublishSubject.create<Any>()

    fun post(event: Any) {
        subject.onNext(event)
    }

    fun getEvents(): Observable<Any> {
        return subject
    }
}
