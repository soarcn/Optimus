package com.cocosw.optimus.app

import android.app.Application
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Startup().init(this)
    }

    internal fun modules(): List<Module> {
        return listOf(
            module {
                single { Retrofit.Builder().build() }
                single { provideApi(get()) }
            }
        )
    }

    private fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }
}