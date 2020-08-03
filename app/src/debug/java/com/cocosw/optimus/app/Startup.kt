package com.cocosw.optimus.app

import android.content.Context.MODE_PRIVATE
import com.cocosw.optimus.Converter
import com.cocosw.optimus.MockResponseSupplier
import com.cocosw.optimus.Optimus
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class Startup {
    fun init(application: App) {
        startKoin {
            androidContext(application)
            modules(
                application.modules() +
                    debugModule()
            )
            androidLogger()
        }
    }

    fun debugModule(): Module {
        return module(override = true) {
            single {
                MockResponseSupplier.create(
                    androidApplication().getSharedPreferences(
                        "demo",
                        MODE_PRIVATE
                    )
                )
            }
            single {
                val moshi: Moshi = get()
                Optimus.Builder(get())
                    .retrofit(
                        get(),
                        androidApplication().getSharedPreferences("demo", MODE_PRIVATE)
                    )
                    .mockGraph(mockApi)
                    .converter(Converter.create(moshi))
                    .build()
            }
            single { Moshi.Builder().build() }
            single { provideMockApi(get()) }
        }
    }

    private fun provideMockApi(optimus: Optimus): Api {
        return optimus.create(Api::class.java)
    }
}
