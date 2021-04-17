package com.codeborne.selenide

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import javax.annotation.CheckReturnValue

/**
 *
 *
 * Interface for using custom WebDriver in your tests
 *
 *
 *
 *
 * To customize [WebDriver] creation one can use any of the alternatives:
 *
 *  * Call method [com.codeborne.selenide.WebDriverRunner.setWebDriver] explicitly.
 *  * Extend [WebDriver] implementation, override `public XxxDriver(Capabilities desiredCapabilities)`
 * constructor and pass this class name as `browser` system variable value.
 *  * Implement this very interface and pass the implementation class name as `browser` system variable value.
 *
 */
interface WebDriverProvider {
    /**
     * Create new [WebDriver] instance. The instance will be bound to current thread, so there is no need to cache
     * this instance in method implementation. Also don't cache the instance in static variable, as [WebDriver
 * instance is not thread-safe](http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Is_WebDriver_thread-safe?).
     *
     * @param desiredCapabilities set of desired capabilities as suggested by Selenide framework; method implementation is
     * recommended to pass this variable to [WebDriver], probably modifying it according to specific needs
     * @return new [WebDriver] instance
     */
    @CheckReturnValue
    fun createDriver(desiredCapabilities: DesiredCapabilities): WebDriver
}
