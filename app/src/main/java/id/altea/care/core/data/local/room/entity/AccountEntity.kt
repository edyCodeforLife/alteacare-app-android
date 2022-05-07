package id.altea.care.core.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.altea.care.core.domain.model.Account

@Entity(tableName = "table_account")
data class AccountEntity(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    var email: String,
    var name: String,
    var isLoggedIn: Boolean,
    var photo: String,
    var token: String,
    var refreshToken: String,
    var isCurrentActive: Boolean
) {
    companion object {
        fun toModel(entity: AccountEntity): Account {
            return Account(
                entity.userId,
                entity.email,
                entity.isLoggedIn,
                entity.name,
                entity.photo,
                entity.token,
                entity.refreshToken,
                entity.isCurrentActive
            )
        }
    }
}
