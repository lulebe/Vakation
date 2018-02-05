package de.lulebe.vakation.data

import android.graphics.Color

enum class ColorScheme {
    FOREST, BEACH, CITY, PARTY;
    companion object {
        val colorValues = mapOf(
                Pair(ColorScheme.FOREST, Pair(Color.parseColor("#795548"), Color.parseColor("#81C784"))),
                Pair(ColorScheme.BEACH, Pair(Color.parseColor("#03A9f4"), Color.parseColor("#d2ae29"))),
                Pair(ColorScheme.CITY, Pair(Color.parseColor("#607D8B"), Color.parseColor("#FF5252"))),
                Pair(ColorScheme.PARTY, Pair(Color.parseColor("#9C27B0"), Color.parseColor("#36b495")))
        )
    }
}