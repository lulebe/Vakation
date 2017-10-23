package de.lulebe.vakation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import de.lulebe.vakation.R
import de.lulebe.vakation.data.AppDB
import de.lulebe.vakation.data.Entry
import de.lulebe.vakation.data.EntryType
import de.lulebe.vakation.data.TagLinkUtils
import de.lulebe.vakation.ui.ViewExt.fadeIn
import de.lulebe.vakation.ui.ViewExt.fadeOut
import kotlinx.android.synthetic.main.activity_add_audio.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.lang.IllegalStateException
import java.lang.RuntimeException

class AddAudioActivity : AddEntryActivity() {

    private val PERMISSION_RC = 1
    private var started = false

    private var mPlayer: MediaPlayer? = null
    private var mRecorder: MediaRecorder? = null
    private var fileName = ""
    private var recorded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(R.layout.activity_add_audio)
        btn_record.isEnabled = false
        btn_record.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!recorded) startRecording() else startPlayback()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!recorded) endRecording() else endPlayback()
                    true
                }
                else -> false
            }
        }
        btn_delete.setOnClickListener { deleteRecording({showRecordBtn()}) }
        managePermissions()
    }

    override fun onStart() {
        super.onStart()
        started = true
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            createRecorder()
    }

    override fun onStop() {
        super.onStop()
        started = false
        destroyRecorder()
    }

    override fun cancelEntry() {
        deleteRecording()
        super.cancelEntry()
    }

    override fun saveEntry(done: () -> Unit) {
        if (!recorded) {
            Toast.makeText(this, "Please make a recording first.", Toast.LENGTH_SHORT).show()
            return
        }
        val title = getInputTitle()
        val tripId = intent.getLongExtra("tripId", 0)
        val tags = getTags()
        val time = getDate()
        doAsync {
            val db = AppDB.getInstance(this@AddAudioActivity)
            val entry = Entry(tripId, EntryType.AUDIO, time, title, fileName)
            val entryId = db.entryDao().insertEntry(entry)
            tags.forEach {
                TagLinkUtils.addTagToEntry(db, tripId, entryId, it)
            }
            getLocation()?.let {
                db.locationDao().insertLocation(it.toDbLocation(tripId, entryId))
            }
            uiThread { done() }
        }
    }

    override fun updateEntry(done: () -> Unit) {}
    override fun loadEntry() {}

    private fun startRecording() {
        val lift = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4F, resources.displayMetrics)
        btn_record.animate().scaleX(1.7F).scaleY(1.7F)
                .setDuration(80)
                .start()
        title = "recording..."
        mRecorder?.let {
            it.reset()
            deleteRecording()
            fileName = System.currentTimeMillis().toString() + ".aac"
            val path = getExternalFilesDir(Environment.DIRECTORY_MUSIC).path +
                    File.separator +
                    fileName
            it.setAudioSource(MediaRecorder.AudioSource.MIC)
            it.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            it.setOutputFile(path)
            it.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            it.setAudioChannels(2)
            it.setAudioSamplingRate(44100)
            it.setAudioEncodingBitRate(128000)
            it.prepare()
            it.start()
        }
    }

    private fun endRecording() {
        btn_record.animate().scaleX(1F).scaleY(1F)
                .setDuration(80)
                .start()
        title = resources.getString(R.string.title_activity_add_audio)
        mRecorder?.let { rec ->
            try {
                rec.stop()
                recorded = true
                showPlayBtns()
            } catch (e: RuntimeException) {
                doAsync {
                    Thread.sleep(1000)
                    uiThread {
                        try {
                            rec.stop()
                            recorded = true
                            showPlayBtns()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun startPlayback() {
        btn_record.animate().scaleX(1.5F).scaleY(1.5F)
            .setDuration(80)
            .start()
        val path = getExternalFilesDir(Environment.DIRECTORY_MUSIC).path +
                File.separator +
                fileName
        mPlayer = MediaPlayer()
        mPlayer?.let {
            it.setDataSource(path)
            it.prepare()
            it.start()
        }
    }

    private fun endPlayback() {
        btn_record.animate().scaleX(1F).scaleY(1F)
                .setDuration(80)
                .start()
        mPlayer?.release()
        mPlayer = null
    }

    private fun deleteRecording(done: () -> Unit = {}) {
        val path = getExternalFilesDir(Environment.DIRECTORY_MUSIC).path +
                File.separator +
                fileName
        doAsync {
            val recording = File(path)
            if (recording.exists()) {
                recording.delete()
            }
            recorded = false
            uiThread {
                done()
            }
        }
    }

    private fun createRecorder() {
        btn_record.isEnabled = true
        if (mRecorder != null) return
        mRecorder = MediaRecorder()
    }

    private fun destroyRecorder() {
        mRecorder?.release()
        mRecorder = null
        btn_record.isEnabled = false
    }

    private fun showRecordBtn() {
        btn_record.setImageResource(R.drawable.ic_microphone_white_24dp)
        btn_record.animate().translationX(0F).setDuration(100).setInterpolator(DecelerateInterpolator()).start()
        btn_delete.fadeOut()
    }

    private fun showPlayBtns() {
        val transX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96F, resources.displayMetrics)
        btn_record.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        btn_record.animate().translationX(transX).setDuration(100).setInterpolator(DecelerateInterpolator()).start()
        btn_delete.fadeIn()
    }

    private fun managePermissions() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_RC)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(started) createRecorder()
        } else
            finish()
    }
}
