package com.cocosw.optimus

import com.squareup.moshi.Moshi
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

internal val testModule = module {
    single<MockResponseSupplier> { InMemoryMockResponseSupplier() }
    single { Moshi.Builder().build() }
    single {
        Retrofit.Builder().baseUrl("http://127.0.0.1:8080/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()
    }
    single { mockwebserver(get()) }
    factory {
        provideOptimus(get(), get(), get())
    }
}

private fun provideOptimus(
    supplier: MockResponseSupplier,
    retrofit: Retrofit,
    moshi: Moshi
): Optimus {
    return Optimus.Builder(supplier)
        .retrofit(retrofit)
        .mockGraph(mock)
        .converter(Converter.create(moshi))
        .build()
}

internal val mock = alter(TestApi::class.java, "TestApi") {
    TestApi::get with TestMockUser::class named "Get user"
    TestApi::find with TestFindUser::class named "Find user"
    TestApi::post with TestMockUserList::class named "Get User List"
}

private fun mockwebserver(moshi: Moshi): MockWebServer {
    val server = MockWebServer()

    server.dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            when (request.path) {
                "/get" -> return MockResponse().setResponseCode(200).setBody(
                    moshi.adapter(User::class.java).toJson(
                        User(999, "test", "test", "test", "test")
                    )
                )
            }
            return MockResponse().setResponseCode(404)
        }
    }
    server.start(8080)
    return server
}
