package com.cocosw.optimus

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

class MockResponseSupplierTest : KoinTest {

    private val supplier = InMemoryMockResponseSupplier()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            testModule + module {
                single { supplier }
            }
        )
    }

    @Test
    fun requireRetrofit() {
        val definition = mock.definitions[0]
        supplier.select(1,definition.kFunction,definition.kClass)
        assertThat(supplier.index(definition.kFunction,definition.kClass)).isEqualTo(1)
        supplier.select(0,definition.kFunction,definition.kClass)
        assertThat(supplier.index(definition.kFunction,definition.kClass)).isEqualTo(0)
    }
}