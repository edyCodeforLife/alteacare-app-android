package id.altea.care.core.domain.usecase

import id.altea.care.core.domain.cache.AccountCache
import id.altea.care.core.domain.cache.AuthCache
import id.altea.care.core.domain.model.Account
import javax.inject.Inject

class SwitchAccountUseCase @Inject constructor(
    private val authCache: AuthCache,
    private val accountCache: AccountCache
) {

    fun switchAccount(userId: String) {
        val currentAccount = accountCache.getUserAccount(authCache.getUserId())

        if (currentAccount != null) {
            accountCache.updateToken(
                authCache.getUserId(),
                authCache.getToken(),
                authCache.getRefreshToken(),
                isLoggedIn = true,
                isCurrentActive = false
            )
        }

        val newAccount = accountCache.getUserAccount(userId)
        newAccount?.let {
            authCache.setUserId(it.userId)
            authCache.setToken(it.token)
            authCache.setRefreshToken(it.refreshToken)
            authCache.setIsLoggedIn(it.isLoggedIn)
        }
    }

    fun getAccountIsLoggedin(): List<Account> {
        // show only another account
        return accountCache.getAllAccountIsLoggedIn().filter { authCache.getUserId() != it.userId }
    }

}
