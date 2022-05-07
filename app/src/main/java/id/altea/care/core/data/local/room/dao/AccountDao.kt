package id.altea.care.core.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import id.altea.care.core.data.local.room.entity.AccountEntity

@Dao
interface AccountDao : BaseDao<AccountEntity> {

    @Query("select * from table_account where isLoggedIn='1'")
    fun getAllAccountLogin(): List<AccountEntity>

    @Query("select * from table_account where isCurrentActive='1'")
    fun getAllAccountActive(): List<AccountEntity>

    @Query("select * from table_account where userId = :userId")
    fun getAccountById(userId: String): AccountEntity?

    @Query("delete from table_account")
    fun truncateTableAccount()
}
