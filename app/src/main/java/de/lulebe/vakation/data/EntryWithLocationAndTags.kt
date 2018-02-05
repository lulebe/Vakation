package de.lulebe.vakation.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import java.util.*


class EntryWithLocationAndTags {
    var uid: Long = 0
    var title: String = ""
    var data: String = ""
    var date: Date = Date()
    @ColumnInfo(name = "trip_id") var tripId: Long = 0
    var type: EntryType = EntryType.TEXT


    @Relation(entity = DbLocation::class, parentColumn = "uid", entityColumn = "entry_id")
    var locations: List<DbLocation> = emptyList()

    @Ignore var tags: List<Tag> = emptyList()

    fun getLocation(): DbLocation? =
            if (locations.isEmpty())
                null
            else
                locations[0]
}