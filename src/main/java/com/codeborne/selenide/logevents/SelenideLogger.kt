package com.codeborne.selenide.logevents

import com.codeborne.selenide.impl.DurationFormat
import com.codeborne.selenide.logevents.LogEvent.EventStatus
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.Arrays
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

/**
 * Logs Selenide test steps and notifies all registered LogEventListener about it
 */
@ParametersAreNonnullByDefault
object SelenideLogger {
    private val LOG = LoggerFactory.getLogger(SelenideLogger::class.java)
    internal val listeners = ThreadLocal<MutableMap<String, LogEventListener>?>()
    private val df = DurationFormat()
    private val methodNameRegex = "([A-Z])".toRegex()

    /**
     * Add a listener (to the current thread).
     * @param name unique name of this listener (per thread).
     * Can be used later to remove listener using method [.removeListener]
     * @param listener event listener
     */
    @JvmStatic
    fun addListener(name: String, listener: LogEventListener) {
        var threadListeners = listeners.get()
        if (threadListeners == null) {
            threadListeners = HashMap()
        }
        threadListeners[name] = listener
        listeners.set(threadListeners)
    }

    @CheckReturnValue
    @JvmSynthetic
    fun beginStep(source: String, methodName: String, vararg args: Any?): SelenideLog {
        return if (args.isEmpty()) {
            beginStep(source, methodName)
        } else {
            beginStep(source, readableMethodName(methodName) + "(" + readableArguments(*args) + ")")
        }
    }

    @CheckReturnValue
    @JvmStatic
    fun beginStep(source: String, methodName: String, firstArg: Any?, vararg args: Any?): SelenideLog {
      val actualArgs = if (firstArg is Array<*> && args.isEmpty()) {
        firstArg
      } else if (firstArg == null && args.isEmpty()) {
        emptyArray()
      } else {
        arrayOf(firstArg, *args)
      }
      return beginStep(source, readableMethodName(methodName) + "(" + readableArguments(*actualArgs) + ")")
    }

    @JvmStatic
    @CheckReturnValue
    fun readableMethodName(methodName: String): String {
      return methodName.replace(methodNameRegex, " $1").toLowerCase()
    }

    @CheckReturnValue
    @JvmStatic
    fun readableArguments(vararg args: Any?): String {
        if (args.isNullOrEmpty()) {
            return ""
        }
        args[0]?.let {
          if (it is Array<*>) {
            return arrayToString(it as Array<Any>)
          }
        }
        return if (args[0] is IntArray) {
            arrayToString(args[0] as IntArray?)
        } else arrayToString(args)
    }

    @CheckReturnValue
    private fun arrayToString(args: Array<out Any?>): String {
        return if (args.size == 1) argToString(args[0]) else '[' + Stream.of(*args).map { obj: Any? -> argToString(obj) }
            .collect(Collectors.joining(", ")) + ']'
    }

    private fun argToString(arg: Any?): String {
        return if (arg is Duration) {
            df.format(arg)
        } else arg.toString()
    }

    @CheckReturnValue
    private fun arrayToString(args: IntArray?): String {
        return if (args!!.size == 1) args[0].toString() else Arrays.toString(args)
    }

    @JvmStatic
    @CheckReturnValue
    fun beginStep(source: String, subject: String): SelenideLog {
        val listeners = eventLoggerListeners
        val log = SelenideLog(source, subject)
        for (listener in listeners) {
            try {
                listener.beforeEvent(log)
            } catch (e: RuntimeException) {
                LOG.error("Failed to call listener {}", listener, e)
            }
        }
        return log
    }

    fun commitStep(log: SelenideLog, error: Throwable) {
      log.error = error
      commitStep(log, EventStatus.FAIL)
    }

    @JvmStatic
    fun commitStep(log: SelenideLog, status: EventStatus) {
      log.status = status
      val listeners = eventLoggerListeners
        for (listener in listeners) {
            try {
                listener.afterEvent(log)
            } catch (e: RuntimeException) {
                LOG.error("Failed to call listener {}", listener, e)
            }
        }
    }

    fun run(source: String, subject: String, runnable: Runnable) {
        val log = beginStep(source, subject)
        try {
            runnable.run()
            commitStep(log, EventStatus.PASS)
        } catch (e: RuntimeException) {
            commitStep(log, e)
            throw e
        } catch (e: Error) {
            commitStep(log, e)
            throw e
        }
    }

    operator fun <T> get(source: String, subject: String, supplier: Supplier<T>): T {
        val log = beginStep(source, subject)
        return try {
            val result = supplier.get()
            commitStep(log, EventStatus.PASS)
            result
        } catch (e: RuntimeException) {
            commitStep(log, e)
            throw e
        } catch (e: Error) {
            commitStep(log, e)
            throw e
        }
    }

    @get:CheckReturnValue
    private val eventLoggerListeners: Collection<LogEventListener>
        get() {
            if (listeners.get() == null) {
                listeners.set(HashMap())
            }
            return listeners.get()!!.values
        }

    /**
     * Remove listener (from the current thread).
     * @param name unique name of listener added by method [.addListener]
     * @param <T> class of listener to be returned
     * @return the listener being removed
    </T> */
    @JvmStatic
    fun <T : LogEventListener?> removeListener(name: String): T? {
        val listeners = listeners.get()
        return if (listeners == null) null else listeners.remove(name) as T?
    }

    @JvmStatic
    fun removeAllListeners() {
        listeners.remove()
    }

    /**
     * If listener with given name is bound (added) to the current thread.
     *
     * @param name unique name of listener added by method [.addListener]
     * @return true if method [.addListener] with
     * corresponding name has been called in current thread.
     */
    @JvmStatic
    fun hasListener(name: String): Boolean {
        val listeners: Map<String, LogEventListener>? = listeners.get()
        return listeners != null && listeners.containsKey(name)
    }
}
