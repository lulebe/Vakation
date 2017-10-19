package de.lulebe.vakation.data

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(
        tableName = "locations",
        foreignKeys = [
            ForeignKey(entity = Entry::class, childColumns = ["entity_id"], parentColumns = ["uid"], onDelete = CASCADE),
            ForeignKey(entity = Trip::class, childColumns = ["trip_id"], parentColumns = ["uid"], onDelete = CASCADE)
        ],
        indices = [
            Index("trip_id"),
            Index("entity_id")
        ]
)
data class DbLocation(
    @ColumnInfo(name = "entity_id") var entityId: Long?,
    @ColumnInfo(name = "trip_id") var tripId: Long,
    var latitude: Double,
    var longitude: Double,
    var name: String?,
    var address: String?
) {

    @PrimaryKey(autoGenerate = true) var uid: Long = 0
}

