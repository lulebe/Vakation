package de.lulebe.vakation.data

object SearchUtils {
    fun searchForTagByName(db: AppDB, tagName: String): MutableList<SearchResult> {
        val tag = db.tagDao().getExactTag(tagName) ?: return mutableListOf<SearchResult>()
        val tripIds = db.tagLinkDao().getTripIdsForTag(tag.uid)
        val results = db.tripDao().getMultipleWithLocations(tripIds).map {
            it.tags = db.tagLinkDao().getTagsForTripOnly(it.uid)
            SearchResult(SearchResult.Type.TRIP, it, null)
        }.toMutableList()
        val entryIds = db.tagLinkDao().getEntryIdsForTag(tag.uid)
        results.addAll(db.entryDao().getMultipleWithLocation(entryIds).map {
            it.tags = db.tagLinkDao().getTagsForEntry(it.uid)
            SearchResult(SearchResult.Type.ENTRY, null, it)
        })
        return results
    }

    fun searchForTrips(db: AppDB, query: String): MutableList<SearchResult> =
            db.tripDao().searchWithLocations(query + "%").map {
                it.tags = db.tagLinkDao().getTagsForTripOnly(it.uid)
                SearchResult(SearchResult.Type.TRIP, it, null)
            }.toMutableList()

    fun searchForEntries(db: AppDB, query: String): MutableList<SearchResult> =
            db.entryDao().searchWithLocations(query + "%").map {
                it.tags = db.tagLinkDao().getTagsForEntry(it.uid)
                SearchResult(SearchResult.Type.ENTRY, null, it)
            }.toMutableList()
}