package com.codeborne.selenide.ex

import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.Mocks.mockElement
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import java.util.Arrays

internal class MatcherErrorTest {
    private val actualElements: List<WebElement> = Arrays.asList(mockElement("mr. %First"), mockElement("mr. %Second"))
    private val ex: WebDriverException = NoSuchElementException(".third")
    @Test
    fun message() = runBlockingTest {
        Assertions.assertThat(
            MatcherError.MatcherError(
                "foo",
                "blah",
                null,
                mockCollection(".rows"),
                actualElements,
                ex,
                4000
            ).message
        ).isEqualTo(
            "Collection matcher error\n" +
                    "Expected: foo of elements to match [blah] predicate\n" +
                    "Collection: .rows\n" +
                    "Elements: [\n" +
                    "\t<div displayed:false>mr. %First</div>,\n" +
                    "\t<div displayed:false>mr. %Second</div>\n" +
                    "]\n" +
                    "Timeout: 4 s.\n" +
                    "Caused by: NoSuchElementException: .third"
        )
    }

    @Test
    fun message_whtExplanation() = runBlockingTest {
        Assertions.assertThat(
            MatcherError.MatcherError(
                "foo",
                "blah",
                "I think so",
                mockCollection(".rows"),
                actualElements,
                ex,
                4000
            ).message
        ).isEqualTo(
            "Collection matcher error\n" +
                    "Expected: foo of elements to match [blah] predicate\n" +
                    "Because: I think so\n" +
                    "Collection: .rows\n" +
                    "Elements: [\n" +
                    "\t<div displayed:false>mr. %First</div>,\n" +
                    "\t<div displayed:false>mr. %Second</div>\n" +
                    "]\n" +
                    "Timeout: 4 s.\n" +
                    "Caused by: NoSuchElementException: .third"
        )
    }
}
