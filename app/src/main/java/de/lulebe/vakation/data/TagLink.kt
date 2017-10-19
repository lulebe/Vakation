package de.lulebe.vakation.data

import android.arch.persistence.room.*

@Entity(
        tableName = "taglinks",
        foreignKeys = [
            ForeignKey(entity = Trip::class, childColumns = arrayOf("trip_id"), parentColumns = arrayOf("uid"), onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Entry::class, childColumns = arrayOf("entry_id"), parentColumns = arrayOf("uid"), onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Tag::class, childColumns = arrayOf("tag_id"), parentColumns = arrayOf("uid"), onDelete = ForeignKey.CASCADE)
        ],
        indices = [
            Index("trip_id"),
            Index("entry_id"),
            Index("tag_id")
        ]
)
data class TagLink(
        @ColumnInfo(name = "tag_id") var tagId: Long,
        @ColumnInfo(name = "trip_id") var tripId: Long,
        @ColumnInfo(name = "entry_id") var entityId: Long?
) {
    @PrimaryKey(autoGenerate = true) var uid: Long = 0
}