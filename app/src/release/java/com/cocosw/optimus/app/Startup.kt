package com.cocosw.optimus.app

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Startup {
    fun init(application: App) {
        startKoin {
            androidContext(application)
        }
    }
}
