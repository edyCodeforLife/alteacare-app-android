package id.altea.care.core.domain.model

import android.os.Parcelable
import id.altea.care.core.data.local.room.entity.AccountEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(
    val userId: String,
    val email: String,
    val isLoggedIn: Boolean,
    val name: String,
    val photo: String,
    val token: String,
    val refreshToken: String,
    val isCurrentActive: Boolean
) : Parcelable {

    companion object {
        fun mapToEntity(account: Account): AccountEntity {
            return AccountEntity(
                account.userId,
                account.email,
                account.name,
                account.isLoggedIn,
                account.photo,
                account.token,
                account.refreshToken,
                account.isCurrentActive
            )
        }
    }
}
