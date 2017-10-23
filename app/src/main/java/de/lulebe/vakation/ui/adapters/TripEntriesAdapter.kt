package de.lulebe.vakation.ui.adapters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.media.MediaPlayer
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import de.lulebe.vakation.R
import de.lulebe.vakation.data.EntryType
import de.lulebe.vakation.data.EntryWithLocationAndTags
import de.lulebe.vakation.data.Location
import de.lulebe.vakation.ui.views.ScrollMapView
import me.gujun.android.taggroup.TagGroup


class TripEntriesAdapter(lo: LifecycleOwner): RecyclerView.Adapter<TripEntriesAdapter.ViewHolder>() {

    private var mPlayer: MediaPlayer? = null
    private var mMapView: ScrollMapView? = null
    private var mMapViewHolder: MapViewHolder? = null
    private var boundsPadding = 50
    var mapTouchInterceptListener: (() -> Unit)? = null

    init {
        lo.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreateParent() {
                mMapView?.onCreate(null)
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyParent() {
                mMapView?.onDestroy()
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStartParent() {
                mMapView?.onStart()
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStopParent() {
                mMapView?.onStop()
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResumeParent() {
                mMapView?.onResume()
            }
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPauseParent() {
                mMapView?.onPause()
            }
        })
    }
    var itemClickedListener: ((EntryWithLocationAndTags) -> Unit)? = null
    var itemLongClickedListener: ((EntryWithLocationAndTags) -> Unit)? = null
    var tagClickedListener: ((String) -> Unit)? = null
    var audioPlaybackRequestListener: ((String, () -> Unit) -> Unit)? = null
    var audioPauseRequestListener: (() -> Unit)? = null

    private val TYPE_EMPTY_INFO = 0
    private val TYPE_TRIP_MAP = 1
    private val TYPE_TRIP_TAGS = 2
    private val TYPE_TEXT = 3
    private val TYPE_AUDIO = 4
    private val TYPE_VIDEO = 5
    private val TYPE_IMAGE = 6

    private val entryOffset = 2

    private var items: List<EntryWithLocationAndTags> = emptyList()
    private var tripTags: List<String> = emptyList()
    private var tripLocations: List<Location> = emptyList()

    fun setItems(p0: List<EntryWithLocationAndTags>) {
        items = p0
        notifyDataSetChanged()
    }
    fun setTripTags(p0: List<String>) {
        tripTags = p0
        notifyItemChanged(1)
    }
    fun setTripLocations(p0: List<Location>) {
        tripLocations = p0
        mMapViewHolder?.updateLocations()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is MapViewHolder -> {
                holder.map.onResume()
            }
            is TagsViewHolder -> {
                holder.tagsList.setTags(tripTags)
                holder.showTagsCard(!tripTags.isEmpty())
            }
            is TextViewHolder -> {
                val item = items[position-2]
                holder.tvTitle.text = item.title
                holder.tvText.text = item.data
                holder.tagsList.setTags(item.tags.map { it.name })
            }
            is AudioViewHolder -> {
                val item = items[position-2]
                holder.tvTitle.text = item.title
                holder.tagsList.setTags(item.tags.map { it.name })
            }
        }
    }

    override fun getItemCount(): Int = if (items.isEmpty()) entryOffset+1 else items.size + entryOffset

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_TRIP_MAP
        if (position == 1) return TYPE_TRIP_TAGS
        if (items.isEmpty()) return TYPE_EMPTY_INFO
        return when(items[position-entryOffset].type) {
            EntryType.IMAGE -> TYPE_IMAGE
            EntryType.AUDIO -> TYPE_AUDIO
            EntryType.VIDEO -> TYPE_VIDEO
            else -> TYPE_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when(viewType) {
        TYPE_EMPTY_INFO -> {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_emptyinfo, parent, false)
            EmptyinfoViewHolder(v)
        }
        TYPE_TRIP_MAP -> {
            boundsPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, parent.resources.displayMetrics).toInt()
            val v = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_map, parent, false)
            mMapViewHolder = MapViewHolder(v)
            mMapViewHolder!!
        }
        TYPE_TRIP_TAGS -> {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_tags, parent, false)
            TagsViewHolder(v)
        }
        TYPE_IMAGE -> {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_text, parent, false)
            TextViewHolder(v)
        }
        TYPE_VIDEO -> {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_text, parent, false)
            TextViewHolder(v)
        }
        TYPE_AUDIO -> {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_audio, parent, false)
            AudioViewHolder(v)
        }
        else -> {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.feeditem_text, parent, false)
            TextViewHolder(v)
        }
    }

    abstract inner class ViewHolder(v: View): RecyclerView.ViewHolder(v)

    inner class EmptyinfoViewHolder(v: View): ViewHolder(v)

    inner class MapViewHolder(v: View): ViewHolder(v) {
        val map = v.findViewById<ScrollMapView>(R.id.map)!!
        init {
            this.setIsRecyclable(false)
            mMapView = map
            map.onCreate(null)
            map.touchInterceptListener = {
                mapTouchInterceptListener?.invoke()
            }
            updateLocations()
        }
        fun updateLocations() {
            map.getMapAsync { m ->
                m.setMaxZoomPreference(17F)
                m.clear()
                if (!tripLocations.isEmpty()) {
                    val boundsBuilder = LatLngBounds.Builder()
                    tripLocations.forEach {
                        val latlng = LatLng(it.latitude, it.longitude)
                        m.addMarker(MarkerOptions().position(latlng).title(it.name).snippet(it.address))
                        boundsBuilder.include(latlng)
                    }
                    m.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), boundsPadding))
                }
            }
        }
    }

    inner class TagsViewHolder(v: View): ViewHolder(v) {
        val tagsList = v.findViewById<TagGroup>(R.id.c_tags)!!
        private val tagsCard = v.findViewById<View>(R.id.tagscard)
        init {
            tagsList.setOnTagClickListener {
                tagClickedListener?.invoke(it)
            }
        }
        fun showTagsCard(show: Boolean) {
            tagsCard.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    abstract inner class EntryViewHolder(v: View): ViewHolder(v) {
        val tvTitle = v.findViewById<TextView>(R.id.tv_title)!!
        val tagsList = v.findViewById<TagGroup>(R.id.c_tags)!!
        init {
            v.setOnClickListener {
                itemClickedListener?.invoke(items[adapterPosition-entryOffset])
            }
            v.setOnLongClickListener {
                itemLongClickedListener?.invoke(items[adapterPosition-entryOffset])
                true
            }
            tagsList.setOnTagClickListener {
                tagClickedListener?.invoke(it)
            }
        }
    }

    inner class TextViewHolder(v: View): EntryViewHolder(v) {
        val tvText = v.findViewById<TextView>(R.id.tv_text)!!
    }

    inner class AudioViewHolder(v: View): EntryViewHolder(v) {
        private val btnPlayStop = v.findViewById<ImageView>(R.id.btn_playstop)!!
        private var isPlaying = false
        private var isActive = false
        init {
            btnPlayStop.setOnClickListener {
                setIsRecyclable(false)
                if (!isPlaying) {
                    isPlaying = true
                    if (!isActive) {
                        isActive = true
                        audioPlaybackRequestListener?.invoke(items[adapterPosition-entryOffset].data, {
                            isPlaying = false
                            isActive = false
                            setIsRecyclable(true)
                            btnPlayStop.setImageResource(R.drawable.ic_play_circle_filled_grey600_24dp)
                        })
                    } else {
                        audioPauseRequestListener?.invoke()
                    }
                    btnPlayStop.setImageResource(R.drawable.ic_pause_circle_filled_grey600_24dp)
                } else {
                    isPlaying = false
                    btnPlayStop.setImageResource(R.drawable.ic_play_circle_filled_grey600_24dp)
                    audioPauseRequestListener?.invoke()
                }
            }
        }
    }
}