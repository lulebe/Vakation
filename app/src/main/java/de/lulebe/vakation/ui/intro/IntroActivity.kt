package de.lulebe.vakation.ui.intro

import agency.tango.materialintroscreen.MaterialIntroActivity
import android.content.Context
import android.os.Bundle


class IntroActivity: MaterialIntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLastSlideAlphaExitTransition(true)
        addSlide(IntroSlideIntro())
        addSlide(IntroSlideMedia())
        addSlide(IntroSlidePermissions())
    }

    override fun onFinish() {
        val sp = getSharedPreferences("internal", Context.MODE_PRIVATE)
        sp.edit().putBoolean("introDone", true).commit()
        super.onFinish()
    }
}