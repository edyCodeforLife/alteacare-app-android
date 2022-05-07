package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class SetPrimaryFamilyUseCase @Inject constructor(val userRepository: UserRepository) {

    fun setPrimaryFamily(patientId : String) : Completable {
        return userRepository.setPrimaryFamily(patientId)
    }
}