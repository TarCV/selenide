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
            "Collection matcher error" + System.lineSeparator() +
                    "Expected: foo of elements to match [blah] predicate" + System.lineSeparator() +
                    "Collection: .rows" + System.lineSeparator() +
                    "Elements: [" + System.lineSeparator() +
                    "\t<div displayed:false>mr. %First</div>," + System.lineSeparator() +
                    "\t<div displayed:false>mr. %Second</div>" + System.lineSeparator() +
                    "]" + System.lineSeparator() +
                    "Timeout: 4 s." + System.lineSeparator() +
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
            "Collection matcher error" + System.lineSeparator() +
                    "Expected: foo of elements to match [blah] predicate" + System.lineSeparator() +
                    "Because: I think so" + System.lineSeparator() +
                    "Collection: .rows" + System.lineSeparator() +
                    "Elements: [" + System.lineSeparator() +
                    "\t<div displayed:false>mr. %First</div>," + System.lineSeparator() +
                    "\t<div displayed:false>mr. %Second</div>" + System.lineSeparator() +
                    "]" + System.lineSeparator() +
                    "Timeout: 4 s." + System.lineSeparator() +
                    "Caused by: NoSuchElementException: .third"
        )
    }
}
