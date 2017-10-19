package de.lulebe.vakation.data

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import java.util.*

@Entity(
        tableName = "entries",
        foreignKeys = [
            ForeignKey(entity = Trip::class, childColumns = ["trip_id"], parentColumns = ["uid"], onDelete = CASCADE)
        ],
        indices = [
            Index("trip_id")
        ]
)
data class Entry(
        @PrimaryKey(autoGenerate = true) var uid: Long,
        @ColumnInfo(name = "trip_id") var tripId: Long,
        var type: EntryType,
        var date: Date,
        var data: String
)