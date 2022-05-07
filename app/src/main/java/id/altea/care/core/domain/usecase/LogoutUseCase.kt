package id.altea.care.core.domain.usecase


import id.altea.care.core.domain.cache.AccountCache
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.cache.UserCache
import id.altea.care.core.domain.model.General
import id.altea.care.core.domain.repository.UserRepository
import io.reactivex.Single
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: UserRepository,
    private val authCache: AuthCache,
    private val userCache: UserCache,
    private val accountCache: AccountCache
) {

    fun doLogout(): Single<General> {
        return repository.logout().flatMap {
            accountCache.invalidateAccount(authCache.getUserId())
            authCache.invalidate()
            userCache.invalidate()
            changeLoginAccountIfExist()
            Single.just(it)
        }
    }

    private fun changeLoginAccountIfExist() {
        // check on local db if any account is login, switch or if empty force logout
        accountCache.getAllAccountIsLoggedIn().firstOrNull()?.let {
            authCache.setUserId(it.userId)
            authCache.setToken(it.token)
            authCache.setIsLoggedIn(it.isLoggedIn)
            authCache.setRefreshToken(it.refreshToken)
        }
    }
}
