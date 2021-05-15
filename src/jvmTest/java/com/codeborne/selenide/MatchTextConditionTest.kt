package com.codeborne.selenide

import com.codeborne.selenide.Condition.Companion.matchText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class MatchTextConditionTest : WithAssertions {
    private val driver = Mockito.mock(Driver::class.java)
    @Test
    fun displaysHumanReadableName() {
        assertThat(matchText("abc"))
            .hasToString("match text 'abc'")
    }

    @Test
    fun matchesWholeString() = runBlockingTest {
        assertThat(
            matchText("Chuck Norris' gmail account is gmail@chuck.norris")
                .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris"))
        )
            .isTrue()
        assertThat(
            matchText("Chuck Norris.* gmail\\s+account is gmail@chuck.norris")
                .apply(driver, element("Chuck Norris' gmail    account is gmail@chuck.norris"))
        )
            .isTrue()
    }

    private fun element(text: String): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.text).thenReturn(text)
        return element
    }

    @Test
    fun matchesSubstring() = runBlockingTest {
        assertThat(
            matchText("Chuck")
                .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris"))
        )
            .isTrue()
        assertThat(
            matchText("Chuck\\s*Norris")
                .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris"))
        )
            .isTrue()
        assertThat(
            matchText("gmail account")
                .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris"))
        )
            .isTrue()
    }
}
