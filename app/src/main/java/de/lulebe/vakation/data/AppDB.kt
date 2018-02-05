package de.lulebe.vakation.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [Trip::class, Entry::class, DbLocation::class, Tag::class, TagLink::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDB: RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun entryDao(): EntryDao
    abstract fun locationDao(): DbLocationDao
    abstract fun tagDao(): TagDao
    abstract fun tagLinkDao(): TagLinkDao
    companion object {
        private var instance: AppDB? = null
        fun getInstance(context: Context): AppDB {
            if (instance == null)
                instance = Room.databaseBuilder(context.applicationContext, AppDB::class.java, "appdb").build()
            return instance!!
        }
    }
}