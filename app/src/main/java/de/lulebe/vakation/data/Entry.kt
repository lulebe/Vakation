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
        @ColumnInfo(name = "trip_id") var tripId: Long,
        var type: EntryType,
        var date: Date,
        var title: String,
        var data: String
) {
    @PrimaryKey(autoGenerate = true) var uid: Long = 0

    data class AudioData(val audioFileName: String, val duration: Long, val ampsFileName: String) {
        companion object {
            fun from (entryData: String): AudioData {
                val parts = entryData.split("|")
                return AudioData(parts[0], parts[1].toLong(), parts[2])
            }
        }
    }

    data class ImageData(val imageFileNames: List<String>, val size: Int) {
        companion object {
            fun from (entryData: String): ImageData {
                val d = entryData.split("|")
                return ImageData(d, d.size)
            }
        }
    }
}