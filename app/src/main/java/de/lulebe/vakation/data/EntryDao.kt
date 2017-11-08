package de.lulebe.vakation.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface EntryDao {

    @Query("SELECT * FROM entries WHERE uid in (:ids)")
    fun getMultipleWithLocation(ids: List<Long>): List<EntryWithLocationAndTags>

    @Query("SELECT * FROM entries WHERE title LIKE :q OR data LIKE :q")
    fun searchWithLocations(q: String): List<EntryWithLocationAndTags>

    @Query("SELECT * FROM entries WHERE trip_id = :tripId")
    fun getEntriesForTripLive(tripId: Long): LiveData<List<EntryWithLocationAndTags>>

    @Query("SELECT * FROM entries WHERE trip_id = :tripId AND type = :type")
    fun getEntriesWithTypeForTrip(type: EntryType, tripId: Long): List<Entry>

    @Query("SELECT * FROM entries WHERE uid = :entryId")
    fun getEntryWithLocation(entryId: Long): EntryWithLocationAndTags

    @Query("SELECT * FROM entries WHERE uid = :entryId")
    fun getOne(entryId: Long): Entry

    @Insert
    fun insertEntry(entry: Entry): Long

    @Update
    fun updateEntry(entry: Entry)

    @Delete
    fun deleteEntry(entry: Entry)

    @Query("DELETE FROM entries WHERE uid = :entryId")
    fun deleteEntryById(entryId: Long)

}