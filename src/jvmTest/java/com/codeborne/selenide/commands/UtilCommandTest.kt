package com.codeborne.selenide.commands

import com.codeborne.selenide.Condition
import com.codeborne.selenide.commands.Util.argsToConditions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import java.util.Arrays
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
internal class UtilCommandTest : WithAssertions {
    @Test
    fun testArgsToCondition() = runBlockingTest {
        val conditions = argsToConditions(
            arrayOf<Any?>(
                Condition.enabled, arrayOf(Condition.appear, Condition.exist),
                "hello",
                2L
            )
        )
        assertThat(conditions)
            .isEqualTo(Arrays.asList(Condition.enabled, Condition.visible, Condition.exist))
    }

    @Test
    fun testArgsToConditionWithIllegalArguments() = runBlockingTest {
        assertThatThrownBy {
            val conditions = argsToConditions(
                arrayOf<Any?>(
                    Condition.enabled, arrayOf(Condition.appear, Condition.exist),
                    1,
                    2.0
                )
            )
            assertThat(conditions)
                .isEqualTo(Arrays.asList(Condition.enabled, Condition.visible, Condition.exist))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
