package com.cocosw.optimus.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cocosw.optimus.Optimus
import com.cocosw.optimus.OptimusView
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val opti:Optimus by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<View>(R.id.fab).setOnClickListener {
            AlertDialog.Builder(this)
                .setView(OptimusView(this).apply { this.setOptimus(opti) })
                .create().show()
        }
    }
}
