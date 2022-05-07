package id.altea.care.core.data.local.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(arr: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

    @Delete
    fun delete(obj: T)
}
