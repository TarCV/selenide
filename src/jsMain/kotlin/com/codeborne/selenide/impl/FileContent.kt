package com.codeborne.selenide.impl

import okio.IOException
import java.nio.charset.StandardCharsets

/**
 * Read file content from classpath
 *
 * The point is in lazy loading: the content is loaded only on the first usage, and only once.
 */
class FileContent(private val filePath: String) {
    val content: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        try {
            val sizzleJs = checkNotNull(
              Thread.currentThread().contextClassLoader.getResource(
                filePath
              )
            )
            IOUtils.toString(sizzleJs, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            throw IllegalArgumentException("Cannot load $filePath from classpath", e)
        }
    }
}
