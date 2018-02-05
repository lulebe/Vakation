package de.lulebe.vakation.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class ColorSchemeView : View {

    private val colorBlockPaint = Paint()
    private var primaryColor = Color.GRAY
    private var accentColor = Color.RED

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        colorBlockPaint.color = primaryColor
        canvas.drawRect(0F, 0F, width.toFloat(), height/2F, colorBlockPaint)
        colorBlockPaint.color = accentColor
        canvas.drawRect(0F, height/2F, width.toFloat(), height.toFloat(), colorBlockPaint)
    }

    fun setColors(primary: Int, accent: Int) {
        primaryColor = primary
        accentColor = accent
        invalidate()
    }
}
