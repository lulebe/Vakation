package de.lulebe.vakation.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import de.lulebe.vakation.R
import de.lulebe.vakation.data.AppDB
import de.lulebe.vakation.data.EntryType
import de.lulebe.vakation.data.Location
import de.lulebe.vakation.ui.adapters.TripEntriesAdapter
import io.github.kobakei.materialfabspeeddial.FabSpeedDial
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File


class TripFeedFragment : Fragment() {

    private val adapter = TripEntriesAdapter(this)

    private var mAudioPlayer: MediaPlayer? = null
    private var mAudioEndListener: (() -> Unit)? = null

    init {
        adapter.itemClickedListener = {
            // TODO open item
        }
        adapter.itemLongClickedListener = {
            AlertDialog.Builder(context)
                    .setTitle(R.string.trip_feed_deleteentrydialog_title)
                    .setMessage(resources.getString(R.string.trip_feed_deleteentrydialog_message) + it.title)
                    .setPositiveButton(android.R.string.ok, { di, _ ->
                        di.dismiss()
                        deleteEntry(it.uid)
                    })
                    .setNegativeButton(android.R.string.cancel, { di, _ ->
                        di.cancel()
                    })
                    .show()
        }
        adapter.tagClickedListener = {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra("query", it)
            intent.putExtra("tagOnly", true)
            startActivity(intent)
        }
        adapter.audioPlaybackRequestListener = { fileName, stopListener ->
            mAudioPlayer?.release()
            mAudioEndListener?.invoke()
            mAudioEndListener = stopListener
            val path = activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC).path +
                    File.separator +
                    fileName
            mAudioPlayer = MediaPlayer()
            mAudioPlayer?.let {
                it.setDataSource(path)
                it.prepare()
                it.setOnCompletionListener {
                    stopListener()
                    mAudioEndListener = null
                    it.release()
                }
                it.start()
            }
        }
        adapter.audioPauseRequestListener = {
            mAudioPlayer?.let {
                if (it.isPlaying) it.pause() else it.start()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tripfeed, container, false)

        val rv = v.findViewById<RecyclerView>(R.id.rv_feed)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter
        adapter.mapTouchInterceptListener = {
            Log.d("TIL", "2")
            rv.requestDisallowInterceptTouchEvent(true)
        }

        v.findViewById<FabSpeedDial>(R.id.fab).addOnMenuItemClickListener { _, _, itemId ->
            when (itemId) {
                R.id.mi_add_text -> openAddActivity(AddTextActivity::class.java)
                R.id.mi_add_audio -> openAddActivity(AddAudioActivity::class.java)
            }
        }

        connectToDB()

        return v
    }

    override fun onResume() {
        super.onResume()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    private fun openAddActivity(cls: Class<*>) {
        val intent = Intent(context, cls)
        intent.putExtra("tripId", arguments.getLong("tripId"))
        intent.putExtra("themeId", (activity as TripActivity).themeId)
        startActivity(intent)
    }

    private fun connectToDB() {
        doAsync {
            val db = AppDB.getInstance(context)
            val tripLocations = db.locationDao().getLocationsForTripOnly(arguments.getLong("tripId")).map {
                Location.fromDbLocation(it)
            }
            uiThread {
                adapter.setTripLocations(tripLocations)
            }
            val entriesLD = db.entryDao().getEntriesForTripLive(arguments.getLong("tripId"))
            val tripTagsLD = db.tagLinkDao().getTagsForTripOnlyLive(arguments.getLong("tripId"))
            entriesLD.observe(this@TripFeedFragment, Observer { entries ->
                if (entries != null) {
                    doAsync {
                        entries.forEach {
                            it.tags = db.tagLinkDao().getTagsForEntry(it.uid)
                        }
                        uiThread {
                            adapter.setItems(entries)
                        }
                    }
                } else
                    adapter.setItems(emptyList())
            })
            tripTagsLD.observe(this@TripFeedFragment, Observer { tags ->
                if (tags != null) {
                    adapter.setTripTags(tags.map { it.name })
                } else
                    adapter.setTripTags(emptyList())
            })
        }
    }

    private fun deleteEntry(entryId: Long) {
        doAsync {
            val db = AppDB.getInstance(context)
            val entry = db.entryDao().getOne(entryId)
            when (entry.type) {
                EntryType.AUDIO -> {
                    val file = File(activity.getExternalFilesDir(Environment.DIRECTORY_MUSIC).path +
                            File.separator +
                            entry.data)
                    if (file.exists())
                        file.delete()
                }
            }
            db.entryDao().deleteEntryById(entryId)
        }
    }
}
