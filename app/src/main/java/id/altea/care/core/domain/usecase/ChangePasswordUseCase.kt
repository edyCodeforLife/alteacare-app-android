package id.altea.care.core.domain.usecase

import id.altea.care.core.data.request.ChangePasswordRequest
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: UserRepository,
    private val authCache: AuthCache
) {

    fun changePassword(password: String, passwordConfirm: String): Single<Boolean> {
        return repository.changePassword(
            ChangePasswordRequest(password, passwordConfirm)
        )
    }

    fun changeForgotPassword(
        token: String,
        password: String,
        passwordConfirm: String
    ): Single<Boolean> {
        var tempToken = ""
        return repository
            .changeForgotPassword(
                "Bearer $token",
                ChangePasswordRequest(password, passwordConfirm)
            )
            .doOnSubscribe {
                tempToken = authCache.getToken()
                authCache.setToken("")
            }
            .doAfterTerminate { authCache.setToken(tempToken) }
    }
}
