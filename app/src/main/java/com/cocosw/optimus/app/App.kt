package com.cocosw.optimus.app

import android.app.Application
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Startup().init(this)
    }

    internal fun modules(): List<Module> {
        return listOf(
            module {
                single {
                    Retrofit.Builder().baseUrl("http://127.0.0.1:8080/")
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(MoshiConverterFactory.create().asLenient())
                        .build()
                }
                single { provideApi(get()) }
            }
        )
    }

    private fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }
}
