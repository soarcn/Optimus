package com.cocosw.optimus

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Test

import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.Exception

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest : KoinTest {

    private val mock = alter(TestApi::class.java,"TestApi") {
        TestApi::get with TestMockUser::class named "Get user"
        TestApi::post with TestMockUserList::class named "Get User List"
    }
    private val mockResponseSupplier:MockResponseSupplier by inject()
    private val optimus:Optimus by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules( module {
            single<MockResponseSupplier> { InMemoryMockResponseSupplier() }
            single {  }
            single { Retrofit.Builder().baseUrl("http://test")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                .build() }
            single { Optimus.Builder(get())
                .retrofit(get())
                .response(mock)
                .converter(object :Converter {
                    val moshi:Moshi = Moshi.Builder().build()
                    override fun convert(obj: Any): ResponseBody {
                        return ResponseBody.create(MediaType.parse("application/json"),moshi.adapter(obj.javaClass).toJson(obj))
                    }
                })
                .build()
            }
        } )
    }

    @Test
    fun defaultResponse() {
        val test = optimus.create(TestApi::class.java)
        assertThat(test.get().blockingGet().id).isEqualTo(1)
    }

    @Test
    fun changeBehavior() {
        val test = optimus.create(TestApi::class.java)
        assertThat(test.get().blockingGet().id).isEqualTo(1)
        mockResponseSupplier.set(TestApi::get,TestMockUser::Another)
        assertThat(test.get().blockingGet().id).isEqualTo(3)
    }

    @Test
    fun unHappy() {
        val test = optimus.create(TestApi::class.java)
        mockResponseSupplier.set(TestApi::get,TestMockUser::Empty)
        try {
            val result = test.get().blockingGet()
            Assert.fail("Should have thrown HttpException exception");
        } catch (e:Exception) {
            assertThat(e).isInstanceOf(HttpException::class.java)
        }
    }
}