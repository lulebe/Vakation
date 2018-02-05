package de.lulebe.vakation.data


object TagLinkUtils {

    fun addTagToTrip(db: AppDB, tripId: Long, tag: String) {
        val tagId = getOrAddTag(db, tag)
        val tagLinkDao = db.tagLinkDao()
        if (tagLinkDao.hasTagForTrip(tagId, tripId)) return
        tagLinkDao.insertTagLink(TagLink(tagId, tripId, null))
    }

    fun addTagToEntry(db: AppDB, tripId: Long, entryId: Long, tag: String) {
        val tagId = getOrAddTag(db, tag)
        val tagLinkDao = db.tagLinkDao()
        if (tagLinkDao.hasTagForEntry(tagId, entryId)) return
        tagLinkDao.insertTagLink(TagLink(tagId, tripId, entryId))
    }


    private fun getOrAddTag(db: AppDB, tag: String): Long {
        val tagDao = db.tagDao()
        val dbTag = tagDao.getExactTag(tag)
        if (dbTag != null) return dbTag.uid
        return tagDao.insertTag(Tag(tag))
    }
}