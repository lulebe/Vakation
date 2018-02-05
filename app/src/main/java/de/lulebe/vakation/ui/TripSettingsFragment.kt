package de.lulebe.vakation.ui


import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.lulebe.vakation.R
import de.lulebe.vakation.data.*
import de.lulebe.vakation.ui.views.ColorSchemeOptionView
import kotlinx.android.synthetic.main.fragment_tripsettings.*
import me.gujun.android.taggroup.TagGroup
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*


class TripSettingsFragment : Fragment() {

    private val mDateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private var mTrip: Trip? = null
    private var mTripTags: List<Tag> = emptyList()
    private val mStartDate = Calendar.getInstance()
    private val mEndDate = Calendar.getInstance()
    private var mCurrentlySelectingStartDate = true
    private var mPickedColorScheme = ColorScheme.FOREST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tripsettings, container, false)
        initViews(v)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadTrip(context)
    }

    private fun loadTrip(ctx: Context) {
        doAsync {
            val db = AppDB.getInstance(ctx)
            db.tripDao().getOneLive(arguments.getLong("tripId", 0)).observe(this@TripSettingsFragment, Observer {
                mTrip = it
                uiThread { updateViews() }
            })
            db.tagLinkDao().getTagsForTripOnlyLive(arguments.getLong("tripId")).observe(this@TripSettingsFragment, Observer {
                mTripTags = it ?: emptyList()
                uiThread { updateViews() }
            })
        }
    }


    private fun initViews(v: View) {
        v.findViewById<View>(R.id.btn_delete).setOnClickListener(deleteBtnListener)
        v.findViewById<TagGroup>(R.id.c_tags).setOnTagChangeListener(mTagChangeListener)
        initDateViews(v)
        v.findViewById<ColorSchemeOptionView>(R.id.colorpicker_forest).setOnClickListener {setPickedColorScheme(ColorScheme.FOREST) }
        v.findViewById<ColorSchemeOptionView>(R.id.colorpicker_beach).setOnClickListener { setPickedColorScheme(ColorScheme.BEACH) }
        v.findViewById<ColorSchemeOptionView>(R.id.colorpicker_city).setOnClickListener { setPickedColorScheme(ColorScheme.CITY) }
        v.findViewById<ColorSchemeOptionView>(R.id.colorpicker_party).setOnClickListener { setPickedColorScheme(ColorScheme.PARTY) }
        v.findViewById<View>(R.id.btn_save).setOnClickListener(saveChanges)
    }

    private fun updateViews() {
        if (mTrip == null) {
            loading.visibility = View.VISIBLE
            content.visibility = View.INVISIBLE
            return
        } else {
            loading.visibility = View.GONE
            content.visibility = View.VISIBLE
        }
        mTrip?.let { trip ->
            activity.title = trip.name
            et_name.setText(trip.name)
            mStartDate.time = trip.startDate
            et_startdate.text = mDateFormatter.format(trip.startDate)
            mEndDate.time = trip.endDate
            et_enddate.text = mDateFormatter.format(trip.endDate)
            setPickedColorScheme(trip.colorScheme)
        }
        c_tags.setTags(mTripTags.map { it.name })
    }


    private fun initDateViews(v: View) {
        val datePicker = DatePickerDialog(context, { _, year, month, day ->
            if (mCurrentlySelectingStartDate) {
                setStartDate(year, month, day)
                if (mStartDate.after(mEndDate))
                    setEndDate(year, month, day)

            } else {
                setEndDate(year, month, day)
                if (mEndDate.before(mStartDate))
                    setStartDate(year, month, day)
            }
        }, 2017, 5, 20)
        v.findViewById<View>(R.id.btn_edit_startdate).setOnClickListener { _ ->
            mCurrentlySelectingStartDate = true
            datePicker.updateDate(
                    mStartDate.get(Calendar.YEAR),
                    mStartDate.get(Calendar.MONTH),
                    mStartDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
        v.findViewById<View>(R.id.btn_edit_enddate).setOnClickListener { _ ->
            mCurrentlySelectingStartDate = false
            datePicker.updateDate(
                    mEndDate.get(Calendar.YEAR),
                    mEndDate.get(Calendar.MONTH),
                    mEndDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun setStartDate(year: Int, month: Int, day: Int) {
        mStartDate.set(Calendar.YEAR, year)
        mStartDate.set(Calendar.MONTH, month)
        mStartDate.set(Calendar.DAY_OF_MONTH, day)
        et_startdate.text = mDateFormatter.format(mStartDate.time)
    }

    private fun setEndDate(year: Int, month: Int, day: Int) {
        mEndDate.set(Calendar.YEAR, year)
        mEndDate.set(Calendar.MONTH, month)
        mEndDate.set(Calendar.DAY_OF_MONTH, day)
        et_enddate.text = mDateFormatter.format(mEndDate.time)
    }


    private val deleteBtnListener: (View) -> Unit = {
        mTrip?.let { trip ->
            doAsync {
                val db = AppDB.getInstance(context)
                db.tripDao().deleteTrip(trip)
                uiThread {
                    activity.finish()
                }
            }
        }
    }

    private val mTagChangeListener = object: TagGroup.OnTagChangeListener {
        override fun onDelete(tagGroup: TagGroup?, tag: String?) {
            tag?.let {
                doAsync {
                    AppDB.getInstance(context).tagLinkDao()
                            .deleteTagLinkByNameFromTrip(arguments.getLong("tripId"), it)
                }
            }
        }
        override fun onAppend(tagGroup: TagGroup?, tag: String?) {
            tag?.let {
                doAsync {
                    val db = AppDB.getInstance(context)
                    TagLinkUtils.addTagToTrip(db, arguments.getLong("tripId"), it)
                }
            }
        }
    }

    private val setPickedColorScheme = { scheme: ColorScheme ->
        mPickedColorScheme = scheme
        val dp2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2F, resources.displayMetrics)
        val activated: Array<Boolean> = when (scheme) {
            ColorScheme.FOREST -> {
                arrayOf(true, false, false, false)
            }
            ColorScheme.BEACH -> {
                arrayOf(false, true, false, false)
            }
            ColorScheme.CITY -> {
                arrayOf(false, false, true, false)
            }
            ColorScheme.PARTY -> {
                arrayOf(false, false, false, true)
            }
        }
        colorpicker_forest.setAsCurrentChoice(activated[0])
        colorpicker_forest.translationZ = if (activated[0]) dp2 else 0F
        colorpicker_beach.setAsCurrentChoice(activated[1])
        colorpicker_beach.translationZ = if (activated[1]) dp2 else 0F
        colorpicker_city.setAsCurrentChoice(activated[2])
        colorpicker_city.translationZ = if (activated[2]) dp2 else 0F
        colorpicker_party.setAsCurrentChoice(activated[3])
        colorpicker_party.translationZ = if (activated[3]) dp2 else 0F
    }

    private val saveChanges: (View) -> Unit = {
        val newName = et_name.text.toString()
        val newStartDate = mStartDate.time
        val newEndDate = mEndDate.time
        val newColorscheme = mPickedColorScheme
        doAsync {
            mTrip?.let {
                it.name = newName
                it.startDate = newStartDate
                it.endDate = newEndDate
                it.colorScheme = newColorscheme
                AppDB.getInstance(context).tripDao().updateTrip(it)
            }
        }
    }

}