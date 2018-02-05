package de.lulebe.vakation.ui.intro

import agency.tango.materialintroscreen.SlideFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.lulebe.vakation.R

class IntroSlideMedia: SlideFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.intro, container, false)
        view.findViewById<ImageView>(R.id.image).setImageResource(R.drawable.intro2)
        view.findViewById<TextView>(R.id.title).setText(R.string.intro_slide2_title)
        view.findViewById<TextView>(R.id.description).setText(R.string.intro_slide2_description)
        return view
    }

    override fun backgroundColor() = R.color.introBG2
    override fun buttonsColor() = R.color.colorAccent
    override fun canMoveFurther() = true
}