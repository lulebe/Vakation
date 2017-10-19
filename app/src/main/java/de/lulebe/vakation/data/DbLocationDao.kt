package de.lulebe.vakation.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert


@Dao
interface DbLocationDao {

    @Insert
    fun insertLocation(location: DbLocation): Long

    @Delete
    fun deleteLocation(location: DbLocation)

}