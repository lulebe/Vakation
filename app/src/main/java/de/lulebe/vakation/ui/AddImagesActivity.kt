package de.lulebe.vakation.ui

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import de.lulebe.vakation.R
import de.lulebe.vakation.data.AppDB
import de.lulebe.vakation.data.Entry
import de.lulebe.vakation.data.EntryType
import de.lulebe.vakation.data.TagLinkUtils
import de.lulebe.vakation.ui.ViewExt.fadeIn
import de.lulebe.vakation.ui.ViewExt.fadeOut
import de.lulebe.vakation.ui.adapters.AddImagesAdapter
import kotlinx.android.synthetic.main.activity_add_images.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File


class AddImagesActivity : AddEntryActivity() {

    private val TAKE_PIC_RC = 1
    private val IMG_IMPORT_RC = 2
    private var mImgIncrement = 1
    private var mCurrentImgName = ""
    private var mCurrentImgPath
        get() = getExternalFilesDir(Environment.DIRECTORY_PICTURES).canonicalPath + File.separator + mCurrentImgName
        set(value) {
            mCurrentImgName = value.removePrefix(getExternalFilesDir(Environment.DIRECTORY_PICTURES).canonicalPath + File.separator)
        }
    private val mImages = mutableListOf<String>()
    private var mAdapter = AddImagesAdapter(this, mImages)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(R.layout.activity_add_images)
        btn_take_photo.setOnClickListener { takePicture() }
        btn_pick_photo.setOnClickListener { startImagePicker() }
        rv_imgs.layoutManager = LinearLayoutManager(this)
        rv_imgs.adapter = mAdapter
        mAdapter.onDeleteListener = {
            deleteImg(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PIC_RC && resultCode == Activity.RESULT_OK) {
            sendMediaScanIntent(mCurrentImgPath)
            mImages.add(mCurrentImgName)
            mAdapter.notifyDataSetChanged()
            renderEmptyOrList()
        } else if (requestCode == IMG_IMPORT_RC && resultCode == Activity.RESULT_OK && data != null) {
            importImgs(data)
        }
    }

    private fun renderEmptyOrList() {
        if (mImages.isEmpty()) {
            rv_imgs.fadeOut()
            emptyinfo.fadeIn()
        } else {
            rv_imgs.fadeIn()
            emptyinfo.fadeOut()
        }
    }

    private fun sendMediaScanIntent(path: String) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(File(path))
        sendBroadcast(intent)
    }

    private fun startImagePicker() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, IMG_IMPORT_RC)
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imgFile = File.createTempFile(System.currentTimeMillis().toString(), ".jpg", dir)
            mCurrentImgPath = imgFile.canonicalPath
            val imgUri = FileProvider.getUriForFile(this, "de.lulebe.vakation", imgFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            startActivityForResult(intent, TAKE_PIC_RC)
        }
    }

    private fun importImgs(int: Intent) {
        val ad = AlertDialog.Builder(this)
                .setTitle("loading...")
                .setCancelable(false)
                .show()
        doAsync {
            if (int.clipData != null) {
                importMultipleImgs(int.clipData)
            } else if (int.data != null) {
                importSingleImg(int.data)
            }
            uiThread {
                ad.dismiss()
                mAdapter.notifyDataSetChanged()
                renderEmptyOrList()
            }
        }
    }

    private fun importSingleImg(uri: Uri) {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
        val name = System.currentTimeMillis().toString() + "_" + (mImgIncrement++).toString() + "." + extension
        val path = dir.canonicalPath + File.separator + name
        val imgFile = File(path)
        try {
            val input = contentResolver.openInputStream(uri)
            input.use { inp ->
                imgFile.outputStream().use { out ->
                    inp.copyTo(out)
                }
            }
            mImages.add(name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun importMultipleImgs(clipData: ClipData) {
        (0..(clipData.itemCount-1))
                .map { clipData.getItemAt(it).uri }
                .forEach {
                    importSingleImg(it)
                    Log.d("IMG_IMPORT", it.toString())
                }
    }

    private fun deleteImg(name: String) {
        mImages.remove(name)
        mAdapter.notifyDataSetChanged()
        renderEmptyOrList()
        doAsync {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).canonicalPath + File.separator + name)
            if (file.exists())
                file.delete()
        }
    }

    override fun saveEntry(done: () -> Unit) {
        if (mCurrentImgPath.isEmpty()) {
            Toast.makeText(this, "Please pick an image first.", Toast.LENGTH_SHORT).show()
            done()
            return
        }
        val title = getInputTitle()
        val tripId = intent.getLongExtra("tripId", 0)
        val tags = getTags()
        val time = getDate()
        doAsync {
            val db = AppDB.getInstance(this@AddImagesActivity)
            val entry = Entry(tripId, EntryType.IMAGES, time, title, mImages.joinToString("|"))
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
}