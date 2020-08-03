package com.cocosw.optimus

import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.inject
import retrofit2.HttpException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OptimusTest : KoinTest {

    private val mockResponseSupplier: MockResponseSupplier by inject()
    private val optimus: Optimus by inject()
    private val mockWebServer: MockWebServer by inject()

    @get:Rule
    val koinTestRule = KoinTestRule.create { modules(testModule) }

    @Test
    fun defaultResponse() {
        val test = optimus.create(TestApi::class.java)
        assertThat(test.get().blockingGet().id).isEqualTo(1)
    }

    @Test
    fun changeBehavior() {
        val test = optimus.create(TestApi::class.java)
        assertThat(test.get().blockingGet().id).isEqualTo(1)
        mockResponseSupplier.set(TestApi::get, TestMockUser::Another)
        assertThat(test.get().blockingGet().id).isEqualTo(3)
    }

    @Test
    fun unHappy() {
        Assert.assertThrows(HttpException::class.java) {
            val test = optimus.create(TestApi::class.java)
            mockResponseSupplier.set(TestApi::get, TestMockUser::Empty)
            test.get().blockingGet()
        }
    }

    @Test
    fun responseWithParameter() {
        val test = optimus.create(TestApi::class.java)
        assertThat(test.find(123).blockingGet().id).isEqualTo(123)
    }

    @Test
    fun classNotFound() {
        Assert.assertThrows(IllegalStateException::class.java) {
            val test = optimus.create(AnotherApi::class.java)
            test.test()
        }
    }

    @Test
    fun methodNotFound() {
        val optimus = Optimus.Builder(get())
            .retrofit(get())
            .mockGraph(mock, alter(AnotherApi::class.java) {})
            .build()

        Assert.assertThrows(IllegalStateException::class.java) {
            optimus.create(AnotherApi::class.java).test()
        }
    }

    @Test
    fun testByPass() {
        mockWebServer.noClientAuth()
        optimus.bypass = true
        val test = optimus.create(TestApi::class.java).get()
        assertThat(test.blockingGet().id).isEqualTo(999)

        Assert.assertThrows(HttpException::class.java) {
            optimus.create(TestApi::class.java).find(11).blockingGet()
        }
    }

    @Test
    fun testCall() {
        val test = optimus.create(TestApi::class.java)
        mockResponseSupplier.set(TestApi::call, TestMockUser::HTTP403)
        var response = test.call().execute()
        Assert.assertEquals(response.isSuccessful, false)
        Assert.assertEquals(response.code(), 403)
        Assert.assertEquals(response.body(), null)
        Assert.assertEquals(response.errorBody()?.string(), "")
        mockResponseSupplier.set(TestApi::call, TestMockUser::Empty)
        response = test.call().execute()
        Assert.assertEquals(response.isSuccessful, false)
        Assert.assertEquals(response.code(), 400)
        Assert.assertEquals(response.body(), null)
        Assert.assertEquals(
            response.errorBody()?.string(),
            "{\"error\":\"error\",\"statusCode\":400}"
        )
    }
}
