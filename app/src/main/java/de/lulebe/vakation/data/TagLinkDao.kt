package de.lulebe.vakation.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface TagLinkDao {

    @Query("SELECT tags.name AS name, tags.uid AS uid FROM taglinks LEFT JOIN tags ON tags.uid = taglinks.tag_id WHERE trip_id = :id AND entry_id is null")
    fun getTagsForTripOnly(id: Long): List<Tag>

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

}