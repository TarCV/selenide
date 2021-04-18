package com.codeborne.selenide.impl

import support.management.ManagementFactory
import support.System

open class FileNamer {
    /**
     * Creates a unique name for a file (to some extent).
     * Name starts with a current time, making it (more or less) easy to sort those files and find something.
     */
    open fun generateFileName(): String {
        return "${System.currentTimeMillis()}_${pid()}_${support.System.currentThreadId()}"
    }
    private fun pid(): String {
        return REGEX_MXBEAN_NAME.replaceFirst(ManagementFactory.getRuntimeMXBean().name, "$1")
    }

    companion object {
        private val REGEX_MXBEAN_NAME = kotlin.text.Regex("(.*)@.*")
    }
}
