package com.i69.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ArrayRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView
import com.i69.data.models.IdWithValue
import com.i69.data.models.Photo
import com.i69.BuildConfig
import com.i69.R
import com.i69.ui.views.InterestsView

@BindingAdapter("profileImage", requireAll = false)
fun ImageView.profileImage(imageUrls: List<Photo>?) {
    if (!imageUrls.isNullOrEmpty()) loadCircleImage(imageUrls.first().url!!)
}

@BindingAdapter("items", requireAll = false)
fun setItems(v: AppCompatSpinner, items: List<String>?) {
    if (items != null) {
        val adapter = ArrayAdapter(
            v.context,
            R.layout.item_no_default_spinner,
            R.id.textSpinner,
            items.toMutableList()
        )
        adapter.setDropDownViewResource(R.layout.item_no_default_spinner_dropdown_menu)
        v.adapter = adapter
    }
}

@BindingAdapter("spinnerItems", requireAll = false)
fun setSpinnerItems(v: AppCompatSpinner, itemsList: List<IdWithValue>?) {
    if (itemsList != null) {
        val items = itemsList.map { if (isCurrentLanguageFrench()) it.valueFr else it.value }
        val adapter = ArrayAdapter(
            v.context,
            R.layout.item_no_default_spinner,
            R.id.textSpinner,
            items.toMutableList()
        )
        adapter.setDropDownViewResource(R.layout.item_no_default_spinner_dropdown_menu)
        v.adapter = adapter
    }
}

@BindingAdapter("itemsHeightGenderRes", requireAll = false)
fun setItemsHeightGender(v: AppCompatSpinner, forGender: Boolean) {
    if (forGender) {
        setItemsRes(v, R.array.gender)
    }
}

@BindingAdapter("itemsRes", requireAll = false)
fun setItemsRes(v: AppCompatSpinner, @ArrayRes items: Int) {

    val spinnerAdapter = object : ArrayAdapter<String>(
        v.context,
        R.layout.item_no_default_spinner,
        R.id.textSpinner,
        v.resources.getStringArray(items).toMutableList()
    ) {

//        override fun getCount(): Int {
//            return v.resources.getStringArray(items).toMutableList().size-1
//        }
//        override fun isEnabled(position: Int): Boolean {
//            // Disable the first item from Spinner
//            // First item will be used for hint
//            return position != 0
//        }

        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view: RelativeLayout =
                super.getDropDownView(position, convertView, parent) as RelativeLayout
            //set the color of first item in the drop down list to gray
            if (position == 3) {
                view.setBackgroundColor(Color.TRANSPARENT)
                view.findViewById<TextView>(R.id.textSpinner).setBackgroundColor(Color.TRANSPARENT)
                view.findViewById<TextView>(R.id.textSpinner).text = ""
//                view.visibility = View.GONE
            } else {
                //here it is possible to define color for other items by
                //view.setTextColor(Color.RED)
//                view.visibility = View.VISIBLE
            }
            return view
        }

    }
    val adapter = ArrayAdapter(
        v.context,
        R.layout.item_no_default_spinner,
        R.id.textSpinner,
        v.resources.getStringArray(items).toMutableList()
    )
    spinnerAdapter.setDropDownViewResource(R.layout.item_no_default_spinner_dropdown_menu)
//    adapter.count = v.resources.getStringArray(items).toMutableList().size-1

    v.adapter = spinnerAdapter
    v.post(Runnable { v.setSelection(3) })
    v.setSelection(3)

}

@BindingAdapter("leftValue", "rightValue", requireAll = false)
fun setValues(
    v: com.i69.ui.views.rangeseekbar.RangeSeekBar,
    setLeftValue: Float,
    setRightValue: Float
) {
    v.setValue(setLeftValue, setRightValue)
}

@BindingAdapter("android:onClick", requireAll = false)
fun onClick(v: InterestsView, list: View.OnClickListener) {
    v.setOnAddButtonClickListener { list.onClick(v) }
}

@BindingAdapter(value = ["leftValueAttrChanged", "rightValueAttrChanged"], requireAll = false)
fun setValuesAdapterLeft(
    v: com.i69.ui.views.rangeseekbar.RangeSeekBar,
    setLeftValueAttrChanged: InverseBindingListener?,
    setRightValueAttrChanged: InverseBindingListener?
) {
    v.setOnRangeChangedListener(object : com.i69.ui.views.rangeseekbar.OnRangeChangedListener {
        override fun onRangeChanged(
            view: com.i69.ui.views.rangeseekbar.RangeSeekBar?,
            leftValue: Float,
            rightValue: Float,
            isFromUser: Boolean
        ) {
            setLeftValueAttrChanged?.onChange()
            setRightValueAttrChanged?.onChange()
        }

        override fun onStartTrackingTouch(
            view: com.i69.ui.views.rangeseekbar.RangeSeekBar?,
            isLeft: Boolean
        ) {
        }

        override fun onStopTrackingTouch(
            view: com.i69.ui.views.rangeseekbar.RangeSeekBar?,
            isLeft: Boolean
        ) {
        }
    })
}

@InverseBindingAdapter(attribute = "leftValue", event = "leftValueAttrChanged")
fun getValuesAdapterLeft(v: com.i69.ui.views.rangeseekbar.RangeSeekBar): Float {
    return v.leftValue
}

@InverseBindingAdapter(attribute = "rightValue")
fun getValuesAdapterRight(v: com.i69.ui.views.rangeseekbar.RangeSeekBar): Float {
    return v.rightValue
}

@BindingAdapter(value = ["singleValue", "singleValueAttrChanged"], requireAll = false)
fun singleValue(
    v: com.i69.ui.views.rangeseekbar.RangeSeekBar,
    value: Float?,
    singleValueAttrChanged: InverseBindingListener?
) {
    v.setOnRangeChangedListener(object : com.i69.ui.views.rangeseekbar.OnRangeChangedListener {
        override fun onRangeChanged(
            view: com.i69.ui.views.rangeseekbar.RangeSeekBar?,
            leftValue: Float,
            rightValue: Float,
            isFromUser: Boolean
        ) {
            singleValueAttrChanged?.onChange()
        }

        override fun onStartTrackingTouch(
            view: com.i69.ui.views.rangeseekbar.RangeSeekBar?,
            isLeft: Boolean
        ) {
        }

        override fun onStopTrackingTouch(
            view: com.i69.ui.views.rangeseekbar.RangeSeekBar?,
            isLeft: Boolean
        ) {
        }
    })
    if (value != null && value >= 0) v.setValue(value)
}

@InverseBindingAdapter(attribute = "singleValue", event = "singleValueAttrChanged")
fun getSingleValueAdapter(v: com.i69.ui.views.rangeseekbar.RangeSeekBar): Float {
    return v.leftValue
}

@BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
fun bindSpinnerData(
    pAppCompatSpinner: AppCompatSpinner,
    newSelectedValue: String?,
    newTextAttrChanged: InverseBindingListener
) {
    pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            newTextAttrChanged.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
    if (newSelectedValue != null && pAppCompatSpinner.adapter != null) {
        val pos = (pAppCompatSpinner.adapter as ArrayAdapter<String>).getPosition(newSelectedValue)
        pAppCompatSpinner.setSelection(pos, true)
    }
}


@BindingAdapter(
    value = ["genderSpinnerSelectedValue", "defaultPicker", "spinnerSelectedValueListener"],
    requireAll = false
)
fun genderSpinnerSelectedValue(
    v: AppCompatSpinner,
    pos: Int?,
    defaultPickerItems: List<IdWithValue>?,
    textAttrChanged: InverseBindingListener?
) {
    v.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            textAttrChanged?.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }



    if (defaultPickerItems.isNullOrEmpty()) {
        if (pos != null && pos >= 0) {
            v.setSelection(pos, true)
        }
    } else {
        var selectedPos: Int = -1
        defaultPickerItems.forEachIndexed { index, idWithValue ->
            if (idWithValue.id == pos) {
                selectedPos = pos
                return@forEachIndexed
            }
        }

        if (selectedPos != -1 && selectedPos != 0) {
            v.setSelection((selectedPos - 1), true)
        }
    }
}


@BindingAdapter(
    value = ["spinnerSelectedValue", "defaultPicker", "spinnerSelectedValueListener"],
    requireAll = false
)
fun spinnerSelectedValue(
    v: AppCompatSpinner,
    pos: Int?,
    defaultPickerItems: List<IdWithValue>?,
    textAttrChanged: InverseBindingListener?
) {
    v.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            textAttrChanged?.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }


    if (defaultPickerItems.isNullOrEmpty()) {
        if (pos != null && pos >= 0) {
            v.setSelection(pos, true)
        }
    } else {
        var selectedPos: Int = -1
        defaultPickerItems.forEachIndexed { index, idWithValue ->
            if (idWithValue.id == pos) {
                selectedPos = index
                return@forEachIndexed
            }
        }

        if (selectedPos != -1 && selectedPos != 0) {
            v.setSelection(selectedPos, true)
        }

        if (defaultPickerItems?.size == 3 && selectedPos == 1) {
            v.setSelection(0, true)
        }
    }
}

//@BindingAdapter(value = [ "spinnerSelectedValueNew","defaultPickerNew"], requireAll = false)
//fun spinnerSelectedValueNew(v: AppCompatSpinner, pos: Int?, defaultPickerItems: List<IdWithValue>?, textAttrChanged: InverseBindingListener?) {
//    v.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//            textAttrChanged?.onChange()
//        }
//
//        override fun onNothingSelected(parent: AdapterView<*>) {}
//    }
//    if (defaultPickerItems.isNullOrEmpty()) {
//        if (pos != null && pos >= 0) v.setSelection(pos, false)
//    } else {
//        var selectedPos: Int = 0
//        defaultPickerItems.forEachIndexed { index, idWithValue ->
//            if (idWithValue.id == pos) {
//                selectedPos = index
//                return@forEachIndexed
//            }
//        }
//
//        if (selectedPos != 0) v.setSelection(selectedPos, false)
//    }
//}

//@BindingAdapter(value = ["defaultSelectPicker"])
//fun spinnerSelectedDefaultValue(v: AppCompatSpinner, pos: Int?, defaultPickerItems: List<IdWithValue>?, textAttrChanged: InverseBindingListener?) {
//    v.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//            textAttrChanged?.onChange()
//        }
//
//        override fun onNothingSelected(parent: AdapterView<*>) {}
//    }
//    if (defaultPickerItems.isNullOrEmpty()) {
//        if (pos != null && pos >= 0) v.setSelection(pos, false)
//    } else {
//        var selectedPos: Int = -1
//        defaultPickerItems.forEachIndexed { index, idWithValue ->
//            if (idWithValue.id == pos) {
//                selectedPos = index
//                return@forEachIndexed
//            }
//        }
//
//        if (selectedPos != -1) v.setSelection(selectedPos, false)
//    }
//}

@InverseBindingAdapter(attribute = "spinnerSelectedValue", event = "spinnerSelectedValueListener")
fun getSpinnerSelectedValue(view: AppCompatSpinner): Int {
    return view.selectedItemPosition
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): String {
    return pAppCompatSpinner.selectedItem as String
}

@BindingAdapter("prepareTermsAndConditions")
fun MaterialTextView.prepareTermsAndConditionsText(terms: Boolean) {
//    val htmlText = if (isCurrentLanguageFrench())
//        "Rien ne sera publié sur Facebook ou sur Linkedin. Accepter nos <a href=\"${com.i69app.data.config.Constants.URL_TERMS_AND_CONDITION}\">Termes, conditions</a> et <a href=\"${com.i69app.data.config.Constants.URL_PRIVACY_POLICY}\">la Politique de confidentialité</a>"
//    else
//        "We don\'t post anything to Facebook and LinkedIn. By signing in, you agree with our <a href=\"${com.i69app.data.config.Constants.URL_TERMS_AND_CONDITION}\">Terms, Conditions</a> and <a href=\"${com.i69app.data.config.Constants.URL_PRIVACY_POLICY}\">Privacy Policy</a>"
//    this.text = spannedFromHtml(this.resources.getString(R.string.my_link))


    this.isClickable = true
//
    this.movementMethod = LinkMovementMethod.getInstance()
//    this.text = Html.fromHtml(this.resources.getString(R.string.my_link))
}

@BindingAdapter(value = ["loadImage", "placeholder"], requireAll = false)
fun loadImage(view: ImageView, url: String, drawable: Drawable?) {

    Glide.with(view.context)
        .load("${BuildConfig.BASE_URL}media/${url}")
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        //.placeholder(drawable)
        .into(view)
}

@BindingAdapter(value = ["loadGiftImage", "placeholder"], requireAll = false)
fun loadGiftImage(view: ImageView, url: String, drawable: Drawable?) {
    val requestOptions = RequestOptions().override(93, 93).centerCrop()

    Glide.with(view.context)
        .load("${BuildConfig.BASE_URL}media/${url}")
        .apply(requestOptions)
        //.placeholder(drawable)
        .into(view)
}