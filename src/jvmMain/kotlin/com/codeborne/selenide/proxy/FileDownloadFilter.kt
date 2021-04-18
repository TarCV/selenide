package com.codeborne.selenide.proxy

import com.browserup.bup.filters.ResponseFilter
import com.browserup.bup.util.HttpMessageContents
import com.browserup.bup.util.HttpMessageInfo
import com.codeborne.selenide.Config
import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.impl.Downloader
import com.codeborne.selenide.impl.Downloads
import com.codeborne.selenide.impl.HttpHelper
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpResponse
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList

class FileDownloadFilter internal constructor(private val config: Config, private val downloader: Downloader) :
    ResponseFilter {
    private val httpHelper = HttpHelper()
    private var active = false
    private val downloads = Downloads()
    private val responses: MutableList<Response> = CopyOnWriteArrayList()

    constructor(config: Config) : this(config, Downloader()) {}

    /**
     * Activate this filter.
     * Starting from this moment, it will record all responses that seem to be a "file download".
     */
    fun activate() {
        reset()
        active = true
    }

    fun reset() {
        downloads.clear()
        responses.clear()
    }

    /**
     * Deactivate this filter.
     * Starting from this moment, it will not record any responses.
     */
    fun deactivate() {
        active = false
    }

    override fun filterResponse(response: HttpResponse, contents: HttpMessageContents, messageInfo: HttpMessageInfo) {
        if (!active) return
        val r = Response(
            messageInfo.url,
            response.status().code(),
            response.status().reasonPhrase(),
            toMap(response.headers()),
            contents.contentType,
            contents.textContents
        )
        responses.add(r)
        if (response.status().code() < 200 || response.status().code() >= 300) return
        val fileName = getFileName(r)
        val file = downloader.prepareTargetFile(config, fileName)
        try {
            FileUtils.writeByteArrayToFile(file, contents.binaryContents)
            downloads.add(DownloadedFile(file, r.headers))
        } catch (e: IOException) {
            log.error("Failed to save downloaded file to {} for url {}", file.absolutePath, messageInfo.url, e)
        }
    }

    private fun toMap(headers: HttpHeaders): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        for ((key, value) in headers) {
            map[key.toLowerCase()] = value
        }
        return map
    }

    /**
     * @return list of all downloaded files since activation.
     */
    fun downloads(): Downloads {
        return downloads
    }
    private fun getFileName(response: Response): String {
        return httpHelper.getFileNameFromContentDisposition(response.headers)
            .map { fileName: String -> httpHelper.normalize(fileName) }
            .orElseGet {
                log.info("Cannot extract file name from http headers. Found headers: ")
                for ((key, value) in response.headers) {
                    log.info("{}={}", key, value)
                }
                val fileNameFromUrl = httpHelper.getFileName(response.url)
                if (StringUtils.isNotBlank(fileNameFromUrl)) fileNameFromUrl else downloader.randomFileName()
            }
    }

    /**
     * @return all intercepted http response (as a string) - it can be useful for debugging
     */
    fun responsesAsString(): String {
        val sb = StringBuilder()
        sb.append("Intercepted ").append(responses.size).append(" responses:\n")
        var i = 0
        for (response in responses) {
            sb.append("  #").append(++i).append("  ").append(response).appendLine()
        }
        return sb.toString()
    }

        private class Response constructor(
        val url: String,
        private val code: Int,
        private val reasonPhrase: String,
        val headers: Map<String, String>,
        private val contentType: String,
        private val content: String
    ) {
        override fun toString(): String {
            return url + " -> " + code + " \"" + reasonPhrase + "\" " + headers + " " +
                    contentType + " " + " (" + content.length + " bytes)"
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(FileDownloadFilter::class)
    }
}
