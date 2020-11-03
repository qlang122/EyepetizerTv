package com.qlang.eyepetizer.base

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Created by qlang on 2018/6/16.
 */
abstract class BaseAdapter<T>(var context: Context) : RecyclerView.Adapter<ViewHolder>() {
    protected open val view by lazy { View(context) }
    protected open val linearLayout by lazy { LinearLayout(context) }
    protected open val imageView by lazy { ImageView(context) }
    protected open val textView by lazy { TextView(context) }
    protected open val checkBox by lazy { CheckBox(context) }

    protected var listener: ((Int, View, T?) -> Unit)? = null
    protected var focusListener: ((Int, View, Boolean, T?) -> Unit)? = null
    protected var longListener: ((Int, View, T?) -> Unit)? = null

    fun setOnItemClickListener(listener: (position: Int, view: View, t: T?) -> Unit) {
        this.listener = listener
    }

    fun setOnItemFocusListener(listener: (position: Int, view: View, hasFocus: Boolean, t: T?) -> Unit) {
        this.focusListener = listener
    }

    fun setOnItemLongClickListener(listener: (position: Int, view: View, t: T?) -> Unit) {
        this.longListener = listener
    }

    abstract fun getLayoutResource(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(getLayoutResource(), parent, false))
    }
}

fun <T : View> View.findViewOften(id: Int): T? {
    val viewHolder: SparseArray<T> = tag as? SparseArray<T> ?: SparseArray()
    tag = viewHolder
    var childView: T? = viewHolder.get(id)
    if (null == childView) {
        childView = findViewById(id)
        viewHolder.put(id, childView)
    }
    return childView
}

open class ViewHolder(private val convertView: View) : RecyclerView.ViewHolder(convertView) {
    fun <T : View> findView(id: Int, t: T): T? = convertView.findViewOften(id)
}