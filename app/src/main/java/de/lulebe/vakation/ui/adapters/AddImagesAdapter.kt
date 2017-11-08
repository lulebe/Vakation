package de.lulebe.vakation.ui.adapters

import android.content.Context
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import de.lulebe.vakation.R
import java.io.File

class AddImagesAdapter(private val mContext: Context, private val mItems: List<String>): RecyclerView.Adapter<AddImagesAdapter.ViewHolder>() {

    var onDeleteListener: ((String) -> Unit)? = null

    override fun getItemCount(): Int = mItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.loadImg()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.listitem_addimage, parent, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val ivImg = v.findViewById<ImageView>(R.id.iv_img)
        init {
            v.findViewById<View>(R.id.btn_delete).setOnClickListener {
                onDeleteListener?.invoke(mItems[adapterPosition])
            }
        }
        fun loadImg() {
            Glide.with(ivImg).load(getImgPath(adapterPosition)).into(ivImg)
        }
    }

    private fun getImgPath(position: Int): String =
            mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).canonicalPath +
                    File.separator +
                    mItems[position]
}