package com.cocosw.optimus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

internal class NetworkPercentageAdapter(context: Context) : BindableAdapter<Int>(context) {

    private val VALUES = intArrayOf(0, 1, 3, 10, 25, 50, 75, 100)

    fun getPositionForValue(value: Int): Int {
        for (i in VALUES.indices) {
            if (VALUES[i] == value) {
                return i
            }
        }
        return 1 // Default to 3% if something changes.
    }

    override fun getCount(): Int {
        return VALUES.size
    }

    override fun getItem(position: Int): Int {
        return VALUES[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun newView(inflater: LayoutInflater, position: Int, container: ViewGroup): View {
        return inflater.inflate(android.R.layout.simple_spinner_item, container, false)
    }

    override fun bindView(item: Int?, position: Int, view: View?) {
        val tv = view!!.findViewById<TextView>(android.R.id.text1)
        if (item == 0) {
            tv.text = "None"
        } else {
            tv.text = item.toString() + "%"
        }
    }

    override fun newDropDownView(
        inflater: LayoutInflater,
        position: Int,
        container: ViewGroup
    ): View {
        return inflater.inflate(android.R.layout.simple_spinner_dropdown_item, container, false)
    }
}
