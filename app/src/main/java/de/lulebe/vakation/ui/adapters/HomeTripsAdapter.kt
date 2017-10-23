package de.lulebe.vakation.ui.adapters

import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.lulebe.vakation.R
import de.lulebe.vakation.data.ColorScheme
import de.lulebe.vakation.data.DbLocation
import de.lulebe.vakation.data.TripWithLocationsAndTags
import de.lulebe.vakation.ui.views.ColorSchemeView
import me.gujun.android.taggroup.TagGroup
import java.text.SimpleDateFormat
import java.util.*


class HomeTripsAdapter: RecyclerView.Adapter<HomeTripsAdapter.ViewHolder>() {

    private var items = emptyList<TripWithLocationsAndTags>()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    var tripClickListener: ((trip: TripWithLocationsAndTags) -> Unit)? = null
    var tripLongClickListener: ((trip: TripWithLocationsAndTags) -> Unit)? = null
    var tagClickListener: ((tag: String) -> Unit)? = null

    fun setTrips (trips: List<TripWithLocationsAndTags>) {
        items = trips
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
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

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_trip, parent, false)
        return ViewHolder(view)
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

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvName = v.findViewById<TextView>(R.id.tv_name)!!
        val tvDates = v.findViewById<TextView>(R.id.tv_dates)!!
        val locationsList = v.findViewById<ViewGroup>(R.id.l_locations)!!
        val tagsList = v.findViewById<TagGroup>(R.id.c_tags)!!
        val colorScheme = v.findViewById<ColorSchemeView>(R.id.c_colorscheme)!!
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
                tripClickListener?.invoke(items[adapterPosition])
            }
            v.setOnLongClickListener {
                tripLongClickListener?.invoke(items[adapterPosition])
                tripLongClickListener != null
            }
            tagsList.setOnTagClickListener {
                tagClickListener?.invoke(it)
            }
        }
    }
}