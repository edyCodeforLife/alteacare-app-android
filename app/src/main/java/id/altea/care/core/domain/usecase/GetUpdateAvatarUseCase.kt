package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.UpdateAvatarRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.General
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class GetUpdateAvatarUseCase @Inject constructor(private val repository: UserRepository) {
    fun updateAvatar(avatar : UpdateAvatarRequest): Single<General> {
        return repository.updateAvatar(avatar)
    }
}