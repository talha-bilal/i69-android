package com.i69.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.i69.R

class TagsCloudView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : com.i69.ui.views.chipcloud.ChipCloud(context, attrs) {

    private var chipListener: OnItemClickListener? = null

    fun setTags(labels: List<String>) {
        removeAllViews()
        for ((i, label) in labels.withIndex()) {
            setSingleTag(label, i)
        }
    }

    private fun setSingleTag(label: String?, labelPos: Int) {
        val container = LayoutInflater.from(context).inflate(R.layout.tag, null) as FrameLayout
        container.findViewById<TextView>(R.id.label).text = label
        container.setOnClickListener {
            chipListener?.onClick(label, labelPos)
        }
        addView(container)
    }

    interface OnItemClickListener {
        fun onClick(tag: String?, position: Int)
    }
}