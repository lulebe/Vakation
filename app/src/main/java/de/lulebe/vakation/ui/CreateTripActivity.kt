package de.lulebe.vakation.ui

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.lulebe.vakation.R
import de.lulebe.vakation.data.*
import de.lulebe.vakation.ui.views.ScrollMapFragment
import kotlinx.android.synthetic.main.activity_create_trip.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class CreateTripActivity : AppCompatActivity() {

    private val startDate = Calendar.getInstance()
    private val endDate = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var currentlySelectingStartDate = true

    private var pickedColorScheme = ColorScheme.FOREST

    private val setPickedColorScheme = { scheme: ColorScheme ->
        pickedColorScheme = scheme
        val dp2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2F, resources.displayMetrics)
        val activated: Array<Boolean>
        when (scheme) {
            ColorScheme.FOREST -> {
                setTheme(R.style.ForestTheme)
                toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryForest, null))
                fab.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.colorAccentForest, null)
                )
                activated = arrayOf(true, false, false, false)
            }
            ColorScheme.BEACH -> {
                setTheme(R.style.BeachTheme)
                toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryBeach, null))
                fab.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.colorAccentBeach, null)
                )
                activated = arrayOf(false, true, false, false)
            }
            ColorScheme.CITY -> {
                setTheme(R.style.CityTheme)
                toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryCity, null))
                fab.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.colorAccentCity, null)
                )
                activated = arrayOf(false, false, true, false)
            }
            ColorScheme.PARTY -> {
                setTheme(R.style.PartyTheme)
                toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryParty, null))
                fab.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(resources, R.color.colorAccentParty, null)
                )
                activated = arrayOf(false, false, false, true)
            }
        }
        colorpicker_forest.setAsCurrentChoice(activated[0])
        colorpicker_forest.translationZ = if (activated[0]) dp2 else 0F
        colorpicker_beach.setAsCurrentChoice(activated[1])
        colorpicker_beach.translationZ = if (activated[1]) dp2 else 0F
        colorpicker_city.setAsCurrentChoice(activated[2])
        colorpicker_city.translationZ = if (activated[2]) dp2 else 0F
        colorpicker_party.setAsCurrentChoice(activated[3])
        colorpicker_party.translationZ = if (activated[3]) dp2 else 0F
    }

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_trip)
        toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryForest, null))
        fab.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorAccentForest, null))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        et_name.requestFocus()
    }

    private fun initViews() {
        fab.setOnClickListener {
            saveAndFinish()
        }
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            if (currentlySelectingStartDate) {
                startDate.set(year, month, day)
                if (startDate.after(endDate))
                    endDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH))
                et_startdate.text = dateFormatter.format(startDate.time)
                et_enddate.text = dateFormatter.format(endDate.time)
            } else {
                endDate.set(year, month, day)
                if (endDate.before(startDate))
                    startDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH))
                et_enddate.text = dateFormatter.format(endDate.time)
                et_startdate.text = dateFormatter.format(startDate.time)
            }
        }, 2017, 5, 20)
        et_startdate.text = dateFormatter.format(System.currentTimeMillis())
        btn_edit_startdate.setOnClickListener { _ ->
            currentlySelectingStartDate = true
            datePicker.updateDate(
                    startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
        et_enddate.text = dateFormatter.format(System.currentTimeMillis())
        btn_edit_enddate.setOnClickListener { _ ->
            currentlySelectingStartDate = false
            datePicker.updateDate(
                    endDate.get(Calendar.YEAR),
                    endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
        colorpicker_forest.setAsCurrentChoice(true)
        colorpicker_forest.setOnClickListener {setPickedColorScheme(ColorScheme.FOREST) }
        colorpicker_beach.setOnClickListener { setPickedColorScheme(ColorScheme.BEACH) }
        colorpicker_city.setOnClickListener { setPickedColorScheme(ColorScheme.CITY) }
        colorpicker_party.setOnClickListener { setPickedColorScheme(ColorScheme.PARTY) }
        manageMap()
    }

    private val markers = WeakHashMap<Location, Marker>()

    private fun manageMap() {
        val mapFragment = fragmentManager.findFragmentById(R.id.gmap) as MapFragment
        val placeFragment = fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment

        (fragmentManager.findFragmentById(R.id.gmap) as ScrollMapFragment).setListener {
            scrollview.requestDisallowInterceptTouchEvent(true)
        }

        mapFragment.getMapAsync { map ->
            googleMap = map
            map.setMaxZoomPreference(17F)
            map.setMinZoomPreference(2F)
            showUserLocation(map)
            map.setOnMapClickListener { clickLocation ->
                doAsync {
                    val geocoder = Geocoder(this@CreateTripActivity)
                    val result = geocoder.getFromLocation(clickLocation.latitude, clickLocation.longitude, 1)
                    var location = if (!result.isEmpty())
                        Location.fromAddress(result[0])
                    else
                        Location.fromCoordinates(clickLocation.latitude, clickLocation.longitude)
                    uiThread {
                        locationslist.places.add(location)
                        locationslist.notifyDatasetChanged()
                        val marker = map.addMarker(MarkerOptions().position(clickLocation))
                        marker.title = location.name
                        markers.put(location, marker)
                    }
                }
            }
        }

        locationslist.onDeleteListener = {
            markers[it]?.remove()
        }

        placeFragment.setHint(resources.getString(R.string.create_trip_hint_searchlocations))
        placeFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p: Place?) {
                placeFragment.setText("")
                p?.let { place ->
                    val existsAlready = locationslist.places.any {
                        it.latitude == place.latLng.latitude && it.longitude == place.latLng.longitude
                    }
                    if (!existsAlready) {
                        val loc = Location.fromPlace(place)
                        locationslist.places.add(loc)
                        locationslist.notifyDatasetChanged()
                        val marker = googleMap?.addMarker(MarkerOptions()
                                .position(place.latLng)
                                .title(place.name.toString())
                        )
                        markers.put(loc, marker)
                    }
                }
            }
            override fun onError(p0: Status?) {}
        })
    }

    private fun showUserLocation (map: GoogleMap) {
        val locationService = LocationServices.getFusedLocationProviderClient(this)
        val permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permissionResult == PackageManager.PERMISSION_GRANTED)
            locationService.lastLocation.addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    val latlng = LatLng(it.result.latitude, it.result.longitude)
                    map.addMarker(MarkerOptions()
                            .position(latlng)
                            .anchor(0.5F, 0.5F)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_userlocation))
                    )
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14F))
                }
            }
    }

    private fun saveAndFinish() {
        val name = et_name.text.toString()
        //startDate, endDate
        val colorScheme = pickedColorScheme
        val tags = input_tags.tags.toList()
        val locations = locationslist.places.toList()
        val trip = Trip(name, startDate.time, endDate.time, colorScheme)
        doAsync {
            val db = AppDB.getInstance(this@CreateTripActivity)
            val tripId = db.tripDao().insertTrip(trip)
            locations.forEach {
                val loc = it.toDbLocation(tripId, null)
                db.locationDao().insertLocation(loc)
            }
            tags.forEach {
                TagLinkUtils.addTagToTrip(db, tripId, it)
            }
            uiThread {
                finish()
            }
        }
    }
}
