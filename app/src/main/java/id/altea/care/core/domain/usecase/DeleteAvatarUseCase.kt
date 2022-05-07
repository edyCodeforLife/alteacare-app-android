package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.UpdateAvatarRequest
import id.altea.care.core.domain.repository.UserRepository
import id.altea.care.core.helper.SingleLiveEvent
import io.reactivex.Single
import javax.inject.Inject

class DeleteAvatarUseCase @Inject constructor(val userRepository: UserRepository) {

    fun deleteAvatar() : Single<Boolean>{
        return userRepository.deteleAvatar()
    }
}