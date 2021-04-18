package com.codeborne.selenide.impl

import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.Optional
import java.util.regex.Pattern
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class HttpHelper {
    @CheckReturnValue
    fun getFileNameFromContentDisposition(headers: Map<String, String?>): Optional<String> {
        return getFileNameFromContentDisposition(headers.entries)
    }

    @CheckReturnValue
    fun getFileNameFromContentDisposition(headers: Collection<Map.Entry<String, String?>>): Optional<String> {
        for ((key, value) in headers) {
            val fileName = getFileNameFromContentDisposition(key, value)
            if (fileName.isPresent) {
                return fileName
            }
        }
        return Optional.empty()
    }

    @CheckReturnValue
    fun getFileNameFromContentDisposition(headerName: String, headerValue: String?): Optional<String> {
        if (!"Content-Disposition".equals(headerName, ignoreCase = true) || headerValue == null) {
            return Optional.empty()
        }
        val regex = FILENAME_IN_CONTENT_DISPOSITION_HEADER.matcher(headerValue)
        if (!regex.matches()) {
            return Optional.empty()
        }
        val fileName = regex.replaceFirst("$3")
        val encoding = StringUtils.defaultIfEmpty(regex.replaceFirst("$2"), regex.replaceFirst("$5"))
        return Optional.of(decodeHttpHeader(fileName, encoding))
    }

    @CheckReturnValue
    private fun decodeHttpHeader(encoded: String, encoding: String): String {
        return try {
            URLDecoder.decode(encoded, StringUtils.defaultIfEmpty(encoding, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
    }

    @CheckReturnValue
    fun getFileName(url: String?): String {
        return normalize(trimQuery(FilenameUtils.getName(url)))
    }

    @CheckReturnValue
    private fun trimQuery(filenameWithQuery: String): String {
        return if (filenameWithQuery.contains("?")) StringUtils.left(
            filenameWithQuery,
            filenameWithQuery.indexOf('?')
        ) else filenameWithQuery
    }

    @CheckReturnValue
    fun normalize(fileName: String): String {
        return FILENAME_FORBIDDEN_CHARACTERS.matcher(fileName).replaceAll("_").replace(' ', '+')
    }

    companion object {
        private val FILENAME_IN_CONTENT_DISPOSITION_HEADER =
            Pattern.compile(".*filename\\*?=\"?((.+)'')?([^\";?]*)\"?(;charset=(.*))?.*", Pattern.CASE_INSENSITIVE)
        private val FILENAME_FORBIDDEN_CHARACTERS = Pattern.compile("[#%&{}/\\\\<>*?$!'\":@+`|=]")
    }
}
