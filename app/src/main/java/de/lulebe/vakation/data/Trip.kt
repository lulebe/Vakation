package de.lulebe.vakation.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(
        tableName = "trips"
)
data class Trip (
        var name: String,
        @ColumnInfo(name = "start_date") var startDate: Date,
        @ColumnInfo(name = "end_date") var endDate: Date,
        @ColumnInfo(name = "color_scheme") var colorScheme: ColorScheme
) {
    @PrimaryKey(autoGenerate = true) var uid: Long = 0
}