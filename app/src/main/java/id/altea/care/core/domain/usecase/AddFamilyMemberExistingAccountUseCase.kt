package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.AddFamilyExistingAccountRequest
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddFamilyMemberExistingAccountUseCase @Inject constructor(val userRepository: UserRepository) {

    fun addFamilyMemberExistingAccount(patientId : String,
                                       email : String,
                                       phone : String) : Completable {

        return userRepository.addMemberFamilyExisitingAccount(patientId,
            AddFamilyExistingAccountRequest(
            email,phone
        ))
    }
}