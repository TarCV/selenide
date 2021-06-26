package com.codeborne.selenide.impl

import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition
import com.codeborne.selenide.logevents.SelenideLogger.removeListener
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.logevents.SelenideLogger.addListener
import com.codeborne.selenide.Condition.Companion.value
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.shouldRetryAfterError
import org.assertj.core.api.WithAssertions
import org.mockito.Mockito
import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.SelenideDriver
import org.junit.jupiter.api.BeforeEach
import java.util.HashMap
import org.mockito.ArgumentMatchers
import org.junit.jupiter.api.AfterEach
import com.codeborne.selenide.logevents.SelenideLogger
import com.codeborne.selenide.logevents.LogEventListener
import org.openqa.selenium.By
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.ElementShould
import com.codeborne.selenide.impl.SelenideElementProxyTest.TestEventListener
import com.codeborne.selenide.logevents.LogEvent.EventStatus
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.SelenideElementProxyTest
import com.codeborne.selenide.impl.SelenideElementProxy
import com.codeborne.selenide.logevents.LogEvent
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.openqa.selenium.InvalidSelectorException
import org.openqa.selenium.JavascriptException
import org.openqa.selenium.NotFoundException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.io.FileNotFoundException
import java.lang.ClassNotFoundException
import java.lang.NoClassDefFoundError
import java.lang.AssertionError
import java.lang.Exception
internal class SelenideElementProxyTest : WithAssertions {
    private val webdriver = Mockito.mock(
        RemoteWebDriver::class.java
    )
    private val element = Mockito.mock(WebElement::class.java)
    private val config = SelenideConfig()
        .screenshots(false)
        .timeout(3)
        .pollingInterval(1)
    private val driver = SelenideDriver(config, webdriver)
    @BeforeEach
    fun mockWebDriver() {
        val map: MutableMap<String, String> = HashMap()
        map["id"] = "id1"
        map["class"] = "class1"
        Mockito.`when`(
            webdriver
                .executeScript(ArgumentMatchers.anyString(), ArgumentMatchers.any(WebElement::class.java))
        )
            .thenReturn(map)
        Mockito.`when`(element.tagName).thenReturn("h1")
        Mockito.`when`(element.text).thenReturn("Hello world")
        Mockito.`when`(element.isDisplayed).thenReturn(true)
    }

    @AfterEach
    fun after() {
        removeListener<LogEventListener>("test")
    }

    @Test
    fun elementShouldBeVisible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(true)
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        driver.find("#firstName").shouldBe(Condition.visible)
    }

    @Test
    fun elementNotFound() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenThrow(NotFoundException())
        assertk.assertThat {
            driver.find("#firstName").shouldBe(Condition.visible)
        }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun elementFoundButNotMatched() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        Mockito.`when`(element.isDisplayed).thenReturn(false)
        assertk.assertThat { driver.find("#firstName").shouldBe(Condition.visible) }
            .isFailure()
            .isInstanceOf(ElementShould::class.java)
    }

    @Test
    fun elementFoundButInvisible() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        Mockito.`when`(element.isDisplayed).thenThrow(WebDriverException("failed to call isDisplayed"))
        assertk.assertThat { driver.find("#firstName").shouldBe(Condition.visible) }
            .isFailure()
            .isInstanceOf(ElementShould::class.java)
    }

    @Test
    fun elementFoundButConditionCheckFailed() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        Mockito.`when`(element.isDisplayed).thenReturn(true)
        assertk.assertThat { driver.find("#firstName").shouldHave(text("goodbye")) }
            .isFailure()
            .isInstanceOf(ElementShould::class.java)
    }

    @Test
    fun elementNotFoundAsExpected() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenThrow(NotFoundException())
        driver.find("#firstName").shouldNotBe(Condition.exist)
        driver.find("#firstName").should(Condition.disappear)
        driver.find("#firstName").shouldNotBe(Condition.visible)
        assertk.assertThat { driver.find("#firstName").shouldNotBe(Condition.enabled) }
            .isFailure()
            .isInstanceOf(
            ElementNotFound::class.java
        )
        assertk.assertThat { driver.find("#firstName").shouldNotHave(text("goodbye")) }
            .isFailure()
            .isInstanceOf(
            ElementNotFound::class.java
        )
    }

    @Test
    fun elementNotFoundAsExpected2() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName")))
            .thenThrow(WebDriverException("element is not found and this is expected"))
        driver.find("#firstName").shouldNot(Condition.exist)
        driver.find("#firstName").should(Condition.disappear)
        driver.find("#firstName").shouldNotBe(Condition.visible)
        assertk.assertThat { driver.find("#firstName").shouldNotBe(Condition.enabled) }
            .isFailure()
            .isInstanceOf(
            ElementNotFound::class.java
        )
        assertk.assertThat { driver.find("#firstName").shouldNotHave(text("goodbye")) }
            .isFailure()
            .isInstanceOf(
            ElementNotFound::class.java
        )
    }

    @Test
    fun webdriverReportsInvalidXpath_using_should() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName")))
            .thenThrow(InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"))
        assertk.assertThat { driver.find("#firstName").should(Condition.disappear) }
            .isFailure()
            .isInstanceOf(InvalidSelectorException::class.java)
    }

    @Test
    fun webdriverReportsInvalidXpath_using_shouldNot() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName")))
            .thenThrow(InvalidSelectorException("Error INVALID_EXPRESSION_ERR ups"))
        assertk.assertThat { driver.find("#firstName").shouldNot(Condition.exist) }
            .isFailure()
            .isInstanceOf(InvalidSelectorException::class.java)
    }

    @Test
    fun setValueShouldNotFailIfElementHasDisappearedWhileEnteringText() = runBlockingTest {
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        Mockito.`when`(webdriver.executeScript(ArgumentMatchers.anyString(), *ArgumentMatchers.any()))
            .thenThrow(StaleElementReferenceException("element disappeared after entering text"))
        driver.find("#firstName").setValue("john")
    }

    @Test
    fun shouldLogSetValueSubject() = runBlockingTest {
        val selector = "#firstName"
        addListener("test", TestEventListener(selector, "set value", EventStatus.PASS))
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        val selEl = driver.find("#firstName")
        selEl.setValue("ABC")
    }

    private inner class TestEventListener(
        private val expectSelector: String,
        private val expectSubject: String,
        private val expectStatus: EventStatus
    ) : LogEventListener {
        override fun afterEvent(logEvent: LogEvent) {
            val format = String.format("{%s} %s: %s", logEvent.element, logEvent.subject, logEvent.status)
            log.info(format)
            assertThat(logEvent.element)
                .contains(expectSelector)
            assertThat(logEvent.subject)
                .contains(expectSubject)
            assertThat(logEvent.status)
                .isEqualTo(expectStatus)
        }

        override fun beforeEvent(logEvent: LogEvent) {}
    }

    @Test
    fun shouldLogShouldSubject() = runBlockingTest {
        val selector = "#firstName"
        addListener("test", TestEventListener(selector, "should have", EventStatus.PASS))
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        Mockito.`when`(element.getAttribute("value")).thenReturn("ABC")
        val selEl = driver.find("#firstName")
        selEl.shouldHave(value("ABC"))
    }

    @Test
    fun shouldLogShouldNotSubject() = runBlockingTest {
        val selector = "#firstName"
        addListener("test", TestEventListener(selector, "should not have", EventStatus.PASS))
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        Mockito.`when`(element.getAttribute("value")).thenReturn("wrong value")
        val selEl = driver.find("#firstName")
        selEl.shouldNotHave(value("ABC"))
    }

    @Test
    fun shouldLogFailedShouldNotSubject() = runBlockingTest {
        val selector = "#firstName"
        addListener("test", TestEventListener(selector, "should have", EventStatus.FAIL))
        Mockito.`when`(webdriver.findElement(By.cssSelector("#firstName"))).thenReturn(element)
        Mockito.`when`(element.getAttribute("value")).thenReturn("wrong value")
        assertk.assertThat { driver.find("#firstName").shouldHave(value("ABC")) }
            .isFailure()
            .isInstanceOf(ElementShould::class.java)
    }

    @Test
    fun shouldNotRetry_onIllegalArgumentException() = runBlockingTest {
        assertThat(shouldRetryAfterError(IllegalArgumentException("The element does not have href attribute")))
            .isFalse
    }

    @Test
    fun shouldNotRetry_onFileNotFoundException() = runBlockingTest {
        assertThat(shouldRetryAfterError(FileNotFoundException("bla")))
            .isFalse
    }

    @Test
    fun shouldNotRetry_onClassLoadingException() = runBlockingTest {
        assertThat(shouldRetryAfterError(ClassNotFoundException("bla")))
            .isFalse
    }

    @Test
    fun shouldNotRetry_onClassDefLoadingException() = runBlockingTest {
        assertThat(shouldRetryAfterError(NoClassDefFoundError("bla")))
            .isFalse
    }

    @Test
    fun shouldNotRetry_onJavaScriptException() = runBlockingTest {
        assertThat(shouldRetryAfterError(JavascriptException("bla")))
            .isFalse
    }

    @Test
    fun shouldRetry_onAssertionError() = runBlockingTest {
        assertThat(shouldRetryAfterError(AssertionError("bla")))
            .isTrue
    }

    @Test
    fun shouldRetry_onAnyOtherException() = runBlockingTest {
        assertThat(shouldRetryAfterError(Exception("bla")))
            .isTrue
    } /* TODO:  @Test
  void detectsIfMethodsBelongsToWebElementOrSelenideElement() throws NoSuchMethodException {
    assertThat(isSelenideElementMethod(SelenideElement.class.getMethod("click"))).isTrue();
    assertThat(isSelenideElementMethod(SelenideElement.class.getMethod("findAll", String.class))).isTrue();

    assertThat(isSelenideElementMethod(WebElement.class.getMethod("click"))).isFalse();
    assertThat(isSelenideElementMethod(WebElement.class.getMethod("findElements", By.class))).isFalse();
  }*/

    companion object {
        private val log = LoggerFactory.getLogger(SelenideElementProxyTest::class.java)
    }
}
