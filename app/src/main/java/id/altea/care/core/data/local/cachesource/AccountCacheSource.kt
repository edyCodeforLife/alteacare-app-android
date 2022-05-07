package id.altea.care.core.data.local.cachesource

import id.altea.care.core.data.local.room.dao.AccountDao
import id.altea.care.core.data.local.room.entity.AccountEntity
import id.altea.care.core.domain.cache.AccountCache
import id.altea.care.core.domain.model.Account

class AccountCacheSource(private val accountDao: AccountDao) : AccountCache {

    override fun addAccount(account: Account) {
        return accountDao.insert(Account.mapToEntity(account))
    }

    override fun updateToken(
        userId: String,
        token: String,
        refreshToken: String,
        isLoggedIn: Boolean,
        isCurrentActive: Boolean
    ) {
        accountDao.getAccountById(userId)?.let {
            accountDao.update(it.apply {
                it.token = token
                it.refreshToken = refreshToken
                it.isLoggedIn = isLoggedIn
                it.isCurrentActive = isCurrentActive
            })
        }
    }

    override fun getAllAccountIsLoggedIn(): List<Account> {
        return accountDao.getAllAccountLogin().map { AccountEntity.toModel(it) }
    }

    override fun setAccountNotActive() {
        accountDao.getAllAccountActive().forEach {
            updateToken(it.userId, it.token, it.refreshToken, it.isLoggedIn, false)
        }
    }

    override fun getUserAccount(userId: String): Account? {
        val accountEntity = accountDao.getAccountById(userId)
        return if (accountEntity != null) AccountEntity.toModel(accountEntity) else null
    }

    override fun invalidateAccount(userId: String) {
        updateToken(userId, "", "", false, isCurrentActive = false)
    }

    override fun invalidate() {
        accountDao.truncateTableAccount()
    }
}
