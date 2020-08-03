package com.cocosw.optimus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseExpandableListAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

internal class OptimusMockAdapter(private val context: Context, private val optimus: Optimus) :
    BaseExpandableListAdapter() {

    var inflater = context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getGroup(p0: Int): Any {
        return optimus.response[p0]
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        p3: ViewGroup?
    ): View {
        val view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, p3, false)
        view.findViewById<TextView>(android.R.id.text1).text = optimus.response[groupPosition].name
        return view
    }

    override fun getChildrenCount(p0: Int): Int {
        return optimus.response[p0].definitions.size
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return optimus.response[p0].definitions[p1]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val view = inflater.inflate(R.layout.optimus_item_layout, p4, false)
        view.findViewById<TextView>(R.id.title).text = optimus.response[p0].definitions[p1].name
        view.findViewById<Spinner>(R.id.spinner).apply {
            val definition = optimus.response[p0].definitions[p1]
            adapter = MockBehaviorAdapter(context, definition)
            setSelection(optimus.supplier.index(definition.kFunction, definition.kClass))

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    optimus.supplier.select(p2, definition.kFunction, definition.kClass)
                }
            }
        }
        return view
    }

    override fun getChildId(gp: Int, cp: Int): Long {
        return (gp * 10000 + cp).toLong()
    }

    override fun getGroupCount(): Int {
        return optimus.response.size
    }
}

internal class MockBehaviorAdapter(context: Context, private val mockDefinition: MockDefinition) :
    BindableAdapter<KProperty1<out MockResponse, Any?>>(context) {

    private var members: List<KProperty1<out MockResponse, Any?>> =
        mockDefinition.kClass.declaredMemberProperties.toList()

    override fun getItem(position: Int): KProperty1<out MockResponse, Any?> {
        return members[position]
    }

    override fun newView(inflater: LayoutInflater, position: Int, container: ViewGroup): View? {
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return members.size
    }

    override fun bindView(item: KProperty1<out MockResponse, Any?>?, position: Int, view: View?) {
        view?.findViewById<TextView>(android.R.id.text1)?.text = item?.name
    }
}
