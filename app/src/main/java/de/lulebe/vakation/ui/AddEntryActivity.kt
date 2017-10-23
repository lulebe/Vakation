package de.lulebe.vakation.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.lulebe.vakation.R
import de.lulebe.vakation.data.AppDB
import de.lulebe.vakation.data.EntryWithLocationAndTags
import de.lulebe.vakation.data.Location
import kotlinx.android.synthetic.main.activity_addentry.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

abstract class AddEntryActivity : AppCompatActivity() {

    abstract fun saveEntry(done: () -> Unit)
    abstract fun updateEntry(done: () -> Unit)
    abstract fun loadEntry()

    private val date = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private var marker: Marker? = null
    private var location: Location? = null
    private var googleMap: GoogleMap? = null
    private var entry: EntryWithLocationAndTags? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(intent.getIntExtra("themeId", R.style.ForestTheme))
        setContentView(R.layout.activity_addentry)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp)

        et_title.requestFocus()
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            date.set(year, month, day)
            et_date.text = dateFormatter.format(date.time)
        }, 2017, 5, 20)
        et_date.text = dateFormatter.format(System.currentTimeMillis())
        btn_edit_date.setOnClickListener { _ ->
            datePicker.updateDate(
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        manageMap()

        if (intent.getBooleanExtra("editEntry", false)) {
            loadEntryFromDb(intent.getLongExtra("entryId", 0))
        }
    }

    fun setContent(layout: Int) {
        val v = findViewById<ViewGroup>(R.id.contentview)
        v.removeAllViews()
        LayoutInflater.from(this).inflate(layout, v, true)
    }

    open fun cancelEntry() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.addentry, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> {
            cancelEntry()
            true
        }
        R.id.mi_save -> {
            val dialog = AlertDialog.Builder(this)
                    .setTitle("saving...")
                    .setCancelable(false)
                    .show()
            if (intent.getBooleanExtra("editEntry", false))
                updateEntryBase {
                    dialog.dismiss()
                    cancelEntry()
                }
            else
                saveEntry {
                    dialog.dismiss()
                    cancelEntry()
                }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun loadEntryFromDb(entryId: Long) {
        doAsync {
            val db = AppDB.getInstance(this@AddEntryActivity)
            entry = db.entryDao().getEntryWithLocation(entryId)
            entry!!.tags = db.tagLinkDao().getTagsForEntry(entryId)
            uiThread {
                loadEntry()
                c_tags.setTags(entry!!.tags.map { it.name })
                entry!!.getLocation()?.let {
                    val latlng = LatLng(it.latitude, it.longitude)
                    marker = googleMap?.addMarker(MarkerOptions().position(latlng).title(it.name))
                    location = Location(it.name ?: "", it.address ?: "", it.latitude, it.longitude)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15F))
                }
            }
        }
    }

    private fun updateEntryBase(done: () -> Unit) {
        // TODO implement
    }

    private fun manageMap() {
        val mapFragment = fragmentManager.findFragmentById(R.id.gmap) as MapFragment
        val placeFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment

        mapFragment.getMapAsync { map ->
            googleMap = map
            map.setOnMapClickListener { clickLocation ->
                doAsync {
                    val geocoder = Geocoder(this@AddEntryActivity)
                    val result = geocoder.getFromLocation(clickLocation.latitude, clickLocation.longitude, 1)
                    location = if (!result.isEmpty())
                        Location.fromAddress(result[0])
                    else
                        Location.fromCoordinates(clickLocation.latitude, clickLocation.longitude)
                    uiThread {
                        marker?.remove()
                        marker = map.addMarker(MarkerOptions().position(clickLocation).title(location!!.name).snippet(location!!.address))
                    }
                }
            }
        }

        placeFragment.setHint(resources.getString(R.string.create_trip_hint_searchlocations))
        placeFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p: Place?) {
                placeFragment.setText("")
                p?.let { place ->
                    location = Location.fromPlace(place)
                    marker?.remove()
                    marker = googleMap?.addMarker(MarkerOptions()
                            .position(place.latLng)
                            .title(place.name.toString())
                            .snippet(place.address.toString())
                    )
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 15F))
                }
            }
            override fun onError(p0: Status?) {}
        })


        val locationService = LocationServices.getFusedLocationProviderClient(this)
        btn_pickcurloc.setOnClickListener {
            val permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (permissionResult == PackageManager.PERMISSION_GRANTED)
                locationService.lastLocation.addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        val latlng = LatLng(it.result.latitude, it.result.longitude)
                        doAsync {
                            val geocoder = Geocoder(this@AddEntryActivity)
                            val result = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
                            location = if (!result.isEmpty())
                                Location.fromAddress(result[0])
                            else
                                Location.fromCoordinates(latlng.latitude, latlng.longitude)
                            uiThread {
                                marker?.remove()
                                marker = googleMap?.addMarker(MarkerOptions().position(latlng))
                                marker!!.title = location!!.name
                                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14F))
                            }
                        }
                    }
                }
        }
    }

    fun getLocation(): Location? = location

    fun getTags(): List<String> = c_tags.tags.toList()

    fun getDate(): Date = date.time

    fun getInputTitle(): String = et_title.text.toString()

    fun getEntry(): EntryWithLocationAndTags? = entry
}
