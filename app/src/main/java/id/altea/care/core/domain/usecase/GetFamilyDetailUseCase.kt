package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.model.DetailPatient
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetFamilyDetailUseCase @Inject constructor(val userRepository: UserRepository) {

    fun getFamilyDetail(patientId : String) : Single<DetailPatient>{
        return userRepository.getFamilyDetailMember(patientId)
    }
}