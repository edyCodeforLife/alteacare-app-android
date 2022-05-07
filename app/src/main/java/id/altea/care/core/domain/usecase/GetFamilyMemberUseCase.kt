package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.PagePatient
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class GetFamilyMemberUseCase @Inject constructor(val userRepository: UserRepository) {

    fun getFamilyMember(page: Int): Single<PagePatient> {
        return userRepository.getFamilyMember(page)
    }

    // this observable will be call page until data is empty or page more than 2
    fun getAllFamilyMember(page: Int): Observable<List<PatientFamily>> {
        return getFamilyMember(page).map { it.patients }.toObservable().concatMap {
            if (it.isEmpty() || page > 2) {
                Observable.just(it)
            } else {
                Observable.just(it).concatWith(getAllFamilyMember(page + 1))
            }
        }
    }
}
