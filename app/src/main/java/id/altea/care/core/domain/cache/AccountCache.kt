package id.altea.care.core.domain.cache

import id.altea.care.core.domain.model.Account

interface AccountCache : BaseCache {

    fun addAccount(account: Account)

    fun getAllAccountIsLoggedIn(): List<Account>

    fun setAccountNotActive()

    fun updateToken(
        userId: String,
        token: String,
        refreshToken: String,
        isLoggedIn: Boolean = true,
        isCurrentActive: Boolean = true
    )

    fun getUserAccount(userId: String): Account?

    fun invalidateAccount(userId: String)

}
