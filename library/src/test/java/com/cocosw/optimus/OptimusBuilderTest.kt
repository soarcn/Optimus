package com.cocosw.optimus

import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import retrofit2.mock.MockRetrofit

class OptimusBuilderTest : KoinTest {

    private val builder = Optimus.Builder(InMemoryMockResponseSupplier())

    @get:Rule
    val koinTestRule = KoinTestRule.create { modules(testModule) }

    @Test
    fun requireRetrofit() {
        builder.mockGraph(mock)
        assertThrows("retrofit or mockRetrofit are required.", IllegalStateException::class.java) {
            builder.build()
        }
    }

    @Test
    fun redundantRetrofit() {
        assertThrows(
            "redundant retrofit/mock retrofit instance",
            IllegalStateException::class.java
        ) {
            builder.retrofit(get())
            builder.mockRetrofit(MockRetrofit.Builder(get()).build())
            builder.build()
        }
    }

    @Test
    fun requireResponse() {
        assertThrows("mockGraph is missing", IllegalStateException::class.java) {
            builder.retrofit(get())
            builder.build()
        }
    }
}
