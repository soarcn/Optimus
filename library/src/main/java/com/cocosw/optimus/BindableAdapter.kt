package com.cocosw.optimus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/** An implementation of [BaseAdapter] which uses the new/bind pattern for its views.  */
abstract internal class BindableAdapter<T>(val context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    abstract override fun getItem(position: Int): T?

    override fun getView(position: Int, v: View?, container: ViewGroup): View {
        var view = v
        if (view == null) {
            view = newView(inflater, position, container)
            if (view == null) {
                throw IllegalStateException("newView next must not be null.")
            }
        }
        bindView(getItem(position), position, view)
        return view
    }

    /** Create a new instance of a view for the specified position.  */
    abstract fun newView(inflater: LayoutInflater, position: Int, container: ViewGroup): View?

    /** Bind the data for the specified `position` to the view.  */
    abstract fun bindView(item: T?, position: Int, view: View?)

    override fun getDropDownView(position: Int, v: View?, container: ViewGroup): View {
        var view = v
        if (view == null) {
            view = newDropDownView(inflater, position, container)
            if (view == null) {
                throw IllegalStateException("newDropDownView next must not be null.")
            }
        }
        bindDropDownView(getItem(position), position, view)
        return view
    }

    /** Create a new instance of a drop-down view for the specified position.  */
    open fun newDropDownView(inflater: LayoutInflater, position: Int, container: ViewGroup): View? {
        return newView(inflater, position, container)
    }

    /** Bind the data for the specified `position` to the drop-down view.  */
    fun bindDropDownView(item: T?, position: Int, view: View?) {
        bindView(item, position, view)
    }
}
