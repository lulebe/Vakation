package de.lulebe.vakation.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import de.lulebe.vakation.R
import de.lulebe.vakation.data.*
import de.lulebe.vakation.ui.ViewExt.fadeIn
import de.lulebe.vakation.ui.ViewExt.fadeOut
import de.lulebe.vakation.ui.adapters.SearchResultsAdapter
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SearchActivity : AppCompatActivity() {

    private val searchAdapter = SearchResultsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        search(intent)
        searchAdapter.resultClickListener = {
            if (it.type == SearchResult.Type.TRIP)
                openTrip(it.trip!!)
            else
                openEntry(it.entry!!)
        }
        searchAdapter.tagClickListener = { q: String ->
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("query", q)
            intent.putExtra("tagOnly", true)
            startActivity(intent)
        }
        rv_searchresults.layoutManager = LinearLayoutManager(this)
        rv_searchresults.adapter = searchAdapter
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) return
        title = resources.getString(R.string.title_activity_search)
        search(intent)
    }

    private fun search(i: Intent) {
        doAsync {
            val query = i.getStringExtra("query")
            val tagOnly = i.getBooleanExtra("tagOnly", false)
            val db = AppDB.getInstance(this@SearchActivity)
            val results = if(!tagOnly) SearchUtils.searchForTrips(db, query) else mutableListOf()
            results.addAll(SearchUtils.searchForTagByName(db, query))
            if (!tagOnly)
                results.addAll(SearchUtils.searchForEntries(db, query))
            uiThread {
                searchAdapter.setResults(results)
                loading.fadeOut()
                rv_searchresults.fadeIn()
                title = if (i.getBooleanExtra("tagOnly", false))
                            "#" + i.getStringExtra("query")
                        else
                            i.getStringExtra("query")
            }
        }
    }

    private fun openEntry(entry: EntryWithLocationAndTags) {
        doAsync {
            val trip = AppDB.getInstance(this@SearchActivity).tripDao().getOne(entry.tripId)
            uiThread {
                val intent = Intent(this@SearchActivity, TripActivity::class.java)
                intent.putExtra("tripId", trip.uid)
                intent.putExtra("colorScheme", trip.colorScheme.name)
                intent.putExtra("name", trip.name)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun openTrip(trip: TripWithLocationsAndTags) {
        val intent = Intent(this, TripActivity::class.java)
        intent.putExtra("tripId", trip.uid)
        intent.putExtra("colorScheme", trip.colorScheme.name)
        intent.putExtra("name", trip.name)
        startActivity(intent)
        finish()
    }

}
