package com.cocosw.optimus

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Spinner
import java.util.concurrent.TimeUnit

class OptimusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private lateinit var listview: ExpandableListView
    private lateinit var optimus: Optimus

    fun setOptimus(optimus: Optimus) {
        this.optimus = optimus
        listview = findViewById(R.id.list)
        listview.setAdapter(OptimusMockAdapter(context, optimus))
        if (optimus.response.size == 1) {
            listview.expandGroup(0)
        }

        val behavior = optimus.networkBehavior

        findViewById<Spinner>(R.id.debug_network_delay).apply {
            val delayAdapter = NetworkDelayAdapter(context)
            adapter = delayAdapter
            setSelection(delayAdapter.getPosition(behavior.networkBehavior.delay(TimeUnit.MILLISECONDS)))

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    behavior.setDelay(delayAdapter.getItem(p2), TimeUnit.MILLISECONDS)
                }
            }
        }

        findViewById<Spinner>(R.id.debug_network_variance).apply {
            val delayAdapter = NetworkVarianceAdapter(context)
            adapter = delayAdapter
            setSelection(delayAdapter.getPositionForValue(behavior.networkBehavior.variancePercent()))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    behavior.setVariancePercent(delayAdapter.getItem(p2))
                }
            }
        }

        findViewById<Spinner>(R.id.debug_network_failure).apply {
            val delayAdapter = NetworkPercentageAdapter(context)
            adapter = delayAdapter
            setSelection(delayAdapter.getPositionForValue(behavior.networkBehavior.failurePercent()))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    behavior.setFailurePercent(delayAdapter.getItem(p2))
                }
            }
        }

        findViewById<Spinner>(R.id.debug_network_error).apply {
            val delayAdapter = NetworkPercentageAdapter(context)
            adapter = delayAdapter
            setSelection(delayAdapter.getPositionForValue(behavior.networkBehavior.errorPercent()))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    behavior.setErrorPercent(delayAdapter.getItem(p2))
                }
            }
        }

        findViewById<Spinner>(R.id.debug_network_error_code).apply {
            if (behavior.getErrorCode() == null)
                this.isEnabled = false
            else {
                val delayAdapter = EnumAdapter(context, NetworkErrorCode::class.java)
                adapter = delayAdapter
                setSelection(
                    NetworkErrorCode.values().indexOfFirst { it.code == behavior.getErrorCode() }
                )
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {}

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        behavior.setErrorCode(
                            delayAdapter.getItem(p2)?.code ?: NetworkErrorCode.HTTP_400.code
                        )
                    }
                }
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.lib_optimus_view, this, true)

        findViewById<RadioGroup>(R.id.group).setOnCheckedChangeListener { _, i ->
            findViewById<View>(R.id.network).visibility =
                if (i == R.id.tab1) View.GONE else View.VISIBLE
            findViewById<View>(R.id.list).visibility =
                if (i == R.id.tab2) View.GONE else View.VISIBLE
        }
    }
}

internal enum class NetworkErrorCode(val code: Int) {
    HTTP_400(400),
    HTTP_401(401),
    HTTP_403(403),
    HTTP_404(404),
    HTTP_429(429),
    HTTP_500(500),
    HTTP_501(501),
    HTTP_503(503),
    HTTP_504(504);

    override fun toString(): String {
        return "HTTP $code"
    }
}
