package de.lulebe.vakation.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query


@Dao
interface DbLocationDao {

    @Query("SELECT * FROM locations WHERE trip_id=:tripId AND entry_id is null")
    fun getLocationsForTripOnly(tripId: Long): List<DbLocation>

    @Insert
    fun insertLocation(location: DbLocation): Long

    @Delete
    fun deleteLocation(location: DbLocation)

}