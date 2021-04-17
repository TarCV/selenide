package com.codeborne.selenide

import com.codeborne.selenide.files.FileFilter
import com.codeborne.selenide.files.FileFilters

class DownloadOptions private constructor(
    val method: FileDownloadMode,
    private val timeout: Long,
    val filter: FileFilter
) {
    fun getTimeout(defaultValue: Long): Long {
        return if (hasSpecifiedTimed()) timeout else defaultValue
    }

    private fun hasSpecifiedTimed(): Boolean {
        return timeout != UNSPECIFIED_TIMEOUT
    }

    fun withTimeout(timeout: Long): DownloadOptions {
        return DownloadOptions(method, timeout, filter)
    }

    fun withFilter(filter: FileFilter): DownloadOptions {
        return DownloadOptions(method, timeout, filter)
    }

    override fun toString(): String {
        return if (hasSpecifiedTimed() && !filter.isEmpty) String.format(
            "method: %s, timeout: %s ms, filter: %s",
            method,
            timeout,
            filter.description()
        ) else if (hasSpecifiedTimed()) String.format(
            "method: %s, timeout: %s ms",
            method,
            timeout
        ) else if (!filter.isEmpty) String.format(
            "method: %s, filter: %s",
            method,
            filter.description()
        ) else String.format("method: %s", method)
    }

    companion object {
        private const val UNSPECIFIED_TIMEOUT = Long.MIN_VALUE
        @JvmStatic
        fun using(method: FileDownloadMode): DownloadOptions {
            return DownloadOptions(method, UNSPECIFIED_TIMEOUT, FileFilters.none())
        }
    }
}
