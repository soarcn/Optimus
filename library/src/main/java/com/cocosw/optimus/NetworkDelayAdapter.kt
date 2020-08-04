package com.cocosw.optimus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

internal class NetworkDelayAdapter(context: Context) : BindableAdapter<Long>(context) {

    private val VALUES = longArrayOf(0, 250, 500, 1000, 2000, 3000, 5000)

    fun getPosition(value: Long): Int {
        for (i in VALUES.indices) {
            if (VALUES[i] == value) {
                return i
            }
        }
        return 3 // Default to 2000 if something changes.
    }

    override fun getCount(): Int {
        return VALUES.size
    }

    override fun getItem(position: Int): Long {
        return VALUES[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun newView(inflater: LayoutInflater, position: Int, container: ViewGroup): View {
        return inflater.inflate(android.R.layout.simple_spinner_item, container, false)
    }

    override fun bindView(item: Long?, position: Int, view: View?) {
        val tv = view!!.findViewById<TextView>(android.R.id.text1)
        tv.text = item.toString() + "ms"
    }

    override fun newDropDownView(
        inflater: LayoutInflater,
        position: Int,
        container: ViewGroup
    ): View {
        return inflater.inflate(android.R.layout.simple_spinner_dropdown_item, container, false)
    }
}
