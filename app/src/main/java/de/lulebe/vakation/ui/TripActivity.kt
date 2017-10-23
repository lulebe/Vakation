package de.lulebe.vakation.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import de.lulebe.vakation.R
import de.lulebe.vakation.data.ColorScheme
import kotlinx.android.synthetic.main.activity_trip.*

class TripActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    var themeId = R.style.ForestTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setThemeForTrip(intent.getStringExtra("colorScheme"))
        setContentView(R.layout.activity_trip)
        title = intent.getStringExtra("name")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setThemeForTrip(scheme: String) {
        val themes = mapOf(
                Pair(ColorScheme.FOREST, R.style.ForestTheme),
                Pair(ColorScheme.BEACH, R.style.BeachTheme),
                Pair(ColorScheme.CITY, R.style.CityTheme),
                Pair(ColorScheme.PARTY, R.style.PartyTheme)
        )
        val themeToSet = themes[ColorScheme.valueOf(scheme)]
        if (themeToSet != null) {
            setTheme(themeToSet)
            themeId = themeToSet
        }
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val fragment = when (position) {
                0 -> TripFeedFragment()
                else -> TripSettingsFragment()
            }
            val args = Bundle()
            args.putLong("tripId", intent.getLongExtra("tripId", 0))
            fragment.arguments = args
            return fragment
        }

        override fun getCount(): Int = 2
    }
}
