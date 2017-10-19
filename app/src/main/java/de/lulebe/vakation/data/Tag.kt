package de.lulebe.vakation.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
        tableName = "tags",
        indices = [
            Index("name", unique = true)
        ]
)
data class Tag(
    var name: String
) {
    @PrimaryKey(autoGenerate = true) var uid: Long = 0
}