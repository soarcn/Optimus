package com.cocosw.optimus

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MockDefinitionTest {

    @Test
    fun testInstance() {
        assertThat(mock.name).isEqualTo("TestApi")
        assertThat(mock.definitions).hasSize(3)
        assertThat(mock.definitions[0].name).isEqualTo("Get user")
        assertThat(mock.definitions[1].name).isEqualTo("Find user")
        assertThat(mock.definitions[2].name).isEqualTo("Get User List")
    }

    @Test
    fun testDefaultName() {
        val noname = alter(TestApi::class.java) {
            TestApi::new with TestMockUser::class
        }

        assertThat(noname.name).isEqualTo(TestApi::class.java.simpleName)
        assertThat(noname.definitions).hasSize(1)
        assertThat(noname.definitions[0].name).isEqualTo(TestApi::new.name)
    }
}
