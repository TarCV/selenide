package com.codeborne.selenide.impl

import java.lang.management.ManagementFactory
import java.util.regex.Pattern
import javax.annotation.CheckReturnValue

open class FileNamer {
    /**
     * Creates a unique name for a file (to some extent).
     * Name starts with a current time, making it (more or less) easy to sort those files and find something.
     */
    @CheckReturnValue
    open fun generateFileName(): String {
        return String.format("%s_%s_%s", System.currentTimeMillis(), pid(), Thread.currentThread().id)
    }

    @CheckReturnValue
    private fun pid(): String {
        return REGEX_MXBEAN_NAME.matcher(ManagementFactory.getRuntimeMXBean().name).replaceFirst("$1")
    }

    companion object {
        private val REGEX_MXBEAN_NAME = Pattern.compile("(.*)@.*")
    }
}
