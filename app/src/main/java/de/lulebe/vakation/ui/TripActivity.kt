package de.lulebe.vakation.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.lulebe.vakation.R
import kotlinx.android.synthetic.main.activity_trip.*

class TripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getLongExtra("tripId", 0).toString()
    }
}
