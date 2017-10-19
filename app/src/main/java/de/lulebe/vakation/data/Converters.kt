package de.lulebe.vakation.data

import android.arch.persistence.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.util.*

class Converters {

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter
    fun dateFromTimestamp(timestamp: Long?): Date? {
        return if (timestamp != null) Date(timestamp) else null
    }

    @TypeConverter
    fun latlngToString(latLng: LatLng?): String? {
        if (latLng == null) return null
        return latLng.latitude.toString() + "," + latLng.longitude.toString()
    }
    @TypeConverter
    fun latlngFromString(string: String?): LatLng? {
        if (string == null) return null
        val parts = string.split(",")
        return LatLng(parts[0].toDouble(), parts[1].toDouble())
    }

    @TypeConverter
    fun colorschemeToInt(cs: ColorScheme): Int = cs.ordinal
    @TypeConverter
    fun colorschemeFromInt(i: Int): ColorScheme = ColorScheme.values()[i]

    @TypeConverter
    fun entrytypeToInt(et: EntryType): Int = et.ordinal
    @TypeConverter
    fun entrytypeFromInt(i: Int): EntryType = EntryType.values()[i]

}