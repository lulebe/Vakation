package de.lulebe.vakation.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.support.graphics.drawable.VectorDrawableCompat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import de.lulebe.vakation.R


class ColorSchemeOptionView : View {

    private val colorBlockPaint = Paint()
    private val namePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var primaryColor = Color.GRAY
    private var accentColor = Color.RED
    private var colorName = "nothing"
    private var picked = false
    private val tick = VectorDrawableCompat.create(resources, R.drawable.ic_done_white_24dp, null)
    private val tickTranslateX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28F, resources.displayMetrics)
    private val tickTranslateY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4F, resources.displayMetrics)
    private val tickSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40F, resources.displayMetrics).toInt()

    init {
        tick?.setBounds(0, 0, tickSize, tickSize)
        namePaint.color = Color.WHITE
        namePaint.textAlign = Paint.Align.CENTER
        namePaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16F, resources.displayMetrics)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init (attrs: AttributeSet?) {
        outlineProvider = OutlineProvider()
        attrs?.let {
            val a = context.theme.obtainStyledAttributes(it, R.styleable.ColorSchemeOptionView, 0, 0)
            try {
                primaryColor = a.getColor(R.styleable.ColorSchemeOptionView_primaryColor, Color.GRAY)
                accentColor = a.getColor(R.styleable.ColorSchemeOptionView_accentColor, Color.RED)
                colorName = a.getString(R.styleable.ColorSchemeOptionView_colorName)
            } finally {
                a.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val calculatedHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48F, resources.displayMetrics).toInt()
        val calculatedWidth = height*2
        setMeasuredDimension(calculatedWidth, calculatedHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        colorBlockPaint.color = primaryColor
        canvas.drawRect(0F, 0F, width/2F, height.toFloat(), colorBlockPaint)
        colorBlockPaint.color = accentColor
        canvas.drawRect(width/2F, 0F, width.toFloat(), height.toFloat(), colorBlockPaint)
        if (picked) {
            Log.d("TICK", (tick != null).toString())
            canvas.save()
            canvas.translate(tickTranslateX, tickTranslateY)
            tick?.draw(canvas)
            canvas.restore()
        } else {
            val xPos = canvas.width / 2F
            val yPos = (canvas.height / 2 - (namePaint.descent() + namePaint.ascent()) / 2)
            canvas.drawText(colorName, xPos, yPos, namePaint)
        }
    }

    fun setAsCurrentChoice(picked: Boolean) {
        this.picked = picked
        invalidate()
    }

    class OutlineProvider: ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRect(0, 0, view.width, view.height)
        }

    }
}
