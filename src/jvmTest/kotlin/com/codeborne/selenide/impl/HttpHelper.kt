package com.codeborne.selenide.impl

import org.apache.commons.io.FilenameUtils
import java.io.UnsupportedEncodingException
import support.URLDecoder

class HttpHelper {
    fun getFileNameFromContentDisposition(headers: Map<String, String?>): String? {
        return getFileNameFromContentDisposition(headers.entries)
    }
    fun getFileNameFromContentDisposition(headers: Collection<Map.Entry<String, String?>>): String? {
        for ((key, value) in headers) {
            val fileName = getFileNameFromContentDisposition(key, value)
            if (fileName.isPresent) {
                return fileName
            }
        }
        return null
    }
    fun getFileNameFromContentDisposition(headerName: String, headerValue: String?): String? {
        if (!"Content-Disposition".equals(headerName, ignoreCase = true) || headerValue == null) {
            return null
        }
        val regex = FILENAME_IN_CONTENT_DISPOSITION_HEADER.matcher(headerValue)
        if (!regex.matches()) {
            return null
        }
        val fileName = regex.replaceFirst("$3")
        val encoding = StringUtils.defaultIfEmpty(regex.replaceFirst("$2"), regex.replaceFirst("$5"))
        return (decodeHttpHeader(fileName, encoding))
    }
    private fun decodeHttpHeader(encoded: String, encoding: String): String {
        return try {
            URLDecoder.decode(encoded, StringUtils.defaultIfEmpty(encoding, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
    }
    fun getFileName(url: String?): String {
        return normalize(trimQuery(FilenameUtils.getName(url)))
    }
    private fun trimQuery(filenameWithQuery: String): String {
        return if (filenameWithQuery.contains("?")) StringUtils.left(
            filenameWithQuery,
            filenameWithQuery.indexOf('?')
        ) else filenameWithQuery
    }
    fun normalize(fileName: String): String {
        return FILENAME_FORBIDDEN_CHARACTERS.matcher(fileName).replaceAll("_").replace(' ', '+')
    }

    companion object {
        private val FILENAME_IN_CONTENT_DISPOSITION_HEADER =
            kotlin.text.Regex(".*filename\\*?=\"?((.+)'')?([^\";?]*)\"?(;charset=(.*))?.*", kotlin.text.Regex.CASE_INSENSITIVE)
        private val FILENAME_FORBIDDEN_CHARACTERS = kotlin.text.Regex("[#%&{}/\\\\<>*?$!'\":@+`|=]")
    }
}
