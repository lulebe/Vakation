package de.lulebe.vakation.data

import android.arch.persistence.room.*

@Dao
interface TripDao {

    @Query("SELECT * FROM trips")
    fun getAll(): List<Trip>

    @Query("SELECT * FROM trips")
    fun getAllWithLocations(): List<TripWithLocationsAndTags>

    @Query("SELECT * FROM trips WHERE uid = :uid")
    fun getOne(uid: Int): Trip

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertTrip(trip: Trip): Long

    @Delete
    fun deleteTrip(trip: Trip)

}