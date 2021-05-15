package com.codeborne.selenide.impl

import assertk.all
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelectorMode
import com.codeborne.selenide.SelenideConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class WebElementSelectorTest {
    private val selector = WebElementSelector()
    private val browser = Browser("zopera", false)
    private val webDriver = Mockito.mock(JSWebDriver::class.java)
    private val parent: SearchContext = Mockito.mock(WebElement::class.java)
    @Test
    fun findElement_byCss() = runBlockingTest {
        val config: Config = SelenideConfig().selectorMode(SelectorMode.CSS)
        val driver: Driver = DriverStub(null, config, browser, webDriver, null)
        val div = Mockito.mock(WebElement::class.java)
        Mockito.`when`(webDriver.findElement(By.cssSelector("a.active"))).thenReturn(div)
        Assertions.assertThat<Any>(selector.findElement(driver, webDriver, By.cssSelector("a.active"))).isSameAs(div)
    }

    @Test
    fun findElement_byNonCss() = runBlockingTest {
        val config: Config = SelenideConfig().selectorMode(SelectorMode.Sizzle)
        val driver: Driver = DriverStub(null, config, browser, webDriver, null)
        val div = Mockito.mock(WebElement::class.java)
        Mockito.`when`(webDriver.findElement(By.xpath("/div/h1"))).thenReturn(div)
        Assertions.assertThat<Any>(selector.findElement(driver, webDriver, By.xpath("/div/h1"))).isSameAs(div)
    }

    @Test
    fun findElement_fromRoot_canUseSizzleSelectors() = runBlockingTest {
        val config: Config = SelenideConfig().selectorMode(SelectorMode.Sizzle)
        val driver: Driver = DriverStub(null, config, browser, webDriver, null)
        val div = Mockito.mock(WebElement::class.java)
        Mockito.`when`(webDriver.executeScript("return typeof Sizzle != 'undefined'")).thenReturn(true)
        Mockito.`when`(webDriver.executeScript("return Sizzle(arguments[0])", "a.active:last"))
            .thenReturn(Arrays.asList(div))
        Assertions.assertThat<Any>(selector.findElement(driver, driver.webDriver, By.cssSelector("a.active:last")))
            .isSameAs(div)
    }

    @Test
    fun findElement_insideElement_canUseSizzleSelectors() = runBlockingTest {
        val config: Config = SelenideConfig().selectorMode(SelectorMode.Sizzle)
        val driver: Driver = DriverStub(null, config, browser, webDriver, null)
        val div = Mockito.mock(WebElement::class.java)
        Mockito.`when`(webDriver.executeScript("return typeof Sizzle != 'undefined'")).thenReturn(true)
        Mockito.`when`(webDriver.executeScript("return Sizzle(arguments[0], arguments[1])", "a.active:last", parent))
            .thenReturn(
                Arrays.asList(div)
            )
        Assertions.assertThat<Any>(selector.findElement(driver, parent, By.cssSelector("a.active:last"))).isSameAs(div)
    }

    @Test
    fun findElements_byCss() = runBlockingTest {
        val config: Config = SelenideConfig().selectorMode(SelectorMode.CSS)
        val driver: Driver = DriverStub(null, config, browser, webDriver, null)
        val divs = Arrays.asList(
            Mockito.mock(
                WebElement::class.java
            ), Mockito.mock(WebElement::class.java)
        )
        Mockito.`when`(webDriver.findElements(By.cssSelector("a.active"))).thenReturn(divs)
        Assertions.assertThat<Any>(selector.findElements(driver, webDriver, By.cssSelector("a.active"))).isSameAs(divs)
    }

    @Test
    fun findElements_byNonCss() = runBlockingTest {
        val config: Config = SelenideConfig().selectorMode(SelectorMode.Sizzle)
        val driver: Driver = DriverStub(null, config, browser, webDriver, null)
        val divs = Arrays.asList(
            Mockito.mock(
                WebElement::class.java
            ), Mockito.mock(WebElement::class.java)
        )
        Mockito.`when`(webDriver.findElements(By.xpath("/div/h1"))).thenReturn(divs)
        Assertions.assertThat<Any>(selector.findElements(driver, webDriver, By.xpath("/div/h1"))).isSameAs(divs)
    }

    @Test
    fun findElement_insideElement_cannotUseXpathStartingWithSlash() = runBlockingTest {
        val driver: Driver = DriverStub("zopera")
        assertThat { selector.findElement(driver, parent, By.xpath("/div")) }
            .isFailure()
            .all {
                isInstanceOf(IllegalArgumentException::class.java)
                hasMessage("XPath starting from / searches from root")
            }
    }

    @Test
    fun findElements_insideElement_cannotUseXpathStartingWithSlash() = runBlockingTest {
        val driver: Driver = DriverStub("zopera")
        assertThat { selector.findElements(driver, parent, By.xpath("/div")) }
            .isFailure()
            .all {
                isInstanceOf(IllegalArgumentException::class.java)
                hasMessage("XPath starting from / searches from root")
            }
    }

    internal interface JSWebDriver : WebDriver, JavascriptExecutor
}
