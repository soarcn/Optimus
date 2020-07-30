package com.cocosw.optimus.app

import com.cocosw.optimus.Optimus
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
            single { provideMockRetrofit(get()) }
            single { Optimus.Builder(get()).build() }
            single { provideMockApi(get()) }
        }
    }

    private fun provideMockApi(optimus: Optimus): Api {
        return optimus.create(Api::class.java)
    }

    private fun provideMockRetrofit(retrofit: Retrofit): MockRetrofit {
        return MockRetrofit.Builder(retrofit).build()
    }
}
