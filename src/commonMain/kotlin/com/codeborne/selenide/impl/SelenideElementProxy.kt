package com.codeborne.selenide.impl

import com.codeborne.selenide.AssertionMode
import com.codeborne.selenide.Command
import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.Stopwatch
import com.codeborne.selenide.commands.Commands
import com.codeborne.selenide.commands.Commands.Companion.instance
import com.codeborne.selenide.commands.SoftAssertionCommand
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
import java.lang.reflect.InvocationTargetException
import java.lang.ReflectiveOperationException
import support.reflect.invokeAsync
import support.reflect.isDeclaringClassAssignableTo
import kotlin.jvm.JvmStatic
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty1
import kotlin.time.Duration

class SelenideElementProxy(protected val webElementSource: WebElementSource) {
    private val exceptionWrapper = ExceptionWrapper()

    @ExperimentalFileSystem
    @kotlin.time.ExperimentalTime
    suspend fun <T> selenideElementInvoke(
        proxy: SelenideElement,
        commandProperty: KProperty1<Commands, Command<T>>,
        args: Array<out Any?>
    ): T {
        val args2 = args as Array<out Any>
        val arguments = Arguments(args2)
        val command: Command<T> = commandProperty.get(instance)

        if (methodsToSkipLogging.contains(commandProperty.name)) {
            return command.execute(
                proxy, webElementSource,
                args2 ?: emptyArray()
            )
        }

        if (command is SoftAssertionCommand) {
            validateAssertionMode(config())
        }
        val timeoutMs = getTimeoutMs(commandProperty.name, arguments)
        val pollingIntervalMs = getPollingIntervalMs(commandProperty.name, arguments)
        val log = beginStep(webElementSource.description(), commandProperty.name, *args2 ?: arrayOfNulls(1))
        return try {
            val result =
                dispatchAndRetry(timeoutMs, pollingIntervalMs) {
                    command.execute(
                        proxy, webElementSource,
                        args2 ?: emptyArray()
                    )
                }
            commitStep(log, EventStatus.PASS)
            result
        } catch (error: Error) {
            val wrappedError: Throwable = UIAssertionError.wrap(driver(), error, timeoutMs)
            commitStep(log, wrappedError)
            continueOrBreak(proxy, command, wrappedError)
        } catch (error: org.openqa.selenium.WebDriverException) {
            val wrappedError = UIAssertionError.wrap(driver(), error, timeoutMs)
            commitStep(log, wrappedError)
            continueOrBreak(proxy, command, wrappedError)
        } catch (error: RuntimeException) {
            commitStep(log, error)
            throw error
        } catch (error: IOException) {
            commitStep(log, error)
            throw error
        }
    }

    @ExperimentalFileSystem
    @kotlin.time.ExperimentalTime
    suspend fun <T> webElementInvoke(
        proxy: Any,
        command: KCallable<T>,
        args: Array<out Any?>
    ): T {
        val args2 = args as Array<out Any>
        val arguments = Arguments(args2)

        val timeoutMs = getTimeoutMs(command.name, arguments)
        val pollingIntervalMs = getPollingIntervalMs(command.name, arguments)
        val log = beginStep(webElementSource.description(), command.name, *args2 ?: arrayOfNulls(1))
        return try {
            val result =
                dispatchAndRetry(timeoutMs, pollingIntervalMs) {
                    command.invokeAsync(
                        webElementSource.getWebElement(),
                        *(args2 ?: emptyArray<Any>())
                    )
                }
            commitStep(log, EventStatus.PASS)
            result
        } catch (error: Error) {
            val wrappedError: Throwable = UIAssertionError.wrap(driver(), error, timeoutMs)
            commitStep(log, wrappedError)
            throw wrappedError
        } catch (error: org.openqa.selenium.WebDriverException) {
            val wrappedError = UIAssertionError.wrap(driver(), error, timeoutMs)
            commitStep(log, wrappedError)
            throw wrappedError
        } catch (error: RuntimeException) {
            commitStep(log, error)
            throw error
        } catch (error: IOException) {
            commitStep(log, error)
            throw error
        }
    }

    @ExperimentalFileSystem
    @kotlin.time.ExperimentalTime
    fun <T> selenideElementInvokeSync(proxy: Any, method: KProperty1<Commands, CommandSync<T>>, args: Array<out Any?>?): T {
        require(methodsToSkipLogging.contains(method.name))

        val args2 = args as Array<out Any>
        val arguments = Arguments(args2)
        proxy as SelenideElement
        args2 ?: emptyArray<kotlin.Any>()
        return instance.doExecute(
            proxy,
            webElementSource,
            method,
            args2 ?: emptyArray()
        )
    }

    private fun <T> continueOrBreak(proxy: SelenideElement, method: Command<T>, wrappedError: Throwable): T {
        return if (config().assertionMode() == AssertionMode.SOFT && method is SoftAssertionCommand) {
            @Suppress("UNCHECKED_CAST") // SoftAssertionCommand is Command<SelenideElement>
            proxy as T
        } else {
            throw wrappedError
        }
    }

    private fun driver(): Driver {
        return webElementSource.driver()
    }

    private fun config(): Config {
        return driver().config()
    }

    @ExperimentalFileSystem
    @kotlin.time.ExperimentalTime
    private suspend inline fun <T> dispatchAndRetry(
      timeoutMs: Long, pollingIntervalMs: Long, method: () -> T
    ): T {
        val stopwatch = Stopwatch(timeoutMs)
        lateinit var lastError: Throwable
        do {
            lastError = try {
                return method()
            } catch (e: InvocationTargetException) {
                e.targetException
            } catch (e: org.openqa.selenium.WebDriverException) {
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
    private fun getTimeoutMs(method: String, arguments: Arguments): Long {
        val duration = arguments.ofType(Duration::class)
        return duration?.let { obj: Duration -> obj.toLongMilliseconds() }
            ?: run { if (isWaitCommand(method)) arguments.nth(1) else config().timeout() }
    }
    private fun getPollingIntervalMs(method: String, arguments: Arguments): Long {
        return if (isWaitCommand(method) && arguments.length() == 3) arguments.nth(2) else config().pollingInterval()
    }
    @Suppress("UNCHECKED_CAST")
    private fun isWaitCommand(method: String): Boolean {
        return "waitUntil" == method || "waitWhile" == method
    }

    companion object {
        val NO_ARGS = emptyArray<Any>()

        private val methodsToSkipLogging: Set<String> = HashSet(
            listOf(
                "as",
                "toWebElement",
                "toString",
                "describe",
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

        @JvmStatic
        fun isSelenideElementMethod(method: KProperty1<*, *>): Boolean {
            return method.isDeclaringClassAssignableTo(SelenideElement::class)
        }

        @JvmStatic
        fun shouldRetryAfterError(e: Throwable?): Boolean {
          return when (e) {
              is FileNotFoundException -> false
              is IllegalArgumentException -> false
              is ReflectiveOperationException -> false
              is org.openqa.selenium.JavascriptException -> false
              else -> e is Exception || e is AssertionError
          }
        }
    }
}
