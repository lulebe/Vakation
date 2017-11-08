package de.lulebe.vakation.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AudioAmpView: View {

    private var _ampsData = emptyList<Int>()
    var ampsData: List<Int>   //should contain 40 data points
        get() = _ampsData
        set(value) {
            _ampsData = value
            recalculateRenderData(value)
        }
    private var renderData = (0..39).map { it.toFloat() }
    private var _playProgress = 0F
    var playProgress: Float   //0-1
        get() = _playProgress
        set(value) {
            _playProgress = value
            invalidate()
        }
    private var _playing = false
    var playing: Boolean
        get() = _playing
        set(value) {
            _playing = value
            invalidate()
        }

    private var mBarWidthWithSpacing = 0F
    private var mBarWidth = 0F
    private var mBarHeight = 0F
    private val mProgressWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2F, resources.displayMetrics)

    private var mBarPaint = Paint()
    private var mProgressPaint = Paint()

    init {
        mBarPaint.color = Color.GRAY
        mProgressPaint.color = Color.parseColor("#333333")
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBarWidthWithSpacing = w / 40F
        mBarWidth = mBarWidthWithSpacing * 0.8F
        mBarHeight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val dbottom = height.toFloat()
        renderData.forEachIndexed { index, amplitude ->
            var dtop = mBarHeight * (1F - amplitude/100F)
            if (dtop >= dbottom-2)
                dtop -= 2
            val dleft = index * mBarWidthWithSpacing
            canvas.drawRect(dleft, dtop, dleft + mBarWidth, dbottom, mBarPaint)
        }
        if (playing) {
            val dleft = width * playProgress - mProgressWidth/2F
            canvas.drawRect(dleft, 0F, dleft + mProgressWidth, dbottom, mProgressPaint)
        }
    }

    private fun recalculateRenderData(amps: List<Int>) {
        doAsync {
            amps.forEach { Log.d("AMP", it.toString()) }
            val psf = LinearInterpolator().interpolate(
                    DoubleArray(amps.size) { it.toDouble() },
                    DoubleArray(amps.size) { amps[it].toDouble() }
            )
            val step = (amps.size-1) / 40.0
            val renderPoints = (0..40).map { psf.value(it*step).toFloat() }
            renderPoints.forEach { Log.d("RENDER", it.toString()) }
            uiThread {
                renderData = renderPoints
                invalidate()
            }
        }
    }
}