package de.lulebe.vakation.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.FAIL
import android.arch.persistence.room.Query

@Dao
interface TagDao {

    @Query("SELECT * FROM tags WHERE name LIKE :q")
    fun getTagsLike(q: String): List<Tag>

    @Query("SELECT * FROM tags WHERE name = :tag LIMIT 0,1")
    fun getExactTag(tag: String): Tag?

    @Query("SELECT * FROM tags WHERE uid = :id")
    fun getTag(id: Long): Tag

    @Insert(onConflict = FAIL)
    fun insertTag(tag: Tag): Long

}