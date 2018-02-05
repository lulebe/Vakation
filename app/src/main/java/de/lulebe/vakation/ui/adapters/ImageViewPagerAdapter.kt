package de.lulebe.vakation.ui.adapters

import android.content.Context
import android.os.Environment
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jsibbold.zoomage.ZoomageView
import java.io.File


class ImageViewPagerAdapter(private val mContext: Context): PagerAdapter() {
    private var items = emptyList<String>()

    fun setItems(itms: List<String>) {
        items = itms
        notifyDataSetChanged()
    }

    private fun getImgPath(name: String): String =
            mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).canonicalPath + File.separator + name

    override fun isViewFromObject(view: View?, obj: Any?): Boolean = view == obj

    override fun getCount(): Int = items.size

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val v = ZoomageView(mContext)
        v.setScaleRange(1F, 5F)
        v.restrictBounds = true
        v.scaleType = ImageView.ScaleType.CENTER_INSIDE
        v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        Glide.with(v).load(getImgPath(items[position])).into(v)
        container?.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup?, position: Int, obj: Any?) {
        if (obj is View)
            container?.removeView(obj)
    }
}