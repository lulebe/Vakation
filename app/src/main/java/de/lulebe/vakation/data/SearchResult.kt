package de.lulebe.vakation.data

data class SearchResult(
        val type: Type,
        val trip: TripWithLocationsAndTags?,
        val entry: EntryWithLocationAndTags?
) {
    enum class Type {
        TRIP, ENTRY
    }
}