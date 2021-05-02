package com.codeborne.selenide.impl

import com.codeborne.selenide.AssertionMode
import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.Stopwatch
import com.codeborne.selenide.commands.Commands.Companion.instance
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.logevents.ErrorsCollector.Companion.validateAssertionMode
import com.codeborne.selenide.logevents.LogEvent.EventStatus
import com.codeborne.selenide.logevents.SelenideLogger.beginStep
import com.codeborne.selenide.logevents.SelenideLogger.commitStep
import okio.ExperimentalFileSystem
import okio.FileNotFoundException
import okio.IOException
import org.openqa.selenium.JavascriptException
import org.openqa.selenium.WebDriverException
import support.InvocationHandler
import support.reflect.InvocationTargetException
import support.reflect.ReflectiveOperationException
import support.reflect.invoke
import kotlin.reflect.KFunction
import kotlin.time.Duration

internal open class SelenideElementProxy(private val webElementSource: WebElementSource) : InvocationHandler {
    private val exceptionWrapper = ExceptionWrapper()

    @ExperimentalFileSystem
    @kotlin.time.ExperimentalTime
    override suspend fun invoke(proxy: Any, method: KFunction<*>, args: Array<out Any?>?): Any {
        val args = args as Array<out Any>?
        val arguments = Arguments(args)
        if (methodsToSkipLogging.contains(method.name)) return instance.execute<Any>(
            proxy,
            webElementSource,
            method.name,
            args ?: emptyArray()
        )!!
        if (isMethodForSoftAssertion(method)) {
            validateAssertionMode(config())
        }
        val timeoutMs = getTimeoutMs(method, arguments)
        val pollingIntervalMs = getPollingIntervalMs(method, arguments)
        val log = beginStep(webElementSource.description(), method.name, *args ?: arrayOfNulls(1))
        return try {
            val result = dispatchAndRetry(timeoutMs, pollingIntervalMs, proxy, method, args ?: emptyArray())!!
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

    private fun continueOrBreak(proxy: Any, method: KFunction<*>, wrappedError: Throwable): Any {
        return if (config().assertionMode() === AssertionMode.SOFT && isMethodForSoftAssertion(method)) proxy else throw wrappedError
    }

    private fun isMethodForSoftAssertion(method: KFunction<*>): Boolean {
        return methodsForSoftAssertion.contains(method.name)
    }

    private fun driver(): Driver {
        return webElementSource.driver()
    }

    private fun config(): Config {
        return driver().config()
    }

    @ExperimentalFileSystem
    @kotlin.time.ExperimentalTime
    protected suspend fun dispatchAndRetry(
      timeoutMs: Long, pollingIntervalMs: Long,
      proxy: Any, method: KFunction<*>, args: Array<out Any>
    ): Any? {
        val stopwatch = Stopwatch(timeoutMs)
        lateinit var lastError: Throwable
        do {
            lastError = try {
                return if (isSelenideElementMethod(method)) {
                    instance.execute<Any>(proxy, webElementSource, method.name, args)
                } else {
                  method.invoke(webElementSource.getWebElement(), *args)
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
    @kotlin.time.ExperimentalTime
    private fun getTimeoutMs(method: KFunction<*>, arguments: Arguments): Long {
        val duration = arguments.ofType(Duration::class)
        return duration?.let { obj: Duration -> obj.toLongMilliseconds() }
            ?: run { if (isWaitCommand(method)) arguments.nth(1) else config().timeout() }
    }
    private fun getPollingIntervalMs(method: KFunction<*>, arguments: Arguments): Long {
        return if (isWaitCommand(method) && arguments.length() == 3) arguments.nth(2) else config().pollingInterval()
    }
    private fun isWaitCommand(method: KFunction<*>): Boolean {
        return "waitUntil" == method.name || "waitWhile" == method.name
    }

    companion object {
        private val methodsToSkipLogging: Set<String> = HashSet(
            listOf(
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
            listOf(
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

        fun isSelenideElementMethod(method: KFunction<*>): Boolean {
            return false
// TODO:            return SelenideElement::class.isInstance(method.declaringClass)
        }

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
