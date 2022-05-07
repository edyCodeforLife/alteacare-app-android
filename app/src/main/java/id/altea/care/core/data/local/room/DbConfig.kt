package id.altea.care.core.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import id.altea.care.core.data.local.room.dao.AccountDao
import id.altea.care.core.data.local.room.entity.AccountEntity

@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
abstract class DbConfig : RoomDatabase() {

    abstract fun accountDao(): AccountDao

}
