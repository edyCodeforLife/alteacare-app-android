package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AuthCache
import javax.inject.Inject

class GetAuthUseCase @Inject constructor(private val authCache: AuthCache) {

    fun getIsLoggedIn(): Boolean = authCache.getIsLoggedIn()

    fun getToken(): String = authCache.getToken()

}
