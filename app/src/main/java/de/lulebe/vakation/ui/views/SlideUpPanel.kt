package de.lulebe.vakation.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import de.lulebe.vakation.R


class SlideUpPanel: FrameLayout {

    constructor(ctx: Context): super(ctx)
    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, styleDef: Int): super(ctx, attrs, styleDef)

    private val collapsedHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48F, resources.displayMetrics)
    private val animTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    private var openBtn: View? = null

    private var expanded = false
    private var prevY = 0F
    private val dragListener = { v: View, ev: MotionEvent ->
        val origTransY = height - collapsedHeight
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                prevY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                var newTransY = translationY + ev.rawY - prevY
                if (newTransY < 0) newTransY = 0F
                else if (newTransY > origTransY) newTransY = origTransY
                translationY = newTransY
                openBtn?.rotation = 180 * (1F- (translationY / (height-collapsedHeight)))
                prevY = ev.rawY
            }
            MotionEvent.ACTION_UP -> {
                var newTransY = translationY + ev.rawY - prevY
                if (newTransY < 0) newTransY = 0F
                else if (newTransY > origTransY) newTransY = origTransY
                if ((newTransY > origTransY*0.3F && expanded) || (newTransY > origTransY*0.7F && !expanded))
                    collapse()
                else
                    expand()
                prevY = ev.rawY
            }
        }
        true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        openBtn = findViewById(R.id.slideuppanel_openbtn)
        openBtn?.let {
            it.setOnClickListener {
                if (expanded) collapse() else expand()
            }
        }
        val dragger = findViewById<View>(R.id.slideuppanel_dragger)
        dragger.setOnTouchListener(dragListener)
    }

    private fun expand() {
        expanded = true
        animate()
                .translationY(0F)
                .setDuration(animTime)
                .setInterpolator(DecelerateInterpolator())
                .start()
        openBtn?.animate()?.rotation(180F)?.setDuration(animTime)?.start()
    }

    private fun collapse() {
        expanded = false
        animate()
                .translationY(height - collapsedHeight)
                .setDuration(animTime)
                .setInterpolator(DecelerateInterpolator())
                .start()
        openBtn?.animate()?.rotation(0F)?.setDuration(animTime)?.start()
    }

}