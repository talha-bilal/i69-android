package com.i69.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.i69.R
import com.i69.data.models.market.Category

class CategoryAdapter(
    val context: Context, private val categories: MutableList<Category>,
    private val clickExpandCategoryListener: ClickExpandCategoryListener,
    private val childCategoryClickListener: ChildCategoryClickListener,
    private val clickCategoryListener: ClickCategoryListener
) :
    BaseAdapter() {
    private var expandedPosition = -1

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Any = categories[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.category_item, parent, false)

        val item = getItem(position) as Category

        val icon = view.findViewById<AppCompatImageView>(R.id.categoryIcon)
        val categoryName = view.findViewById<TextView>(R.id.categoryName)
        val listChildCategory = view.findViewById<ListView>(R.id.listChildCategory)
        val imgExpand = view.findViewById<AppCompatImageView>(R.id.imgExpand)

//        item.categoryId?.let { icon.setImageResource(it) }
        categoryName.text = item.categoryName
        categoryName.setOnClickListener {
            clickCategoryListener.onCategoryClick(item)
        }

        val childCategoryList =
            ChildCategoryAdapter(context, item.childCategory, childCategoryClickListener)
        listChildCategory.adapter = childCategoryList

        if (position == expandedPosition && item.childCategory.isNotEmpty()) {
            setListViewHeightBasedOnChildren(listChildCategory)
            listChildCategory.visibility = View.VISIBLE
        } else listChildCategory.visibility = View.GONE

        if (item.haveChildCategories == false || position == 0) {
            imgExpand.visibility = View.GONE
        } else imgExpand.visibility = View.VISIBLE

        imgExpand.setOnClickListener {
            if (position == expandedPosition) {
                expandedPosition = -1
                notifyDataSetChanged()
            } else {
                if (item.childCategory.isNotEmpty()) {
                    expandedPosition = position
                    notifyDataSetChanged()
                } else {
                    clickExpandCategoryListener.onCategoryExpandClick(
                        position,
                        item.categoryId!!, item
                    )
                }
            }
        }
        return view
    }

    fun addExpandChildList(position: Int, childCategoryList: List<Category>) {
        categories[position].childCategory.clear()
        categories[position].childCategory.addAll(childCategoryList)
        expandedPosition = position
        if (categories[position].childCategory.isEmpty()) {
            categories[position].haveChildCategories = false
            Toast.makeText(context, "No Child Categories", Toast.LENGTH_SHORT).show()
        }
        notifyDataSetChanged()
    }

    interface ClickCategoryListener {
        fun onCategoryClick(category: Category)
    }

    interface ClickExpandCategoryListener {
        fun onCategoryExpandClick(position: Int, categoryId: String, category: Category)
    }

    interface ChildCategoryClickListener {
        fun onChildCategoryClick(childCategory: Category)
    }

    class ChildCategoryAdapter(
        val context: Context,
        private val childCategory: List<Category>,
        private val childCategoryClickListener: ChildCategoryClickListener
    ) :
        BaseAdapter() {
        override fun getCount(): Int = childCategory.size

        override fun getItem(position: Int): Any = childCategory[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = LayoutInflater.from(context)
            val view = convertView ?: inflater.inflate(R.layout.child_category_item, parent, false)

            val item = getItem(position) as Category

            val layChildCategory = view.findViewById<LinearLayoutCompat>(R.id.layChildCategory)
            val childCategoryName = view.findViewById<TextView>(R.id.childCategoryName)
            childCategoryName.text = item.categoryName

            layChildCategory.setOnClickListener {
                childCategoryClickListener.onChildCategoryClick(item)
            }
            return view
        }
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.UNSPECIFIED
            )
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }
}