package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class DeleteFamilyUseCase @Inject constructor(val userRepository: UserRepository) {

    fun deleteFamilyMember(patientId : String) : Single<Boolean>{
        return userRepository.deleteFamilyMember(patientId)
    }
}