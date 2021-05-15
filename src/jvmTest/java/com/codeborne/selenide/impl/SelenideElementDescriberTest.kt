package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementShould
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.doThrow
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.UnsupportedCommandException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class SelenideElementDescriberTest : WithAssertions {
    private val describe = SelenideElementDescriber()
    @Test
    fun selectorIsReportedAsIs() {
        assertThat(describe.selector(By.id("firstName"))).isEqualTo("By.id: firstName")
        assertThat(describe.selector(By.className("bootstrap-active"))).isEqualTo("By.className: bootstrap-active")
        assertThat(describe.selector(By.name("firstName"))).isEqualTo("By.name: firstName")
        assertThat(describe.selector(By.linkText("tere"))).isEqualTo("By.linkText: tere")
        assertThat(describe.selector(By.partialLinkText("tere"))).isEqualTo("By.partialLinkText: tere")
        assertThat(describe.selector(By.tagName("tere"))).isEqualTo("By.tagName: tere")
        assertThat(describe.selector(By.xpath("tere"))).isEqualTo("By.xpath: tere")
    }

    @Test
    fun cssSelectorIsShortened() {
        assertThat(describe.selector(By.cssSelector("#firstName"))).isEqualTo("#firstName")
    }

    @Test
    fun shortlyForSelenideElementShouldDelegateToOriginalWebElement() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val webElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`(webElement.tagName).thenThrow(StaleElementReferenceException("disappeared"))
        val selenideElement = Mockito.mock(SelenideElement::class.java)
        Mockito.`when`<Any>(selenideElement.toWebElement()).thenReturn(webElement)
        doThrow(ElementShould.ElementShould(driver, null, null, Condition.visible, webElement, null)).`when`<SelenideElement>(
            selenideElement
        ).getTagName()
        assertThat<Any>(describe.briefly(driver, selenideElement))
            .isEqualTo("Ups, failed to described the element [caused by: StaleElementReferenceException: disappeared]")
    }

    @Test
    fun describe() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "class", "active")
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo("<h1 class=\"active\">Hello yo</h1>")
    }

    @Test
    fun describe_attribute_with_empty_value() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("input", "readonly", "")
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo("<input readonly>Hello yo</input>")
    }

    @Test
    fun describe_elementHasDisappeared() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("input", "readonly", "")
        Mockito.doThrow(StaleElementReferenceException("Booo")).`when`(selenideElement).tagName
        assertThat<Any>(describe.fully(driver, selenideElement))
            .isEqualTo("Ups, failed to described the element [caused by: StaleElementReferenceException: Booo]")
    }

    @Test
    fun describe_collectionHasResized() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("input", "readonly", "")
        Mockito.doThrow(IndexOutOfBoundsException("Fooo")).`when`(selenideElement).tagName
        assertThat<Any>(describe.fully(driver, selenideElement))
            .isEqualTo("Ups, failed to described the element [caused by: java.lang.IndexOutOfBoundsException: Fooo]")
    }

    @Test
    fun describe_appium_NoSuchElementException() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "name", "theName")
        Mockito.`when`(selenideElement.getAttribute("class"))
            .thenThrow(NoSuchElementException("Appium throws exception for missing attributes"))
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>")
    }

    @Test
    fun describe_appium_UnsupportedOperationException() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "name", "theName")
        Mockito.`when`(selenideElement.getAttribute("disabled")).thenThrow(
            UnsupportedOperationException(
                "io.appium.uiautomator2.common.exceptions.NoAttributeFoundException: 'disabled' attribute is unknown for the element"
            )
        )
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>")
    }

    @Test
    fun describe_appium_UnsupportedCommandException() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "name", "theName")
        Mockito.`when`(selenideElement.getAttribute("disabled")).thenThrow(
            UnsupportedCommandException(
                "io.appium.uiautomator2.common.exceptions.NoAttributeFoundException: 'disabled' attribute is unknown for the element"
            )
        )
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"theName\">Hello yo</h1>")
    }

    @Test
    fun describe_appium_isSelected_UnsupportedOperationException() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "name", "fname")
        Mockito.`when`(selenideElement.isSelected)
            .thenThrow(UnsupportedOperationException("isSelected doesn't work in iOS"))
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"fname\">Hello yo</h1>")
    }

    @Test
    fun describe_appium_isSelected_WebDriverException() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "name", "fname")
        Mockito.`when`(selenideElement.isSelected)
            .thenThrow(WebDriverException("isSelected might fail on stolen element"))
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo("<h1 name=\"fname\">Hello yo</h1>")
    }

    @Test
    fun describe_appium_isDisplayed_UnsupportedOperationException() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "name", "fname")
        Mockito.`when`(selenideElement.isDisplayed).thenThrow(UnsupportedOperationException("it happens"))
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo(
            "<h1 name=\"fname\" displayed:java.lang.UnsupportedOperationException: it happens>Hello yo</h1>"
        )
    }

    @Test
    fun describe_appium_isDisplayed_WebDriverException() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val selenideElement = element("h1", "name", "fname")
        Mockito.`when`(selenideElement.isDisplayed)
            .thenThrow(WebDriverException("isDisplayed might fail on stolen element"))
        assertThat<Any>(describe.fully(driver, selenideElement)).isEqualTo(
            "<h1 name=\"fname\" displayed:WebDriverException: isDisplayed might fail on stolen element>Hello yo</h1>"
        )
    }

    private fun element(tagName: String, attributeName: String, attributeValue: String): SelenideElement {
        val selenideElement = Mockito.mock(SelenideElement::class.java)
        Mockito.`when`(selenideElement.tagName).thenReturn(tagName)
        Mockito.`when`(selenideElement.text).thenReturn("Hello yo")
        Mockito.`when`(selenideElement.isDisplayed).thenReturn(true)
        Mockito.`when`(selenideElement.getAttribute(attributeName)).thenReturn(attributeValue)
        return selenideElement
    }
}
