package de.lulebe.vakation.ui.views

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import de.lulebe.vakation.R
import de.lulebe.vakation.data.Location

class CreateTripLocationsView: LinearLayout {

    var onDeleteListener: ((Location) -> Unit)? = null

    private var _places = mutableListOf<Location>()
    var places: MutableList<Location>
        get () = _places
        set(value) {
            _places = value
            notifyDatasetChanged()
        }

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)

    init {
        orientation = LinearLayout.VERTICAL
    }

    fun notifyDatasetChanged() {
        removeAllViews()
        renderChildren()
    }

    private fun renderChildren() {
        places.forEach { loc ->
            val view = LayoutInflater.from(context).inflate(R.layout.listitem_triplocations, this, false)
            view.findViewById<TextView>(R.id.tv_name).text = loc.name
            view.findViewById<TextView>(R.id.tv_address).text = loc.address
            view.findViewById<View>(R.id.btn_delete).setOnClickListener {
                onDeleteListener?.invoke(loc)
                places.remove(loc)
                val animator = view.animate()
                animator.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                animator.scaleX(0.7F)
                animator.scaleY(0.7F)
                animator.alpha(0F)
                animator.interpolator = DecelerateInterpolator()
                animator.setListener(object: Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationStart(p0: Animator?) {}
                    override fun onAnimationEnd(p0: Animator?) {
                        notifyDatasetChanged()
                    }
                })
                animator.start()
            }
            addView(view)
        }
    }
}