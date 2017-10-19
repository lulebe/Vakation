package de.lulebe.vakation.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import java.util.*


class TripWithLocationsAndTags {
    var uid: Long = 0
    var name: String = ""
    @ColumnInfo(name = "start_date") var startDate: Date = Date()
    @ColumnInfo(name = "end_date") var endDate: Date = Date()
    @ColumnInfo(name = "color_scheme") var colorScheme: ColorScheme = ColorScheme.FOREST

    @Relation(entity = DbLocation::class, parentColumn = "uid", entityColumn = "trip_id")
    var locations: List<DbLocation> = emptyList()

    @Ignore var tags: List<Tag> = emptyList()
}