package com.codeborne.selenide.impl

import com.codeborne.selenide.AssertionMode
import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.Stopwatch
import com.codeborne.selenide.commands.Commands.Companion.instance
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.logevents.ErrorsCollector.Companion.validateAssertionMode
import com.codeborne.selenide.logevents.LogEvent.EventStatus
import com.codeborne.selenide.logevents.SelenideLogger.beginStep
import com.codeborne.selenide.logevents.SelenideLogger.commitStep
import org.openqa.selenium.JavascriptException
import org.openqa.selenium.WebDriverException
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.time.Duration
import java.util.Arrays
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
internal open class SelenideElementProxy(private val webElementSource: WebElementSource) : InvocationHandler {
    private val exceptionWrapper = ExceptionWrapper()
    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val arguments = Arguments(args)
        if (methodsToSkipLogging.contains(method.name)) return instance.execute<Any>(
            proxy,
            webElementSource,
            method.name,
            args
        )!!
        if (isMethodForSoftAssertion(method)) {
            validateAssertionMode(config())
        }
        val timeoutMs = getTimeoutMs(method, arguments)
        val pollingIntervalMs = getPollingIntervalMs(method, arguments)
        val log = beginStep(webElementSource.description(), method.name, *args ?: arrayOfNulls(1))
        return try {
            val result = dispatchAndRetry(timeoutMs, pollingIntervalMs, proxy, method, args)!!
            commitStep(log, EventStatus.PASS)
            result
        } catch (error: Error) {
            val wrappedError: Throwable = UIAssertionError.wrap(driver(), error, timeoutMs)
            commitStep(log, wrappedError)
            continueOrBreak(proxy, method, wrappedError)
        } catch (error: WebDriverException) {
            val wrappedError = UIAssertionError.wrap(driver(), error, timeoutMs)
            commitStep(log, wrappedError)
            continueOrBreak(proxy, method, wrappedError)
        } catch (error: RuntimeException) {
            commitStep(log, error)
            throw error
        } catch (error: IOException) {
            commitStep(log, error)
            throw error
        }
    }

    @Throws(Throwable::class)
    private fun continueOrBreak(proxy: Any, method: Method, wrappedError: Throwable): Any {
        return if (config().assertionMode() === AssertionMode.SOFT && isMethodForSoftAssertion(method)) proxy else throw wrappedError
    }

    private fun isMethodForSoftAssertion(method: Method): Boolean {
        return methodsForSoftAssertion.contains(method.name)
    }

    private fun driver(): Driver {
        return webElementSource.driver()
    }

    private fun config(): Config {
        return driver().config()
    }

    @Throws(Throwable::class)
    protected fun dispatchAndRetry(
      timeoutMs: Long, pollingIntervalMs: Long,
      proxy: Any, method: Method, args: Array<out Any>?
    ): Any? {
        val stopwatch = Stopwatch(timeoutMs)
        lateinit var lastError: Throwable
        do {
            lastError = try {
                return if (isSelenideElementMethod(method)) {
                    instance.execute<Any>(proxy, webElementSource, method.name, args)
                } else {
                  method.invoke(webElementSource.webElement, *args ?: arrayOfNulls(1))
                }
            } catch (e: InvocationTargetException) {
                e.targetException
            } catch (e: WebDriverException) {
                e
            } catch (e: IndexOutOfBoundsException) {
                e
            } catch (e: AssertionError) {
                e
            }
            if (Cleanup.of.isInvalidSelectorError(lastError)) {
                throw Cleanup.of.wrap(lastError)
            } else if (!shouldRetryAfterError(lastError)) {
                throw lastError
            }
            stopwatch.sleep(pollingIntervalMs)
        } while (!stopwatch.isTimeoutReached)
        throw exceptionWrapper.wrap(lastError, webElementSource)
    }

    @CheckReturnValue
    private fun getTimeoutMs(method: Method, arguments: Arguments): Long {
        val duration = arguments.ofType(Duration::class.java)
        return duration.map { obj: Duration -> obj.toMillis() }
            .orElseGet { if (isWaitCommand(method)) arguments.nth(1) else config().timeout() }
    }

    @CheckReturnValue
    private fun getPollingIntervalMs(method: Method, arguments: Arguments): Long {
        return if (isWaitCommand(method) && arguments.length() == 3) arguments.nth(2) else config().pollingInterval()
    }

    @CheckReturnValue
    private fun isWaitCommand(method: Method): Boolean {
        return "waitUntil" == method.name || "waitWhile" == method.name
    }

    companion object {
        private val methodsToSkipLogging: Set<String> = HashSet(
            Arrays.asList(
                "as",
                "toWebElement",
                "toString",
                "getSearchCriteria",
                "$",
                "\$x",
                "find",
                "$$",
                "$\$x",
                "findAll",
                "parent",
                "sibling",
                "preceding",
                "lastChild",
                "closest"
            )
        )
        private val methodsForSoftAssertion: Set<String> = HashSet(
            Arrays.asList(
                "should",
                "shouldBe",
                "shouldHave",
                "shouldNot",
                "shouldNotHave",
                "shouldNotBe",
                "waitUntil",
                "waitWhile"
            )
        )

        @JvmStatic
        @CheckReturnValue
        fun isSelenideElementMethod(method: Method): Boolean {
            return SelenideElement::class.java.isAssignableFrom(method.declaringClass)
        }

        @JvmStatic
        @CheckReturnValue
        fun shouldRetryAfterError(e: Throwable?): Boolean {
          return when (e) {
              is FileNotFoundException -> false
              is IllegalArgumentException -> false
              is ReflectiveOperationException -> false
              is JavascriptException -> false
              else -> e is Exception || e is AssertionError
          }
        }
    }
}
