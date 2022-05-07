package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AuthCache
import javax.inject.Inject

class CreateAuthUseCase @Inject constructor(private val authCache: AuthCache) {

    data class Param(val token: String, val refreshToken: String)

    fun setIsLoggedIn(params: Param) {
        authCache.run {
            setIsLoggedIn(true)
            setToken(params.token)
            setRefreshToken(params.refreshToken)
        }
    }
}
