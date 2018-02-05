package de.lulebe.vakation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.View
import com.miguelcatalan.materialsearchview.MaterialSearchView
import de.lulebe.vakation.R
import de.lulebe.vakation.data.AppDB
import de.lulebe.vakation.data.Permissions
import de.lulebe.vakation.data.TripUtils
import de.lulebe.vakation.ui.ViewExt.fadeIn
import de.lulebe.vakation.ui.ViewExt.fadeOut
import de.lulebe.vakation.ui.adapters.HomeTripsAdapter
import de.lulebe.vakation.ui.intro.IntroActivity
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class HomeActivity : AppCompatActivity(), MaterialSearchView.OnQueryTextListener {

    private val adapter = HomeTripsAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))

        //searchView
        search_view.setOnQueryTextListener(this)
        search_view.setVoiceSearch(true)

        //list
        rv_trips.layoutManager = LinearLayoutManager(this)
        rv_trips.adapter = adapter

        adapter.tripClickListener = {
            val intent = Intent(this, TripActivity::class.java)
            intent.putExtra("tripId", it.uid)
            intent.putExtra("colorScheme", it.colorScheme.name)
            intent.putExtra("name", it.name)
            startActivity(intent)
        }
        adapter.tagClickListener = { q: String ->
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("query", q)
            intent.putExtra("tagOnly", true)
            startActivity(intent)
        }

        fab.setOnClickListener(startCreateTripActivity)
    }

    override fun onResume() {
        super.onResume()
        val sp = getSharedPreferences("internal", Context.MODE_PRIVATE)
        if (Permissions.needsPermissions(this) || !sp.getBoolean("introDone", false))
            startActivity(Intent(this, IntroActivity::class.java))
        displayTrips()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    search_view.setQuery(searchWrd, false)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        search_view.setMenuItem(menu.findItem(R.id.action_search))
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            val intent = Intent(this@HomeActivity, SearchActivity::class.java)
            intent.putExtra("query", it)
            intent.putExtra("tagOnly", false)
            startActivity(intent)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean = false

    private fun displayTrips() {
        doAsync {
            val db = AppDB.getInstance(applicationContext)
            val trips = TripUtils.getTripsWithTagsAndLocations(db)
            uiThread {
                if (trips.isEmpty()) {
                    rv_trips.fadeOut()
                    no_trips.fadeIn()
                } else {
                    rv_trips.fadeIn()
                    no_trips.fadeOut()
                }
                adapter.setTrips(trips)
            }
        }
    }

    private val startCreateTripActivity =  { _: View ->
        startActivity(Intent(this, CreateTripActivity::class.java))
    }
}
