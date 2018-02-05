package de.lulebe.vakation.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.google.android.gms.maps.MapView


class ScrollMapView: MapView {

    var touchInterceptListener: (() -> Unit)? = null

    constructor(ctx: Context): super(ctx)
    constructor(ctx: Context, attr: AttributeSet): super(ctx, attr)
    constructor(ctx: Context, attr: AttributeSet, styleDef: Int): super(ctx, attr, styleDef)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TIL", "0")
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN ->
                touchInterceptListener?.invoke()
            MotionEvent.ACTION_UP ->
                touchInterceptListener?.invoke()
        }

        // Handle MapView's touch events.
        return super.dispatchTouchEvent(ev)
    }

}