package de.lulebe.vakation.ui.adapters

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import de.lulebe.vakation.R
import de.lulebe.vakation.data.ColorScheme
import de.lulebe.vakation.data.DbLocation
import de.lulebe.vakation.data.EntryType
import de.lulebe.vakation.data.SearchResult
import de.lulebe.vakation.ui.views.ColorSchemeView
import me.gujun.android.taggroup.TagGroup
import java.text.SimpleDateFormat
import java.util.*


class SearchResultsAdapter: RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    var resultClickListener: ((result: SearchResult) -> Unit)? = null
    var tagClickListener: ((tag: String) -> Unit)? = null

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var results: List<SearchResult> = emptyList()

    private val VIEW_TYPE_TRIP = 1
    private val VIEW_TYPE_ENTRY_TEXT = 2
    private val VIEW_TYPE_ENTRY_AUDIO = 3
    private val VIEW_TYPE_ENTRY_VIDEO = 4
    private val VIEW_TYPE_ENTRY_IMAGE = 5

    fun setResults(r: List<SearchResult>) {
        results = r
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is TripViewHolder -> {
                val item = results[position].trip!!
                holder.tvName.text = item.name
                holder.tvDates.text = arrayOf(
                        dateFormatter.format(item.startDate),
                        "to",
                        dateFormatter.format(item.endDate)
                ).joinToString(" ")
                fillLocationsList(holder.locationsList, item.locations)
                holder.tagsList.setTags(item.tags.map { it.name })
                ColorScheme.colorValues[item.colorScheme]?.let {
                    holder.colorScheme.setColors(it.first, it.second)
                }
            }
            is TextEntryViewHolder -> {
                val item = results[position].entry!!
                holder.tvTitle.text = item.title
                holder.tvText.text = item.data
                holder.tagsList.setTags(item.tags.map { it.name })
            }
        }
    }

    override fun getItemCount(): Int = results.size

    override fun getItemViewType(position: Int): Int = when(results[position].type) {
        SearchResult.Type.TRIP -> VIEW_TYPE_TRIP
        else -> {
            when(results[position].entry!!.type) {
                EntryType.IMAGE -> VIEW_TYPE_ENTRY_IMAGE
                EntryType.AUDIO -> VIEW_TYPE_ENTRY_IMAGE
                EntryType.VIDEO -> VIEW_TYPE_ENTRY_IMAGE
                else -> VIEW_TYPE_ENTRY_TEXT
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when(viewType) {
            VIEW_TYPE_TRIP -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_trip, parent, false)
                return TripViewHolder(view as ViewGroup)
            }
            VIEW_TYPE_ENTRY_IMAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_text, parent, false)
                return TextEntryViewHolder(view as ViewGroup)
            }
            VIEW_TYPE_ENTRY_AUDIO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_text, parent, false)
                return TextEntryViewHolder(view as ViewGroup)
            }
            VIEW_TYPE_ENTRY_VIDEO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_text, parent, false)
                return TextEntryViewHolder(view as ViewGroup)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_text, parent, false)
                return TextEntryViewHolder(view as ViewGroup)
            }
        }
    }

    private fun fillLocationsList(v: ViewGroup, locations: List<DbLocation>) {
        v.removeAllViews()
        locations.forEach {
            val child = LayoutInflater.from(v.context).inflate(R.layout.listitem_hometriplocations, v, false)
            child.findViewById<TextView>(R.id.tv_name).text = it.name
            child.findViewById<TextView>(R.id.tv_address).text = it.address
            v.addView(child)
        }
    }

    abstract inner class ViewHolder(v: ViewGroup): RecyclerView.ViewHolder(v) {
        val tagsList = v.findViewById<TagGroup>(R.id.c_tags)!!
        init {
            v.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> view.translationZ = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1F, view.context.resources.displayMetrics)
                    MotionEvent.ACTION_CANCEL -> view.translationZ = 0F
                    MotionEvent.ACTION_UP -> view.translationZ = 0F
                }
                false
            }
            v.setOnClickListener {
                resultClickListener?.invoke(results[adapterPosition])
            }
            tagsList.setOnTagClickListener {
                tagClickListener?.invoke(it)
            }
        }
    }

    inner class TripViewHolder(v: ViewGroup): ViewHolder(v) {
        val tvName = v.findViewById<TextView>(R.id.tv_name)!!
        val tvDates = v.findViewById<TextView>(R.id.tv_dates)!!
        val locationsList = v.findViewById<ViewGroup>(R.id.l_locations)!!
        val colorScheme = v.findViewById<ColorSchemeView>(R.id.c_colorscheme)!!
    }

    inner class TextEntryViewHolder(v: ViewGroup): ViewHolder(v) {
        val tvTitle = v.findViewById<TextView>(R.id.tv_title)!!
        val tvText = v.findViewById<TextView>(R.id.tv_text)!!
    }
}