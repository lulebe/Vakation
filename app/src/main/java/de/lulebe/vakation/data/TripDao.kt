package de.lulebe.vakation.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface TripDao {

    @Query("SELECT * FROM trips")
    fun getAll(): List<Trip>

    @Query("SELECT * FROM trips")
    fun getAllWithLocations(): List<TripWithLocationsAndTags>

    @Query("SELECT * FROM trips WHERE uid IN(:ids)")
    fun getMultipleWithLocations(ids: List<Long>): List<TripWithLocationsAndTags>

    @Query("SELECT * FROM trips WHERE uid = :uid")
    fun getOne(uid: Long): Trip

    @Query("SELECT * FROM trips WHERE uid = :uid")
    fun getOneLive(uid: Long): LiveData<Trip>

    @Query("SELECT * FROM trips WHERE name LIKE :q")
    fun searchWithLocations(q: String): List<TripWithLocationsAndTags>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertTrip(trip: Trip): Long

    @Update
    fun updateTrip(trip: Trip)

    @Delete
    fun deleteTrip(trip: Trip)

}