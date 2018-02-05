package de.lulebe.vakation.ui

import android.os.Bundle
import de.lulebe.vakation.R
import de.lulebe.vakation.data.AppDB
import de.lulebe.vakation.data.Entry
import de.lulebe.vakation.data.EntryType
import de.lulebe.vakation.data.TagLinkUtils

import kotlinx.android.synthetic.main.activity_add_text.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AddTextActivity : AddEntryActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(R.layout.activity_add_text)
    }

    override fun loadEntry() {
        val entry = getEntry()
    }

    override fun saveEntry(done: () -> Unit) {
        val text = et_text.text.toString()
        val title = getInputTitle()
        val tripId = intent.getLongExtra("tripId", 0)
        val tags = getTags()
        val time = getDate()
        doAsync {
            val db = AppDB.getInstance(this@AddTextActivity)
            val entry = Entry(tripId, EntryType.TEXT, time, title, text)
            val entryId = db.entryDao().insertEntry(entry)
            tags.forEach {
                TagLinkUtils.addTagToEntry(db, tripId, entryId, it)
            }
            getLocation()?.let {
                db.locationDao().insertLocation(it.toDbLocation(tripId, entryId))
            }
            uiThread { done() }
        }
    }

    override fun updateEntry(done: () -> Unit) {
        val text = et_text.text.toString()
        val title = getInputTitle()
        val tags = getTags()
        val entry = getEntry()
        entry?.let {
            it.data = text
            it.title = title
            doAsync {
                val db = AppDB.getInstance(this@AddTextActivity)

                uiThread { done() }
            }
        }
        if (entry == null) done()
    }

}
