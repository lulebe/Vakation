package de.lulebe.vakation.data


object TripUtils {
    fun getTripsWithTagsAndLocations(db: AppDB): List<TripWithLocationsAndTags> {
        val trips = db.tripDao().getAllWithLocations()
        trips.forEach {
            it.tags = db.tagLinkDao().getTagsForTripOnly(it.uid)
        }
        return trips
    }
}