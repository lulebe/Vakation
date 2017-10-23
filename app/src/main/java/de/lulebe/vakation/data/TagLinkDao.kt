package de.lulebe.vakation.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface TagLinkDao {

    @Query("SELECT tags.name AS name, tags.uid AS uid FROM taglinks LEFT JOIN tags ON tags.uid = taglinks.tag_id WHERE trip_id = :id AND entry_id is null")
    fun getTagsForTripOnly(id: Long): List<Tag>

    @Query("SELECT tags.name AS name, tags.uid AS uid FROM taglinks LEFT JOIN tags ON tags.uid = taglinks.tag_id WHERE trip_id = :id AND entry_id is null")
    fun getTagsForTripOnlyLive(id: Long): LiveData<List<Tag>>

    @Query("SELECT tags.name AS name, tags.uid AS uid FROM taglinks LEFT JOIN tags ON tags.uid = taglinks.tag_id WHERE trip_id = :id")
    fun getTagsForTripAndEntries(id: Long): List<Tag>

    @Query("SELECT tags.name AS name, tags.uid AS uid FROM taglinks LEFT JOIN tags ON tags.uid = taglinks.tag_id WHERE entry_id = :id")
    fun getTagsForEntry(id: Long): List<Tag>

    @Query("SELECT EXISTS(SELECT 1 FROM taglinks WHERE tag_id = :tagId AND trip_id = :tripId AND entry_id is null LIMIT 0,1)")
    fun hasTagForTrip(tagId: Long, tripId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM taglinks WHERE tag_id = :tagId AND entry_id = :entryId LIMIT 0,1)")
    fun hasTagForEntry(tagId: Long, entryId: Long): Boolean

    @Insert
    fun insertTagLink(tagLink: TagLink): Long

    @Delete
    fun deleteTagLink(tagLink: TagLink)

    @Query("DELETE FROM taglinks WHERE uid IN (SELECT taglinks.uid FROM taglinks LEFT JOIN tags ON taglinks.tag_id=tags.uid WHERE tags.name=:tag AND taglinks.trip_id=:tripId AND taglinks.entry_id is null)")
    fun deleteTagLinkByNameFromTrip(tripId: Long, tag: String)

    @Query("DELETE FROM taglinks WHERE uid IN (SELECT taglinks.uid FROM taglinks LEFT JOIN tags ON taglinks.tag_id=tags.uid WHERE tags.name=:tag AND taglinks.entry_id=:entryId)")
    fun deleteTagLinkByNameFromEntry(entryId: Long, tag: String)


    @Query("SELECT trips.uid FROM taglinks LEFT JOIN trips ON taglinks.trip_id=trips.uid WHERE taglinks.tag_id=:tagId AND taglinks.entry_id is null")
    fun getTripIdsForTag(tagId: Long): List<Long>

    @Query("SELECT entries.uid FROM taglinks LEFT JOIN entries ON taglinks.entry_id=entries.uid WHERE taglinks.tag_id=:tagId")
    fun getEntryIdsForTag(tagId: Long): List<Long>

}