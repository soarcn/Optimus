package com.cocosw.optimus.app

import android.content.Context.MODE_PRIVATE
import com.cocosw.optimus.MockResponseSupplier
import com.cocosw.optimus.Optimus
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.mock.MockRetrofit

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
        return module {
            single { MockResponseSupplier.create(androidApplication().getSharedPreferences("demo",MODE_PRIVATE)) }
            single { Optimus.Builder(get()).retrofit(get(),androidApplication().getSharedPreferences("demo",MODE_PRIVATE)).mockGraph(mockApi).build() }
            single { Moshi.Builder().build() }
        }
    }

    private fun provideMockApi(optimus: Optimus): Api {
        return optimus.create(Api::class.java)
    }

    private fun provideMockRetrofit(retrofit: Retrofit): MockRetrofit {
        return MockRetrofit.Builder(retrofit).build()
    }
}
